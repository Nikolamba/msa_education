package ru.kalampus.RequestService.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${topic-name}")

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name("requests")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
