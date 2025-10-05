package org.sideprj.weatheranalyticsservice.kafka.producer;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weathercommons.kafka.producer.AbstractKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class HotWeatherInternalProducer extends AbstractKafkaProducer<String, WeatherEvent> {

    @Value("${kafka.internal.topic.hot}")
    private String hotWeatherTopic;

    public HotWeatherInternalProducer(
            @Value("${kafka.header.source}") String sourceHeader,
            @Value("${kafka.header.version}") String versionHeader,
            KafkaTemplate<String, WeatherEvent> kafkaTemplate
    ) {
        super(sourceHeader, versionHeader, kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return hotWeatherTopic;
    }
}
