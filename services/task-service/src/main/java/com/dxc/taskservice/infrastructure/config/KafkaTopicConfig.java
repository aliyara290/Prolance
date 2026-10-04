package com.dxc.taskservice.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic taskEventsTopic() {
        return TopicBuilder.name("task-events").build();
    }
}
