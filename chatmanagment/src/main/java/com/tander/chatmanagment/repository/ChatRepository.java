package com.tander.chatmanagment.repository;
import org.springframework.data.repository.CrudRepository;

import com.tander.chatmanagment.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, String>{
}
