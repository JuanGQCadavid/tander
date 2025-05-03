package com.tander.notifications.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tander.commons.model.Notification;
import com.tander.notifications.service.NotificationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ConsumerService {

    @Autowired
    final NotificationService notificationService;

    @KafkaListener(topics = "notification", groupId = "on-live-updates-notifications")
    public void NotifyUser(Notification notification){
        log.info("Noti arrived!");

        if(notification.getEventNotificationPayload() != null ){
            log.info("Event notification" + notification.getEventNotificationPayload());
        }else if(notification.getMatchNotification() != null ){
            log.info("Match notification" + notification.getMatchNotification());
            notificationService.notifyMatch(notification.getMatchNotification()); // TODO if this fail, then we should return the notification to the queue
        }else if(notification.getVerificationCode() != null){
            log.info("Verification notification" + notification.getVerificationCode());
            notificationService.notifyVerification(notification.getVerificationCode());
        }
        else {
            log.info("Hmmmm no idea what arrived");
        }
        try{
            log.info(notification.toString());
        } finally {
            log.info("Yop");
        }
        
    }
}
