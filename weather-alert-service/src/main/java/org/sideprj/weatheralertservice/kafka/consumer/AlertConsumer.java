package org.sideprj.weatheralertservice.kafka.consumer;

import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertConsumer {

    @RetryableTopic
    @KafkaListener(topics = "${kafka.alert.topic.hot_weather}")
    public void consumeAlert(HotWeatherAlertEvent event) {
        log.debug("Received message: {}", event);
    }
}
