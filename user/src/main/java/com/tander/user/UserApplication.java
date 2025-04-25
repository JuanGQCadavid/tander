package com.tander.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        DatabaseInitializer.initialize("userservice_db");
        SpringApplication.run(UserApplication.class, args);
    }

}
