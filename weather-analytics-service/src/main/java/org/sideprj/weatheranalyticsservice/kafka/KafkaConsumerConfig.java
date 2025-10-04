package org.sideprj.weatheranalyticsservice.kafka;

import java.util.Map;

import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.util.WeatherEventMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;
    private static final int HOT_TEMPERATURE = 30;

    private Map<String, Object> getConsumerProperties(Class<?> classType) {
        var props = kafkaProperties.buildConsumerProperties();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, classType);
        return props;
    }

    @Bean
    public ConsumerFactory<String, WeatherEvent> weatherEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                getConsumerProperties(WeatherEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WeatherEvent> weatherEventContainerFactory(
            KafkaTemplate<String, HotWeatherAlertEvent> kafkaTemplate,
            @Value("${kafka.internal.topic.hot}") String internalHotTopic,
            @Value("${kafka.notification.topic.hot_weather}") String notificationHotWeatherTopic
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, WeatherEvent>();
        factory.setConsumerFactory(weatherEventConsumerFactory());
        factory.setAckDiscarded(true);
        factory.setRecordFilterStrategy(eventRecord -> {
            if (eventRecord.value().getTemp() > HOT_TEMPERATURE) {
                kafkaTemplate.send(internalHotTopic, WeatherEventMapper.toHotWeatherAlert(eventRecord.value()));
                kafkaTemplate.send(notificationHotWeatherTopic, WeatherEventMapper.toHotWeatherAlert(eventRecord.value()));
                return false;
            }
            return false;
        });
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HotWeatherAlertEvent> hotWeatherAlertContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, HotWeatherAlertEvent>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(getConsumerProperties(HotWeatherAlertEvent.class)));
        return factory;
    }
}
