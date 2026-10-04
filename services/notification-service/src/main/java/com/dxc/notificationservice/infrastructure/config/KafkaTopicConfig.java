package com.dxc.notificationservice.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic tenantEventsTopic() {
        return TopicBuilder.name("tenant-events").build();
    }

    @Bean
    public NewTopic projectEventsTopic() {
        return TopicBuilder.name("project-events").build();
    }

    @Bean
    public NewTopic taskEventsTopic() {
        return TopicBuilder.name("task-events").build();
    }

    @Bean
    public NewTopic crmEventsTopic() {
        return TopicBuilder.name("crm-events").build();
    }
}
