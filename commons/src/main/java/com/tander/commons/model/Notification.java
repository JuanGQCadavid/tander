package com.tander.commons.model;

import com.tander.commons.model.payloads.EventNotification;
import com.tander.commons.model.payloads.MatchNotification;
import com.tander.commons.model.payloads.VerificationCode;

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

    //Verifying
    private VerificationCode verificationCode;

}
