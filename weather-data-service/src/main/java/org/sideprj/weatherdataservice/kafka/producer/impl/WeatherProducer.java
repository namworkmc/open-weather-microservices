package org.sideprj.weatherdataservice.kafka.producer.impl;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatherdataservice.kafka.producer.AbstractProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WeatherProducer extends AbstractProducer<String, WeatherEvent> {

    @Value("${kafka.data.topic.raw}")
    private String topicRaw;

    @Override
    public String getTopic() {
        return topicRaw;
    }

    public WeatherProducer(KafkaTemplate<String, WeatherEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }
}
