package com.tander.chatmanagment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tander.chatmanagment.model.Chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ChatDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("dateOfCreation")
    private String dateOfCreation;

    @JsonProperty("dateOfArchived")
    private String dateOfArchived;

    public static ChatDTO fromChat(Chat chat){
        log.info(chat.toString());
        String dateOfArchive = chat.getDateOfArchived() == null ? "" : chat.getDateOfArchived().toString();

        return ChatDTO.builder()
                    .id(chat.getId())
                    .isActive(chat.isActive())
                    .dateOfArchived(dateOfArchive)
                    .dateOfCreation(chat.getDateOfCreation().toString())
                    .build();
    }
    
}
