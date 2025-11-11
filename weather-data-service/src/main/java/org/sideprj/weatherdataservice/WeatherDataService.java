package org.sideprj.weatherdataservice;

import org.sideprj.weathercommons.kafka.KafkaCommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableCaching
@EnableKafka
@Configuration
@Import(KafkaCommonConfig.class)
public class WeatherDataService {

    public static void main(String[] args) {
        SpringApplication.run(WeatherDataService.class, args);
    }

    @Bean
    public ThreadPoolTaskSchedulerCustomizer threadPoolTaskSchedulerCustomizer() {
        return scheduler -> scheduler.setErrorHandler(err -> log.error("Error in thread pool task scheduler", err));
    }
}
