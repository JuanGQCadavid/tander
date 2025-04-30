package com.tander.notipersistance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDB {

    @Id
    private String id;
    private String notificationCorrelation;
    private String userId;

    private String userMatchName; // Probably this will be deleted.
    private String userMatchId;

    private String dateOfMatch;
    private boolean checked;

    public NotificationDB(String id){
        this.id = id;
    }
}
