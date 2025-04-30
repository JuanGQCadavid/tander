package com.tander.notipersistance.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tander.commons.model.Notification;
import com.tander.notipersistance.exceptions.NotificationException;
import com.tander.notipersistance.model.NotificationDB;
import com.tander.notipersistance.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

    private final NewTopic notificationTopic;

    @Autowired
    private NotificationRepository repository;

    NotificationService(NewTopic notificationTopic) {
        this.notificationTopic = notificationTopic;
    }

    public void saveNotifcation(Notification notification){
        String id = UUID.randomUUID().toString();
        log.info("Saving notification " + id);

        repository.save(NotificationDB.builder()
                .id(UUID.randomUUID().toString())
                .notificationCorrelation(id)
                .dateOfMatch(notification.getMatchNotification().getDateOfMatch())

                .userId(notification.getMatchNotification().getUserIDA())

                .userMatchName(notification.getMatchNotification().getUserNameB())
                .userMatchId(notification.getMatchNotification().getUserIDB())
                
                .checked(false)
                
                .build()
        );

        repository.save(NotificationDB.builder()
                .id(UUID.randomUUID().toString())
                .notificationCorrelation(id)
                .dateOfMatch(notification.getMatchNotification().getDateOfMatch())

                .userId(notification.getMatchNotification().getUserIDB())
                .userMatchName(notification.getMatchNotification().getUserNameA())
                .userMatchId(notification.getMatchNotification().getUserIDA())
                
                .checked(false)

                .build()
        );
    }

    public List<NotificationDB> getNotifications(String userId){
        return repository.findByUserId(userId);
    }


    public void markNotification(String notificationId, String userId) {
        Optional<NotificationDB> notification = repository.findById(notificationId);

        if(notification.isEmpty()){
            throw new NotificationException("Notification id " + notificationId + " Does not exist");
        }

        NotificationDB notificationGet = notification.get();

        if (!notificationGet.getUserId().equals(userId)) {
            throw new NotificationException("Notification id " + notificationId + " is not yours... hmmm suspicious");
        }

        if (notificationGet.isChecked()){
            log.info("Notifcation " + notificationId + " is already check, so aborting update process");
            return;
        }

        notificationGet.setChecked(true);
        repository.save(notificationGet);
    }
}
