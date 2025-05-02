package com.tander.chatmanagment.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.chatmanagment.model.ChatUser;
import com.tander.chatmanagment.model.ChatUserKey;

public interface ChatUserRepository extends CrudRepository<ChatUser, ChatUserKey>{
    
}
