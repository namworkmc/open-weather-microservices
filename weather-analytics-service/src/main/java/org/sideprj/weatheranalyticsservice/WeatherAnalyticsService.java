package org.sideprj.weatheranalyticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableMongoAuditing
@EnableKafka
@EnableKafkaStreams
public class WeatherAnalyticsService {

    public static void main(String[] args) {
        SpringApplication.run(WeatherAnalyticsService.class, args);
    }

}
