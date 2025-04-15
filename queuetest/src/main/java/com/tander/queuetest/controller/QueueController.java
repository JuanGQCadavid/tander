package com.tander.queuetest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.tander.queuetest.model.Notification;
import com.tander.queuetest.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/notification")
public class QueueController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/publish")
    public ResponseEntity<String> postMethodName(@RequestBody Notification notification) {    
        notificationService.sendNotificationMessage(notification);    
        return ResponseEntity.ok("Oki oki");
    }
    
}
