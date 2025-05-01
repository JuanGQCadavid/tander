package com.tander.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.tander.chat.managers.WebsocketConnectionsManager;
import com.tander.chat.model.cmd.AttachUserId;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ChatService {
    final WebsocketConnectionsManager websocketConnectionsManager;

    public void attachUsertoChat(AttachUserId payload, WebSocketSession session){
        // TODO - I need to validate here that the user exist on the relation chat User!
        log.info("Attaching user Id: " + payload.getUserId() + " to chat Id " + payload.getChatId());
        websocketConnectionsManager.attachSessionToChat(
            payload.getChatId(), 
            payload.getUserId(), 
            session
        );
    }

    public void removeSessionId(WebSocketSession session){
        websocketConnectionsManager.removeSessionFromChat(session);
    }
    
}
