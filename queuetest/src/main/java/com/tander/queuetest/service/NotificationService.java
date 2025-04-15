package com.tander.queuetest.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tander.commons.model.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, Notification> kafki;

    public void sendNotificationMessage(Notification notification){
        log.info("Send Notification: {} ", notification.toString());
        kafki.send("notification", notification);
    }
    
}
// String topic, K key, @Nullable V data