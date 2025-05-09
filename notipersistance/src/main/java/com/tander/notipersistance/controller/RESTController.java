package com.tander.notipersistance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.tander.notipersistance.model.NotificationDB;
import com.tander.notipersistance.service.NotificationService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;



@CrossOrigin(origins = "*")
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class RESTController {

    @Autowired
    private NotificationService service;

    @GetMapping("/history")
    public List<NotificationDB> getMethodName(@RequestHeader String userID) {
        return service.getNotifications(userID);
    }

    @PostMapping("/mark/{notificationID}")
    public void postMethodName(@PathVariable String notificationID, @RequestHeader String userID) {
        service.markNotification(notificationID, userID);
    }
    
    

}
