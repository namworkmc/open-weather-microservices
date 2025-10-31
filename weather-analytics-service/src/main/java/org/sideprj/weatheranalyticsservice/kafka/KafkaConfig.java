package org.sideprj.weatheranalyticsservice.kafka;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.service.WeatherRuleSetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final WeatherRuleSetService weatherRuleService;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WeatherEvent> dataRawContainerFactory(
            ConsumerFactory<String, WeatherEvent> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, WeatherEvent>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAckDiscarded(true);
        factory.setRecordFilterStrategy(
                consumerRecord -> weatherRuleService.isValid(consumerRecord.value())
        );
        return factory;
    }
}
