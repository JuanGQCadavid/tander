package com.tander.chat.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tander.chat.model.WebsocketCommands;
import com.tander.chat.model.cmd.AttachUserId;
import com.tander.chat.model.cmd.Message;
import com.tander.chat.service.ChatService;


import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    final ChatService chatService;
    final Map<WebsocketCommands, BiConsumer<WebSocketSession,TextMessage>> actions;

    public WebSocketHandler(ChatService chatService){
        this.chatService = chatService;
        this.actions = new HashMap<WebsocketCommands, BiConsumer<WebSocketSession,TextMessage>>();

        this.actions.put(WebsocketCommands.ATTACH_USER_ID, this::cmdUserAttach);
        this.actions.put(WebsocketCommands.NEW_MESSAGE, this::cmdNewMesssage);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> castpayload(TextMessage message, WebsocketCommands commands) {
        T payload = null;

        try {
            String json = message.getPayload();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            payload = (T) objectMapper.treeToValue(
                root.get("payload"), 
                commands.getCommandClass()
            );
        } catch (Exception e) {
            log.error("castpayload - Casting payload fail", e);
        }

        if (payload == null) {
            log.warn("castpayload - Casting not possible.");
            return Optional.empty();
        }

        return Optional.of(payload);
    }

    private void cmdUserAttach(WebSocketSession session,  TextMessage message) {
        Optional<AttachUserId> payload = castpayload(message, WebsocketCommands.ATTACH_USER_ID);

        if(!payload.isPresent()){
            log.info("AttachUserId is empy!");
            this.sendMessageBack(session, "payload needs to have userId and chatId");
            return;
        }

        chatService.attachUsertoChat(payload.get(), session);        
    }


    private void cmdNewMesssage(WebSocketSession session, TextMessage message) {
        Optional<Message> payload = castpayload(message, WebsocketCommands.NEW_MESSAGE);

        if(!payload.isPresent()){
            log.info("New Message is empy!");
            this.sendMessageBack(session, "payload needs to have textContent");
            return;
        }

        this.chatService.newMessage(payload.get(), session);
    }

    private void sendMessageBack(WebSocketSession session, String message){
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            log.error("We fail to reply back to session id " + session.getId(), e);
        }
        
    }

    /*
     * 
     * Connect and close connection.
     * 
     */

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("New connection added: " + session.getId());
	}

    @Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.chatService.removeSessionId(session);
        log.info("Connection removed: " + session.getId());
	}

    /*
    * 
    * Message handling
    * 
    */

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String json = message.getPayload();
        log.info("Received message: " + json);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = objectMapper.readTree(json);
        String cmd = root.get("cmd").asText();

        for(WebsocketCommands cmds: this.actions.keySet()) {
            if(cmds.getCmd().equals(cmd)){
                log.info("Command: ", cmds.getCmd());
                this.actions.get(cmds).accept(session, message);
            }
        }
    }

}
