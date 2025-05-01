package com.tander.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tander.chat.model.Messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessagesDTO {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("chatId")
    private String chatId;

    @JsonProperty("senderId")
    private String senderId;

    @JsonProperty("textContent")
    private String textContent;

    @JsonProperty("dateOfCreation")
    private String dateOfCreation;

    public static MessagesDTO fromMessages(Messages msg) {
        return MessagesDTO.builder()
                            .id(msg.getId())
                            .chatId(msg.getChatId())
                            .senderId(msg.getSenderId())
                            .textContent(msg.getTextContent())
                            .dateOfCreation(msg.getDateOfCreation().toString())
                            .build();
    }
}
