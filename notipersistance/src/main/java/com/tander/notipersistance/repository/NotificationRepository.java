package com.tander.notipersistance.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.notipersistance.model.NotificationDB;

public interface NotificationRepository extends CrudRepository<NotificationDB, String>{

    
} 