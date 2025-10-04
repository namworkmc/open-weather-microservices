package org.sideprj.weathernotificationservice.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> notificationFactory() {
//        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
//        factory.setConsumerFactory(getConsumerFactory());
//        return factory;
//    }
//
//    private Map<String, Object> getConsumerProperties(Class<?> classType) {
//        var props = kafkaProperties.buildConsumerProperties();
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, classType);
//        return props;
//    }
//
//    private DefaultKafkaConsumerFactory<String, String> getConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(getConsumerProperties(String.class));
//    }
}
