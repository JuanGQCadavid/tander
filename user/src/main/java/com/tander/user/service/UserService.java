package com.tander.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tander.commons.model.Notification;
import com.tander.commons.model.payloads.VerificationCode;
import com.tander.user.dto.LoginDto;
import com.tander.user.dto.NotificationPreferenceDto;
import com.tander.user.dto.TestDTO;
import com.tander.user.dto.UserDto;
import com.tander.user.dto.UserRegistrationDto;
import com.tander.user.exception.AuthException;
import com.tander.user.exception.ResourceAlreadyExistsException;
import com.tander.user.exception.UserNotFoundException;
import com.tander.user.model.NotificationPreference;
import com.tander.user.model.User;
import com.tander.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final KafkaTemplate<String, Notification> kafki;

    public List<UserDto> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users.stream().map(this::mapToUserDto).toList();
    }

    public Optional<UserDto> getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::mapToUserDto);
    }

    public void requestVerificationCode(TestDTO userDto) {
        Notification notification = new Notification();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode("090909");
        verificationCode.setUserId(userDto.getId());
        notification.setVerificationCode(verificationCode);

        kafki.send("notification", notification);
    }

    public UserDto registerUser(UserRegistrationDto registrationDtoDto) {
        if (userRepository.existsByEmail(registrationDtoDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        User user = User.builder()
                .email(registrationDtoDto.getEmail())
                .password(registrationDtoDto.getPassword()) // TODO: hash
                .phoneNumber(registrationDtoDto.getPhoneNumber())
                .isVerified(false)
                .build();

        userRepository.save(user);

        Notification notification = new Notification();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode("090909");
        verificationCode.setUserId(user.getId());
        notification.setVerificationCode(verificationCode);

        kafki.send("notification", notification);

        log.info("User {} is added to DB", user.getId());
        return mapToUserDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
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

    public UserDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new AuthException("Invalid username or password"));

        // TODO: resolve hash
        if (user.getPassword() != loginDto.getPassword()) {
            throw new AuthException("Invalid username or password");
        }

        return mapToUserDto(user);
    }

    public void updateNotificationPreferences(Long id, NotificationPreferenceDto notificationPreferenceDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

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
