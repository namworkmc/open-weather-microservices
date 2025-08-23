package org.sideprj.weatherdataservice.kafka.producer.impl;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatherdataservice.kafka.producer.WeatherProducerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherProducerServiceImpl implements WeatherProducerService {

    @Value("${kafka.weather.topic.raw}")
    private String topicRaw;

    private final KafkaTemplate<String, WeatherEvent> kafkaTemplate;

    @Override
    public void produce(String city, WeatherEvent weatherEvent) {
        kafkaTemplate.send(topicRaw, city, weatherEvent);
        log.info("Sent [city={}}] with key {}", city, city);
    }
}
