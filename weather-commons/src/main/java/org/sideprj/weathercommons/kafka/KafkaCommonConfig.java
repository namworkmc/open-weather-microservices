package org.sideprj.weathercommons.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class KafkaCommonConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<?, ?> template) {
        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 3);
        DefaultErrorHandler handler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template),
                fixedBackOff
        );
        handler.setRetryListeners(
                (re, ex, attempt) -> log.error("DLT forwarding after {} attempts: {}", attempt, re, ex)
        );
        return handler;
    }

    @Bean
    public SerdeFactoryFactory serdeFactoryFactory(KafkaProperties kafkaProperties) {
        return new SerdeFactoryFactory(kafkaProperties);
    }
}
