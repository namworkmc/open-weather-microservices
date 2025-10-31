package org.sideprj.weatheranalyticsservice.kafka.consumer;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.mapper.HotWeatherEventMapper;
import org.sideprj.weatheranalyticsservice.mapper.WeatherEventMapper;
import org.sideprj.weatheranalyticsservice.service.OutboxService;
import org.sideprj.weatheranalyticsservice.service.WeatherEventService;
import org.sideprj.weatheranalyticsservice.util.DerivedMetricsUtil;
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

    @Value("${weather.heat-index-alert}")
    private double heatIndexAlert;

    @Value("${weather.dew-point-alert}")
    private double dewPointAlert;

    @Value("${weather.temp-deviation-alert}")
    private double tempDeviationAlert;

    @Value("${kafka.alert.topic.hot_weather}")
    private String alertHotWeatherTopic;

    @Value("${kafka.notification.topic.ignore}")
    private String notificationIgnoreTopic;

    private final WeatherEventMapper weatherEventMapper;

    private final HotWeatherEventMapper hotWeatherEventMapper;

    private final WeatherEventService weatherEventService;

    private final OutboxService outboxService;

    @RetryableTopic
    @KafkaListener(topics = "${kafka.data.topic.raw}", groupId = "analytics-service-group", containerFactory = "dataRawContainerFactory")
    public void consumeDataRaw(WeatherEvent message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.debug("Received message: {}, partition {}", message, partition);

        weatherEventService.save(weatherEventMapper.toEntity(message));

        var heatIndex = DerivedMetricsUtil.calculateHeatIndex(message.getTemperature(), message.getHumidity());
        var dewPoint = DerivedMetricsUtil.calculateDewPoint(message.getTemperature(), message.getHumidity());
        var tempDeviation = weatherEventService.getDeviation(message.getCity(), message.getTemperature());

        if (heatIndex > heatIndexAlert
                || dewPoint > dewPointAlert
                || tempDeviation > tempDeviationAlert) {
            outboxService.createOutboxMessage(alertHotWeatherTopic, message.getCity(), hotWeatherEventMapper.toHotWeatherAlertEvent(message));
        } else {
            outboxService.createOutboxMessage(notificationIgnoreTopic, message.getCity(), message);
        }
    }
}
