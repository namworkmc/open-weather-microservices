package org.sideprj.weatheranalyticsservice;

import org.sideprj.weathercommons.kafka.KafkaCommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableMongoAuditing
@EnableKafka
@EnableKafkaStreams
@Import(KafkaCommonConfig.class)
public class WeatherAnalyticsService {

    public static void main(String[] args) {
        SpringApplication.run(WeatherAnalyticsService.class, args);
    }

}
