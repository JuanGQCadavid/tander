package com.tander.user.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.user.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
}
