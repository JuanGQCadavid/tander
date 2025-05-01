package com.tander.chat.repository;
import org.springframework.data.repository.CrudRepository;

import com.tander.chat.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, String>{
    
}
