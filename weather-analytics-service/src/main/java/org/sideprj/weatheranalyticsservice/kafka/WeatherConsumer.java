package org.sideprj.weatheranalyticsservice.kafka;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.kafka.mapper.WeatherEventMapper;
import org.sideprj.weatheranalyticsservice.service.OutboxService;
import org.sideprj.weatheranalyticsservice.service.WeatherEvaluatorService;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${weather.hot-temperature}")
    private double hotTemperature;

    @Value("${kafka.internal.topic.hot}")
    private String hotWeatherInternalTopic;

    private final WeatherEventMapper weatherEventMapper;

    private final WeatherEvaluatorService weatherEvaluatorService;

    private final OutboxService outboxService;

    @RetryableTopic
    @KafkaListener(topics = "${kafka.data.topic.raw}", groupId = "analytics-service-group")
    public void consumeHotWeatherData(WeatherEvent message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.debug("Received message: {}, partition {}", message, partition);

        if (message.getTemp() >= hotTemperature) {
            outboxService.createOutboxMessage(hotWeatherInternalTopic, message.getCity(), message);
        }

        weatherEvaluatorService.evaluateAndPersist(weatherEventMapper.toEntity(message));
    }

    @RetryableTopic
    @KafkaListener(topics = "${kafka.internal.topic.hot}")
    public void consumeHotWeather(WeatherEvent event, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.debug("Received hot weather event: {}, partition: {}", event, partition);
        var alertDoc = weatherEventMapper.toEntity(event);
        weatherEvaluatorService.evaluateAndPersist(alertDoc);
    }
}
