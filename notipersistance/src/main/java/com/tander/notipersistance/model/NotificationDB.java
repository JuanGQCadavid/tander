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

    private String userNameA;
    private String userIDA;
    private String userNameB;
    private String userIDB;
    private String dateOfMatch;

    public NotificationDB(String id){
        this.id = id;
    }
}
