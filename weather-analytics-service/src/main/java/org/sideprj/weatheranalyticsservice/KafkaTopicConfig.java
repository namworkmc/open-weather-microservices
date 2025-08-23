package org.sideprj.weatheranalyticsservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin.NewTopics hotWeatherTopic(
            @Value("${kafka.weather.topic.raw}") String weatherRawTopic,
            @Value("${kafka.weather.topic.hot-weather}") String weatherHotTopic
    ) {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(weatherRawTopic)
                        .partitions(3)
                        .replicas(1)
                        .build(),
                TopicBuilder.name(weatherHotTopic)
                        .partitions(3)
                        .replicas(1)
                        .build()
        );
    }
}
