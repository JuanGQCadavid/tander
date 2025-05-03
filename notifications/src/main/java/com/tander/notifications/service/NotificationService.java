package com.tander.notifications.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tander.commons.model.payloads.MatchNotification;
import com.tander.notifications.controller.WebSocketHandler;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    @Autowired
    private final WebSocketHandler webSocketHandler;

    @Autowired
    private final SnsClient snsClient;

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
                sendMSM(userId, "You have a new Match! Check your matches on Tander!");
            }
        }
    }

    // TWILO
    public void sendMSM(String userId, String message) {
        log.info("Email to " + userId + " with " +message);

        // TODO -- Call to users backend
        Map<String, String> userPhones = new HashMap<String,String>() {};
        // userPhones.put("andre", "+37256503971");
        // userPhones.put("kristofer", "+3725022180");
        // userPhones.put("juan", "+37253956581");

        userPhones.put("1", "+37256503971");
        userPhones.put("2", "+3725022180");
        userPhones.put("3", "+37253956581");


        if (userPhones.getOrDefault(userId, "Nope").equals("Nope")){
            log.info("user not found! " + userId);
            return;
        }
        
        log.info("Message ID: " + publishMessage(userPhones.get(userId), message));
    }

    //SNS
    public String publishMessage(String phoneNumber, String message) {
        log.info("Message to " + phoneNumber);
        PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .build();

        PublishResponse result = snsClient.publish(request);
        return result.messageId(); // can log this if needed
    }
}
