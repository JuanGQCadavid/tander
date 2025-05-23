package com.tander.notifications.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tander.notifications.managers.SocketConnectionManager;
import com.tander.notifications.model.WebsocketCommands;
import com.tander.notifications.model.commands.AttachUserId;
import com.tander.notifications.security.SecurityUtil;

// import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
// @AllArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    final SocketConnectionManager socketConnectionManager;
    final Map<WebsocketCommands, BiConsumer<WebSocketSession,TextMessage>> actions;
    private final SecurityUtil securityUtil;

    WebSocketHandler(SocketConnectionManager socketConnectionManager, SecurityUtil securityUtil){
        this.socketConnectionManager = socketConnectionManager;
        this.securityUtil = securityUtil;
        this.actions = new HashMap<>() {};

        actions.put(WebsocketCommands.ATTACH_USER_ID, this::cmdUserAttach);
    }

    public boolean isUserConnected(String userId) {
        Optional<WebSocketSession>  session = socketConnectionManager.getSessionByUserId(userId);

        if(!session.isPresent()) {
            return false;
        }

        return true;
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
        Optional<AttachUserId> attachUserId = castpayload(message, WebsocketCommands.ATTACH_USER_ID);

        if(!attachUserId.isPresent()){
            log.info("attachUserIds is empy!");
            return;
        }
        AttachUserId attachUserId2 = attachUserId.get();
        attachUserId2.setUserId(securityUtil.getUserIdLOrThrowError(attachUserId2.getUserId()));
        log.info("attachUserIds: " + attachUserId2.getUserId());
        socketConnectionManager.attachSessionToUserId(session.getId(), attachUserId2.getUserId());

        try {
            session.sendMessage(new TextMessage("OK"));
        } catch (Exception e) {
            log.error("We fail to reply back", e);
        }
        
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
