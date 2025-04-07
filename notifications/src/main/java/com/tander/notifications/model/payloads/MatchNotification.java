package com.tander.notifications.model.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchNotification {

    private String userNameA;
    private String userIDA;

    private String userNameB;
    private String userIDB;

    private String dateOfMatch;
}
