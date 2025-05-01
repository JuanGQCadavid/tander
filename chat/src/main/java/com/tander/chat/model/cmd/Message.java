package com.tander.chat.model.cmd;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @JsonProperty("textCotent")
    private String textContent;

    @JsonProperty("chatId")
    private String chatId;
}
