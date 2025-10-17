package com.charllson.interactionservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {
    @Bean
    public NewTopic postLikedTopic() {
        return TopicBuilder
                .name("post-liked-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic postUnlikedTopic() {
        return TopicBuilder
                .name("post-unliked-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
