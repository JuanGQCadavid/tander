package com.tander.queuetest.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
    @Bean
    public NewTopic notificationTopic(){
        return TopicBuilder
            .name("notification")
            .partitions(1)
            .build();
    }
    
}
