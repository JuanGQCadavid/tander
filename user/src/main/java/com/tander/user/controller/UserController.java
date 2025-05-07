package com.tander.user.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tander.user.dto.LoginDto;
import com.tander.user.dto.NotificationPreferenceDto;
import com.tander.user.dto.TestDTO;
import com.tander.user.dto.UserDto;
import com.tander.user.dto.UserRegistrationDto;
import com.tander.user.service.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "http://localhost:8081/")
@RestController
@RequestMapping("/api/user")
public class UserController {
    // TODO: field validation
    // TODO: permissions
    // TODO: exception handling
    // TODO: HTTP responses

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Optional<UserDto> getUserById(@RequestParam Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/register")
    public UserDto registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        return userService.registerUser(userRegistrationDto);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/verify")
    public void requestVerificationCode(@RequestBody TestDTO userDto) {
        userService.requestVerificationCode(userDto);
    }

    @PutMapping("/{id}/notifications")
    public void updateNotificationPreferences(
            @PathVariable Long id,
            @RequestBody NotificationPreferenceDto notificationPreferenceDto) {
        userService.updateNotificationPreferences(id, notificationPreferenceDto);
    }
}
