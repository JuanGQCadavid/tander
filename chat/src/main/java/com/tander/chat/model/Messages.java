package com.tander.chat.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("id")
    @Id
    private String id;
    
    @JsonProperty("chatId")
    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @JsonProperty("senderId")
    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @JsonProperty("textContent")
    @Column(name = "text_content", nullable = false)
    private String textContent;

    @JsonProperty("dateOfCreation")
    @Column(name = "date_of_creation", nullable = false)
    private OffsetDateTime dateOfCreation;
}
