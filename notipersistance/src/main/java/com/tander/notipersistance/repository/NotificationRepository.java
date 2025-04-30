package com.tander.notipersistance.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.notipersistance.model.NotificationDB;
import java.util.List;



public interface NotificationRepository extends CrudRepository<NotificationDB, String>{
    List<NotificationDB> findByUserId(String userId);
} 