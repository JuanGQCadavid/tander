package com.tander.notifications.model;

import com.tander.notifications.model.payloads.EventNotification;
import com.tander.notifications.model.payloads.MatchNotification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    //Tander
    private MatchNotification matchNotification;

    // Side project
    private EventNotification eventNotificationPayload;
}
