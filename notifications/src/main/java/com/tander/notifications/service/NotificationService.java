package com.tander.notifications.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tander.commons.model.payloads.MatchNotification;
import com.tander.notifications.controller.WebSocketHandler;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    @Autowired
    private final WebSocketHandler webSocketHandler;

    public void notifyMatch(MatchNotification matchNotification) {
        List<String> users = new ArrayList<String>();
        users.add(matchNotification.getUserIDA());
        users.add(matchNotification.getUserIDB());

        for (String userId: users) {
            boolean webSocket = webSocketHandler.isUserConnected(userId);

            if (webSocket) {
                try {
                    webSocketHandler.sendMessageToUserId(userId, "You have a new Match! Check your matchs");
                } catch (Exception e) {
                    log.error("We fail to send message notification to " + userId, e);
                    webSocket = false;
                }
            }
            
            if (!webSocket) {
                sendEmail(userId, "You have a new Match! Check your matchs on Tander!");
            }
        }
    }

    // TWILO
    public void sendEmail(String userId, String message) {
        log.info("Email to " + userId + " with " +message);

    }
}
