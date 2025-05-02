package com.tander.chatmanagment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tander.chatmanagment.service.ChatManagmentService;
import com.tander.chatmanagment.dto.ChatDTO;
import com.tander.chatmanagment.dto.MessagesDTO;
import com.tander.chatmanagment.exceptions.MatchException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController()
@RequestMapping("/api/chat")
@AllArgsConstructor
public class RESTController {
    private final ChatManagmentService service;

    @GetMapping("/history/{chatId}")
    public List<MessagesDTO> getMethodName(
        @RequestParam(required = false) String fromId, 
        @PathVariable String chatId,
        @RequestHeader String userId
        ) {

            if (!service.isUserOnChat(userId, chatId)){
                log.warn("UserId " + userId + " try to access chat id  " + chatId + " but he is not part of the gang");
                throw new MatchException("Hmmm, you are not part of the crew... why are you attempting to read from a chat you dont belong? hu?");
            }

            if(fromId == null) {
                return service.getMessages(chatId);
            }

            return service.getMessagesFromId(fromId, chatId);
    }

    @GetMapping("/")
    public List<ChatDTO> getChats(@RequestHeader String userId) {
        return service.getChats(userId);
    }
    
    
}
