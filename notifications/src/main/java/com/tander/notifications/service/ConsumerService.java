package com.tander.notifications.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tander.notifications.model.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsumerService {
    @KafkaListener(topics = "notification", groupId = "on-live-updates-notifications")
    public void NotifyUser(Notification notification){
        log.info("Noti arrived!");

        if(notification.getEventNotificationPayload() != null ){
            log.info("Event notification" + notification.getEventNotificationPayload());
        }else if(notification.getMatchNotification() != null ){
            log.info("Match notification" + notification.getMatchNotification());
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
