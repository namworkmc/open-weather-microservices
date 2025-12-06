package org.sideprj.weatherrecommendationservice;

import org.sideprj.weathercommons.kafka.KafkaCommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableFeignClients
@EnableKafkaStreams
@Import(KafkaCommonConfig.class)
public class WeatherRecommendationService {

    public static void main(String[] args) {
        SpringApplication.run(WeatherRecommendationService.class, args);
    }
}
