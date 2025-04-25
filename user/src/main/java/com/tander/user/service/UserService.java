package com.tander.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tander.user.dto.UserDto;
import com.tander.user.model.User;
import com.tander.user.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users.stream().map(this::mapToUserDto).toList();
    }

    public Optional<UserDto> getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::mapToUserDto);
    }

    public void registerUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // TODO: I think another DTO needs to be created to pass password
        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .isVerified(userDto.getIsVerified())
                .build();
        userRepository.save(user);
        log.info("User {} is added to DB", user.getId());
    }

    public void updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EntityExistsException("Email already registered");
        }

        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        userRepository.save(user);
        log.info("User {} is updated", user.getId());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User {} has been deleted", id);
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isVerified(user.getIsVerified())
                .build();
    }
}
