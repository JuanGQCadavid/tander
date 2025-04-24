package com.tander.notifications.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tander.notifications.configurations.WebSocketConfig;
import com.tander.notifications.managers.SocketConnectionManager;
import com.tander.notifications.model.WebsocketCommands;
import com.tander.notifications.model.commands.AttachUserId;

// import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
// @AllArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    final SocketConnectionManager socketConnectionManager;
    final Map<WebsocketCommands, BiConsumer<WebSocketSession,TextMessage>> actions;

    WebSocketHandler(SocketConnectionManager socketConnectionManager){
        this.socketConnectionManager = socketConnectionManager;
        this.actions = new HashMap<>() {};

        actions.put(WebsocketCommands.ATTACH_USER_ID, this::cmdUserAttach);
    }


    private void cmdUserAttach(WebSocketSession session,  TextMessage message) {
        AttachUserId attachUserId = null;

        try {
            String json = message.getPayload();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            attachUserId = (AttachUserId) objectMapper.treeToValue(
                root.get("payload"), 
                WebsocketCommands.ATTACH_USER_ID.getCommandClass()
            );
        } catch (Exception e) {
            log.error("cmdUserAttach - Casting payload fail", e);
        }

        if (attachUserId == null) {
            log.warn("cmdUserAttach - Casting not possible.");
            return;
        }

        log.info("attachUserIds: " + attachUserId.getUserId());
    }
    
    /*
     * 
     * Connect and close connection.
     * 
     */

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        socketConnectionManager.addSession(session.getId(), session);
        log.info("New connection added: " + session.getId());
	}

    @Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        socketConnectionManager.removeSession(session.getId());
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


        for (WebsocketCommands cmds: this.actions.keySet()){
            if(cmds.getCmd().equals(cmd)) {
                log.info("Command: ", cmds.getCmd());
                this.actions.get(cmds).accept(session, message);
            }
        } 

        // if(cmd.equals("AttachUserId")) {
        //     String payload = root.get("payload").asText();
        //     log.info("AttachUserId: " + payload);
        //     AttachUserId attachUserId = objectMapper.treeToValue(root.get("payload"), AttachUserId.class);
        //     log.info("user id: " + attachUserId.getUserId());
        // }
 
        // WebsocketMessages websocketMessages = objectMapper.readValue(json, WebsocketMessages.class);

        // System.out.println("Command: " + websocketMessages.getCmd());
        // System.out.println("User ID from AttachUserId: " + websocketMessages.getAttachUserId().getUserId());

        session.sendMessage(new TextMessage("Echo: " + json));
	}

    @Override
	protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("Ping received: " + message.getPayload());
        session.sendMessage(new TextMessage("Pong"));
	}

    /*
     * 
     * Testing
     * 
     */

    public void sendMessageToSession(String sessionId, String message) throws Exception {
        Optional<WebSocketSession> sessionOptional = socketConnectionManager.getSessionBySessionId(sessionId);
        if (sessionOptional.isPresent()){
            log.info("Session founded");
            WebSocketSession session = sessionOptional.get();
            if (session != null && session.isOpen()) {
                log.info("Message sent");
                session.sendMessage(new TextMessage(message));
            }
        } else{
            log.warn("Session NOT founded", sessionId);
        }
    }

    public void sendMessageToUserId(String userId, String message) throws Exception {
        Optional<WebSocketSession> sessionOptional = socketConnectionManager.getSessionByUserId(userId);
        if (sessionOptional.isPresent()){
            log.info("Session founded");
            WebSocketSession session = sessionOptional.get();
            if (session != null && session.isOpen()) {
                log.info("Message sent");
                session.sendMessage(new TextMessage(message));
            }
        } else{
            log.warn("Session NOT founded for user id", userId);
        }
    }

	// @Override
	// public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	// }


}
