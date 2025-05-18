package com.tander.notifications.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tander.commons.model.payloads.MatchNotification;
import com.tander.commons.model.payloads.VerificationCode;
import com.tander.notifications.controller.WebSocketHandler;
import com.tander.notifications.dtos.UserDTO;

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
    private final RestTemplate restTemplate;

    @Autowired
    private final SnsClient snsClient;

    public void notifyVerification(VerificationCode nCode) {
        sendMSM(String.valueOf(nCode.getUserId()), "Your Tander verification code is: " + nCode.getCode());
    }

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

    public String getUserPhoneOld(String userId){
        Map<String, String> userPhones = new HashMap<String,String>() {};

        // userPhones.put("andre", "+37256503971");
        // userPhones.put("kristofer", "+3725022180");
        // userPhones.put("juan", "+37253956581");

        userPhones.put("1", "+37256503971");
        userPhones.put("2", "+3725022180");
        userPhones.put("3", "+37253956581");
        
        return userPhones.getOrDefault(userId, "Nope");
    }

    public String getUserPhoneCall(String userId){
        String url = "http://localhost:8003/api/user/"+userId; // TODO - What token should I use?
        UserDTO response = restTemplate.getForObject(url, UserDTO.class);
        return response != null ? response.getPhoneNumber() : null;
    }

    // TWILO
    public void sendMSM(String userId, String message) {
        log.info("Email to " + userId + " with " +message);

        String userphone = getUserPhoneCall(userId);
        log.info("----------");
        log.info("");
        log.info(userphone);

        if(userphone.isEmpty()){
            userphone = getUserPhoneOld(userId);
        }

        if (userphone.equals("Nope")){
            log.info("user not found! " + userId);
            return;
        }
        
        log.info("Message ID: " + publishMessage(userphone, message));
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
