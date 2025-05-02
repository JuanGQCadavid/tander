package com.tander.chatmanagment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import com.tander.chatmanagment.service.ChatManagmentService;
import com.tander.commons.model.Notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
public class KafkaController {
    
    @Autowired
    private final ChatManagmentService service;

    @KafkaListener(topics = "chat", groupId = "chat-srv-managment")
    public void newEvent(Notification notification){
        log.info("Event arrived!");

        if(notification.getMatchNotification() != null ){
            log.info("Match notification" + notification.getMatchNotification());
            service.openChat(notification.getMatchNotification());
        } else {
            log.info("Hmmmm no idea what arrived");
        }
        try{
            log.info(notification.toString());
        } finally {
            log.info("Yop");
        }
    }


}
