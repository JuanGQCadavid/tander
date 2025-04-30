package com.tander.notipersistance.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tander.commons.model.Notification;
import com.tander.notipersistance.model.NotificationDB;
import com.tander.notipersistance.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    public void saveNotifcation(Notification notification){
        String id = UUID.randomUUID().toString();
        log.info("Saving notification " + id);

        repository.save(NotificationDB.builder()
                .id(id)
                .dateOfMatch(notification.getMatchNotification().getDateOfMatch())
                .userNameA(notification.getMatchNotification().getUserNameA())
                .userIDA(notification.getMatchNotification().getUserIDA())
                .userNameB(notification.getMatchNotification().getUserNameB())
                .userIDB(notification.getMatchNotification().getUserIDB())
                .build()
        );
    }

}
