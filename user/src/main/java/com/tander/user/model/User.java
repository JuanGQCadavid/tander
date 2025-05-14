package com.tander.user.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "usertable")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String phoneNumber;
    private String password;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private String role;

    @Embedded
    private NotificationPreference notificationPreference;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
