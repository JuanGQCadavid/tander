package com.tander.chat.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages", indexes = @Index( columnList = "chat_id"))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Messages {

    @Id
    private String id;
    
    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "text_content", nullable = false)
    private String textContent;

    @Column(name = "date_of_creation", nullable = false)
    private OffsetDateTime dateOfCreation;
}
