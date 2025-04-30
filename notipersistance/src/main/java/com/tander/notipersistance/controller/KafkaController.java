package com.tander.notipersistance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import com.tander.commons.model.Notification;
import com.tander.notipersistance.service.NotificationService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Controller
public class KafkaController {

    @Autowired
    private NotificationService service;

    @KafkaListener(topics = "notification", groupId = "persistance-notifications")
    public void newNotification(Notification notification) {
        log.info("Notification arrived!");

        if(notification.getEventNotificationPayload() != null ){
            log.info("Event notification" + notification.getEventNotificationPayload());
        }else if(notification.getMatchNotification() != null ){
            log.info("Match notification" + notification.getMatchNotification());
            service.saveNotifcation(notification); // TODO if this fail, then we should return the notification to the queue
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
