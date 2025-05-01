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
		SpringApplication.run(ChatApplication.class, args);
	}

	// https://open.spotify.com/track/7xFy1kfgGWJWTpx1vSHBLi?si=579d57cdebd94581

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
