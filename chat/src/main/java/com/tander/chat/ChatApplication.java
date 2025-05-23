package com.tander.chat;

import java.time.OffsetDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.tander.chat.model.ChatUser;
import com.tander.chat.model.ChatUserKey;
import com.tander.chat.repository.ChatUserRepository;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) {
		DatabaseInitializer.initialize("chat_service");
		SpringApplication.run(ChatApplication.class, args);
	}

    @Bean
    public CommandLineRunner loadData(ChatUserRepository chatUserRepository) {
      	return args -> {
			if (chatUserRepository.findById(
					ChatUserKey.builder()
								.userId("juan")
								.chatid("chat_1")
								.build()
				).isEmpty()
			) {
				chatUserRepository.save(
					ChatUser.builder()
							.id(ChatUserKey.builder()
								.userId("juan")
								.chatid("chat_1")
								.build()
							)
							.dateOfJoining(OffsetDateTime.now())
							.build()
				);
			}

			if (chatUserRepository.findById(
					ChatUserKey.builder()
								.userId("gonzalo")
								.chatid("chat_1")
								.build()
				).isEmpty()
			) {
				chatUserRepository.save(
					ChatUser.builder()
							.id(ChatUserKey.builder()
								.userId("gonzalo")
								.chatid("chat_1")
								.build()
							)
							.dateOfJoining(OffsetDateTime.now())
							.build()
				);
			}
		};
     }
     

}
