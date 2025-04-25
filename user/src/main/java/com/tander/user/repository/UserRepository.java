package com.tander.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tander.user.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
