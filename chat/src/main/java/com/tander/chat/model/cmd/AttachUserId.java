package com.tander.chat.model.cmd;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachUserId {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("chatId")
    private String chatId;
}
