package org.sideprj.weatheranalyticsservice.service;

import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.entity.WeatherEventEntity;
import org.sideprj.weatheranalyticsservice.util.WeatherEventMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherConsumerService {

    private final WeatherEvaluatorService weatherEvaluatorService;

    @RetryableTopic
    @KafkaListener(
            topics = "${kafka.weather.topic.raw}",
            containerFactory = "weatherEventContainerFactory"
    )
    public void consumeRaw(
            WeatherEvent message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.info("Received message: {}, partition {}", message, partition);
        WeatherEventEntity eventDoc = WeatherEventMapper.toEntity(message);
        weatherEvaluatorService.evaluateAndPersist(eventDoc);
    }

    @RetryableTopic
    @KafkaListener(
            topics = "${kafka.weather.topic.hot-weather}",
            containerFactory = "hotWeatherAlertContainerFactory"
    )
    public void consumeHotWeather(
            HotWeatherAlertEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("Received hot weather event: {}, partition: {}", event, partition);
        var alertDoc = WeatherEventMapper.toEntity(event);
        weatherEvaluatorService.evaluateAndPersist(alertDoc);
    }
}
