package com.tander.notifications.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsumerService {
    @KafkaListener(topics = "notification", groupId = "on-live-updates-notifications")
    public void NotifyUser(){
        
    }
}
