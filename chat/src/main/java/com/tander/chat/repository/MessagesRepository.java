package com.tander.chat.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.chat.model.Messages;

public interface MessagesRepository extends CrudRepository<Messages, String>{
    
}
