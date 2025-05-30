package com.tander.notipersistance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotipersistanceApplication {

	public static void main(String[] args) {
		DatabaseInitializer.initialize("notifications_service");
		SpringApplication.run(NotipersistanceApplication.class, args);
	}

}
