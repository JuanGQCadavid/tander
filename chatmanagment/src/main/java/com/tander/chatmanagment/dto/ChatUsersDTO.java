package com.tander.chatmanagment.dto;

import java.util.List;

import com.tander.chatmanagment.model.ChatUser;

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
public class ChatUsersDTO {
    private String chatId;
    private String userId;
    private String userName;
    private String email;

    public static ChatUsersDTO fromChatUser(ChatUser ch){
        return ChatUsersDTO.builder()
                            .chatId(ch.getId().getChatid())
                            .userId(ch.getId().getUserId())
                            .build();
    }
}
