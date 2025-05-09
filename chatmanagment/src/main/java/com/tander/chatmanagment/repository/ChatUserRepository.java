package com.tander.chatmanagment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tander.chatmanagment.model.ChatUser;
import com.tander.chatmanagment.model.ChatUserKey;
import java.util.List;


public interface ChatUserRepository extends CrudRepository<ChatUser, ChatUserKey>{
    // List<ChatUser> findByIdUserId(String userId);

    @Query("SELECT cu FROM ChatUser cu WHERE cu.id.userId = :userId")
    List<ChatUser> findByUserId(@Param("userId") String userId);

    @Query("SELECT cu FROM ChatUser cu WHERE cu.id.chatid = :chatId")
    List<ChatUser> findMembersByChatId(@Param("chatId") String chatId);
}
