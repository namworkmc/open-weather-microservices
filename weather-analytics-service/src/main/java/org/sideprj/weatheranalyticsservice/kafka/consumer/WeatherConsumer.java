package org.sideprj.weatheranalyticsservice.kafka.consumer;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.kafka.mapper.HotWeatherEventMapper;
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

    @Value("${kafka.alert.topic.hot_weather}")
    private String alertHotWeatherTopic;

    @Value("${kafka.notification.topic.ignore}")
    private String notificationIgnoreTopic;

    private final WeatherEventMapper weatherEventMapper;

    private final HotWeatherEventMapper hotWeatherEventMapper;

    private final WeatherEvaluatorService weatherEvaluatorService;

    private final OutboxService outboxService;

    @RetryableTopic
    @KafkaListener(topics = "${kafka.data.topic.raw}", groupId = "analytics-service-group")
    public void consumeHotWeatherData(WeatherEvent message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.debug("Received message: {}, partition {}", message, partition);

        if (message.getTemp() >= hotTemperature) {
            outboxService.createOutboxMessage(alertHotWeatherTopic, message.getCity(), hotWeatherEventMapper.toHotWeatherAlertEvent(message));
        } else {
            outboxService.createOutboxMessage(notificationIgnoreTopic, message.getCity(), message);
        }

        weatherEvaluatorService.evaluateAndPersist(weatherEventMapper.toEntity(message));
    }
}
