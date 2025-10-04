package org.sideprj.weatheranalyticsservice.kafka;

import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.entity.WeatherEventEntity;
import org.sideprj.weatheranalyticsservice.service.WeatherEvaluatorService;
import org.sideprj.weatheranalyticsservice.util.WeatherEventMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherConsumer {

    private final WeatherEvaluatorService weatherEvaluatorService;

    @RetryableTopic
    @KafkaListener(
            topics = "${kafka.data.topic.raw}",
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
            topics = "${kafka.internal.topic.hot}",
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
