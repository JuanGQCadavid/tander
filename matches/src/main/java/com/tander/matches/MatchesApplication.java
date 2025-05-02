package com.tander.matches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchesApplication {

    public static void main(String[] args) {
        DatabaseInitializer.initialize("matchesservice_db");
        SpringApplication.run(MatchesApplication.class, args);
    }

}
