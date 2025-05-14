package com.tander.user.controller;

import java.util.Optional;

import com.tander.user.exception.AuthException;
import com.tander.user.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    // TODO: field validation
    // TODO: permissions
    // TODO: exception handling
    // TODO: HTTP responses

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/{id}")
    public Optional<UserDto> getUserById(@PathVariable Long id) {
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
    public String login(@RequestBody LoginDto loginDto) {
        log.info("Logging in has started: {}", loginDto);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            log.info("authentication result: {}", authentication);
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(loginDto.getEmail());
                log.info("JWT token: {}", token);
                return token;
            } else {
                log.error("Auth has failed :(");
                throw new AuthException("Auth has failed");
            }
        } catch (Exception e) {
            log.error("Ming error: {}", e.getMessage());
            throw new AuthException("Auth has failed.......");
        }
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
