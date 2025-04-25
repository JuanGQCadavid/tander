package com.tander.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tander.user.dto.LoginDto;
import com.tander.user.dto.NotificationPreferenceDto;
import com.tander.user.dto.UserDto;
import com.tander.user.dto.UserRegistrationDto;
import com.tander.user.model.NotificationPreference;
import com.tander.user.model.User;
import com.tander.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    // TODO: reasonable exception types

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

    public UserDto registerUser(UserRegistrationDto registrationDtoDto) {
        if (userRepository.existsByEmail(registrationDtoDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(registrationDtoDto.getEmail())
                .password(registrationDtoDto.getPassword()) // TODO: hash
                .phoneNumber(registrationDtoDto.getPhoneNumber())
                .isVerified(false)
                .build();
        userRepository.save(user);
        log.info("User {} is added to DB", user.getId());
        return mapToUserDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        userRepository.save(user);
        log.info("User {} is updated", user.getId());
        return mapToUserDto(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User {} has been deleted", id);
    }

    public UserDto loginUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // TODO: resolve hash
        if (user.getPassword() != loginDto.getPassword()) {
            throw new RuntimeException("Invalid username or password");
        }

        return mapToUserDto(user);
    }

    public void updateNotificationPreferences(Long id, NotificationPreferenceDto notificationPreferenceDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        NotificationPreference notificationPreference = NotificationPreference.builder()
                .allowEmail(notificationPreferenceDto.isAllowEmail())
                .allowMessage(notificationPreferenceDto.isAllowMessage())
                .allowPush(notificationPreferenceDto.isAllowPush())
                .build();
        user.setNotificationPreference(notificationPreference);
        log.info("User {} notification preferences were updated", id);
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
