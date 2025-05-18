package com.tander.chatmanagment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
// @EnableDiscoveryClient
public class ChatmanagmentApplication {

	public static void main(String[] args) {
		DatabaseInitializer.initialize("chat_service");
		SpringApplication.run(ChatmanagmentApplication.class, args);
	}

}
