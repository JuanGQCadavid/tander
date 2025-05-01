package com.tander.chat.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.chat.model.ChatUser;
import com.tander.chat.model.ChatUserKey;

public interface ChatUserRepository extends CrudRepository<ChatUser, ChatUserKey>{
    
}
