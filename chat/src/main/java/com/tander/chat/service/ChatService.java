package com.tander.chat.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.tander.chat.managers.WebsocketConnectionsManager;
import com.tander.chat.model.ChatUser;
import com.tander.chat.model.ChatUserKey;
import com.tander.chat.model.cmd.AttachUserId;
import com.tander.chat.repository.ChatUserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ChatService {

    final WebsocketConnectionsManager websocketConnectionsManager;
    final ChatUserRepository chatUserRepository;

    public void attachUsertoChat(AttachUserId payload, WebSocketSession session){
        Optional<ChatUser>  chatuser = chatUserRepository.findById(
            ChatUserKey.builder()
                        .userId(payload.getUserId())
                        .chatid(payload.getChatId())
                        .build()
        );

        if (chatuser.isEmpty()){
            this.sendMessageBack(session, "Well, this is embarrassing... you dont belong to that chat, sorry mate");
            return;
        }

        log.info("Attaching user Id: " + payload.getUserId() + " to chat Id " + payload.getChatId());
        websocketConnectionsManager.attachSessionToChat(
            payload.getChatId(), 
            payload.getUserId(), 
            session
        );

        this.sendMessageBack(session, "Oks");
    }

    public void removeSessionId(WebSocketSession session){
        websocketConnectionsManager.removeSessionFromChat(session);
    }

    private void sendMessageBack(WebSocketSession session, String message){
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            log.error("We fail to reply back to session id " + session.getId(), e);
        }
        
    }
    
}
