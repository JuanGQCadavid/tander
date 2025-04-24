package com.tander.notifications.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class HttpService {
    
    @Autowired
    private final WebSocketHandler webSocketHandler;

    @PostMapping("/send/{sessionId}")
    public String sendMessage(@PathVariable String sessionId, @RequestBody String message) {
        try {
            webSocketHandler.sendMessageToSession(sessionId, message);
            return "Message sent.";
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }

    @PostMapping("/send/user/{userId}")
    public String sendMessageToUserId(@PathVariable String userId, @RequestBody String message) {
        try {
            webSocketHandler.sendMessageToUserId(userId, message);
            return "Message sent.";
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }
    
}
