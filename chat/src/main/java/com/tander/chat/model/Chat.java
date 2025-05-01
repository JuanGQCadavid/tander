package com.tander.chat.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="chat")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    private String id;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "date_of_creation")
    private OffsetDateTime dateOfCreation;

    @Column(name = "date_of_archived", nullable = true)
    private OffsetDateTime dateOfArchived;
}
