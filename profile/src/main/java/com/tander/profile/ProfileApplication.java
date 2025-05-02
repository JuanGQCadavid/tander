package com.tander.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProfileApplication {

    public static void main(String[] args) {
        DatabaseInitializer.initialize("profileservice_db");
        SpringApplication.run(ProfileApplication.class, args);
    }

}
