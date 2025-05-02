package com.tander.chatmanagment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tander.chatmanagment.model.Messages;
import java.util.List;


public interface MessagesRepository extends CrudRepository<Messages, String>{
    List<Messages> findByChatId(String chatId);    

    @Query("SELECT ms FROM Messages ms WHERE ms.chatId = :chatId and ms.dateOfCreation > ( SELECT inerMS.dateOfCreation FROM Messages inerMS WHERE inerMS.id = :messageId) order by ms.dateOfCreation desc")
    List<Messages> findMessagesAfterMessageId(String chatId, String messageId); 
}
