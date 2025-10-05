package org.sideprj.weatherdataservice.kafka.producer;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weathercommons.kafka.producer.AbstractKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataRawKafkaProducer extends AbstractKafkaProducer<String, WeatherEvent> {

    @Value("${kafka.data.topic.raw}")
    private String topicRaw;

    public DataRawKafkaProducer(
            @Value("${kafka.header.source}") String sourceHeader,
            @Value("${kafka.header.version}") String versionHeader,
            KafkaTemplate<String, WeatherEvent> kafkaTemplate) {
        super(sourceHeader, versionHeader, kafkaTemplate);
    }

    @Override
    public String getTopic() {
        return topicRaw;
    }
}
