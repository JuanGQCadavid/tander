package com.tander.chatmanagment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tander.chatmanagment.service.ChatManagmentService;
import com.tander.chatmanagment.dto.ChatDTO;
import com.tander.chatmanagment.dto.ChatUsersDTO;
import com.tander.chatmanagment.dto.MessagesDTO;
import com.tander.chatmanagment.exceptions.MatchException;
import com.tander.chatmanagment.security.SecurityUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "*")
@Slf4j
@RestController()
@RequestMapping("/api/chat")
@AllArgsConstructor
public class RESTController {

    private final SecurityUtil securityUtil;
    private final ChatManagmentService service;

    @GetMapping("/history/{chatId}")
    public List<MessagesDTO> getMethodName(
            @RequestParam(required = false) String fromId,
            @PathVariable String chatId,
            @RequestHeader String Authorization) {

        String userId = securityUtil.getUserIdLOrThrowError(Authorization);

        if (!service.isUserOnChat(userId, chatId)) {
            log.warn("UserId " + userId + " try to access chat id  " + chatId + " but he is not part of the gang");
            throw new MatchException(
                    "Hmmm, you are not part of the crew... why are you attempting to read from a chat you dont belong? hu?");
        }

        if (fromId == null) {
            return service.getMessages(chatId);
        }

        return service.getMessagesFromId(fromId, chatId);
    }

    @GetMapping("/")
    public List<ChatDTO> getChats(@RequestHeader String Authorization) {
        String userId = securityUtil.getUserIdLOrThrowError(Authorization);
        return service.getChats(userId);
    }

    @GetMapping("/members/{chatId}")
    public List<ChatUsersDTO> getChatMembers(@RequestHeader String Authorization, @PathVariable String chatId) {
        String userId = securityUtil.getUserIdLOrThrowError(Authorization);
        return service.getChatMembers(userId, chatId, Authorization);
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable String id) {
        service.deleteChat(id);
    }
}
