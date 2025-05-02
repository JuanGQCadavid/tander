package com.tander.chatmanagment.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="chatUser")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatUser {

    @EmbeddedId
    private ChatUserKey id;

    @Column(name = "date_of_joining")
    private OffsetDateTime dateOfJoining;
    
}
