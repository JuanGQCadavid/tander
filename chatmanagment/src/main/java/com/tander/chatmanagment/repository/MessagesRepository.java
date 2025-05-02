package com.tander.chatmanagment.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.chatmanagment.model.Messages;
import java.util.List;


public interface MessagesRepository extends CrudRepository<Messages, String>{
    List<Messages> findByChatId(String chatId);    
}
