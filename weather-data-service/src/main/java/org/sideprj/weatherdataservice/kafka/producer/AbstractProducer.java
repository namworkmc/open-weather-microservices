package org.sideprj.weatherdataservice.kafka.producer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractProducer<K, V> {

    protected final KafkaTemplate<K, V> kafkaTemplate;

    protected abstract String getTopic();

    protected String getDlqTopic() {
        return StringUtils.join(getTopic(), "-dlq");
    }

    public void produce(V message) {
        kafkaTemplate.send(getTopic(), message);
    }

    public void produce(K key, V message) {
        kafkaTemplate.send(getTopic(), key, message);
    }

    public void produceDlq(V message) {
        kafkaTemplate.send(getDlqTopic(), message);
    }

    public void produceDlq(K key, V message) {
        kafkaTemplate.send(getDlqTopic(), key, message);
    }
}
