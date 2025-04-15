package com.tander.queuetest.model;

import com.tander.queuetest.model.payloads.EventNotification;
import com.tander.queuetest.model.payloads.MatchNotification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private String userId;

    //Tander
    private MatchNotification matchNotification;

    // Side project
    private EventNotification eventNotificationPayload;
}
