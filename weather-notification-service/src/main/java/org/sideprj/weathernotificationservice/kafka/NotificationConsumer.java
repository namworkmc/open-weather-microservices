package org.sideprj.weathernotificationservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.sideprj.weathernotificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.notification.topic.hot_weather}")
    public void consumeHotWeatherNotification(ConsumerRecord<String, ?> message) {
        log.debug("Received message: {}", message);
        notificationService.saveNotification(message.value());
        log.debug("Saved message");
    }
}
