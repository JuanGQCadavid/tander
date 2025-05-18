package com.tander.chatmanagment.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.tander.chatmanagment.exceptions.MatchException;

import com.tander.chatmanagment.dto.ChatDTO;
import com.tander.chatmanagment.dto.ChatUsersDTO;
import com.tander.chatmanagment.dto.MessagesDTO;
import com.tander.chatmanagment.model.Chat;
import com.tander.chatmanagment.model.ChatUser;
import com.tander.chatmanagment.model.ChatUserKey;
import com.tander.chatmanagment.repository.ChatRepository;
import com.tander.chatmanagment.repository.ChatUserRepository;
import com.tander.chatmanagment.repository.MessagesRepository;
import com.tander.commons.model.payloads.MatchNotification;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ChatManagmentService {
    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final MessagesRepository messagesRepository;
    private final RestTemplate restTemplate;

    public List<MessagesDTO> getMessagesFromId(String lastMessageId, String chatId) {
        return messagesRepository.findMessagesAfterMessageId(chatId, lastMessageId)
                .stream()
                .map(chat -> MessagesDTO.fromMessages(chat))
                .collect(Collectors.toList());
    }

    public List<ChatDTO> getChats(String userId) {
        log.info(userId);
        return chatUserRepository.findByUserId(userId)
                .stream()
                .map(cu -> chatRepository.findById(cu.getId().getChatid()))
                .filter(chOptional -> chOptional.isPresent())
                .map(chOptional -> chOptional.get())
                .map(chat -> ChatDTO.fromChat(chat))
                .collect(Collectors.toList());
    }

    public List<ChatUsersDTO> getChatMembers(String userId, String chatId, String authorization) {
        log.info(userId);
        List<ChatUsersDTO> noFiltered = chatUserRepository.findMembersByChatId(chatId)
                .stream()
                .map(chatUser -> ChatUsersDTO.fromChatUser(chatUser))
                .map(dto -> addUserAttributes(dto, authorization))
                .collect(Collectors.toList());

        List<ChatUsersDTO> filtered = noFiltered
                .stream()
                .filter(user -> !user.getUserId().equals(userId))
                .collect(Collectors.toList());

        if (filtered.size() == noFiltered.size()) {
            throw new MatchException(
                    "Hmmm, you are not part of the crew... why are you attempting to read from a chat you dont belong? hu?");
        }
        return filtered;
    }

    public ChatUsersDTO addUserAttributes(ChatUsersDTO dto, String authorization) {

        String url = "http://localhost:8003/api/user/" + dto.getUserId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        HttpEntity<String> entity = new HttpEntity(headers);
        
        ResponseEntity<ChatUsersDTO> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            ChatUsersDTO.class
        );


        log.info(url);
        log.info(response.toString());
        dto.setEmail(response.getBody().getEmail());
        return dto;
    }

    public List<MessagesDTO> getMessages(String chatId) {
        return messagesRepository.findByChatId(chatId)
                .stream()
                .map(msg -> MessagesDTO.fromMessages(msg))
                .collect(Collectors.toList());
    }

    public boolean isUserOnChat(String userId, String chatId) {
        Optional<ChatUser> user = chatUserRepository.findById(
                ChatUserKey.builder()
                        .chatid(chatId)
                        .userId(userId)
                        .build());

        if (user.isPresent() &&
                user.get().getId().getUserId().equals(userId) &&
                user.get().getId().getChatid().equals(chatId)) {
            return true;
        }

        return false;
    }

    public void openChat(MatchNotification notification) {
        String chatId = UUID.randomUUID().toString();
        OffsetDateTime timeOf = OffsetDateTime.now();

        log.info("Creating chat with id " + chatId);
        chatRepository.save(
                Chat.builder()
                        .id(chatId)
                        .dateOfCreation(timeOf)
                        .isActive(true)
                        .build());

        log.info("Adding user with id " + notification.getUserIDA());
        chatUserRepository.save(
                ChatUser.builder()
                        .dateOfJoining(timeOf)
                        .id(
                                ChatUserKey.builder()
                                        .chatid(chatId)
                                        .userId(notification.getUserIDA())
                                        .build())
                        .build());

        log.info("Adding user with id " + notification.getUserIDB());
        chatUserRepository.save(
                ChatUser.builder()
                        .dateOfJoining(timeOf)
                        .id(
                                ChatUserKey.builder()
                                        .chatid(chatId)
                                        .userId(notification.getUserIDB())
                                        .build())
                        .build());
    }

    public void deleteChat(String id) {
        log.info("Deleting chat with id " + id);
        chatRepository.deleteById(id);
    }
}
