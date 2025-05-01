package com.tander.chat.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tander.chat.dto.MessagesDTO;
import com.tander.chat.managers.WebsocketConnectionsManager;
import com.tander.chat.model.ChatUser;
import com.tander.chat.model.ChatUserKey;
import com.tander.chat.model.Messages;
import com.tander.chat.model.cmd.AttachUserId;
import com.tander.chat.model.cmd.Message;
import com.tander.chat.repository.ChatUserRepository;
import com.tander.chat.repository.MessagesRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ChatService {

    final WebsocketConnectionsManager websocketConnectionsManager;
    final ChatUserRepository chatUserRepository;
    final MessagesRepository messagesRepository;

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

    public void newMessage(Message payload, WebSocketSession session){
        List<WebSocketSession> sessions = websocketConnectionsManager.getSessionsFromChatId(payload.getChatId());

        if(sessions == null || sessions.isEmpty()) {
            sendMessageBack(session, "That chat id does not exist dude");
            return;
        }

        
        if (sessions.stream().filter(ses -> ses.getId().equals(session.getId())).findFirst().isEmpty()) {
            sendMessageBack(session, "Go attempt, it seems some one does not respect boundaries..");
            return;
        }
        
        String sender = websocketConnectionsManager.getUserIdFromChatSession(payload.getChatId(),session.getId()).get();

        Messages msg = Messages.builder()
                                .id(UUID.randomUUID().toString())
                                .chatId(payload.getChatId())
                                .senderId(sender)
                                .dateOfCreation(OffsetDateTime.now())
                                .textContent(payload.getTextContent())
                                .build();

        messagesRepository.save(msg);

        MessagesDTO messagesDTO = MessagesDTO.fromMessages(msg);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("cmd", "newMessage");
        root.put("payload", objectMapper.valueToTree(messagesDTO));

        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);

            sessions.stream()
            .filter(ses -> !ses.getId().equals(session.getId()))
            .forEach(ses -> {
                sendMessageBack(ses, json);
            });


        } catch (Exception e) {
            log.error("Shit, we fail to transform to json....", e);
            return;
        }
    }

    public void removeSessionId(WebSocketSession session){
        websocketConnectionsManager.removeSessionFromChat(session);
    }

    private void sendMessageBack(WebSocketSession session, String message){
        if(!session.isOpen()){
            log.info("Session was closed, removing it from the list "+ session.getId());
            websocketConnectionsManager.removeSessionFromChat(session);
            return;
        }

        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            log.error("We fail to reply back to session id " + session.getId(), e);
        }
        
    }
    
}
