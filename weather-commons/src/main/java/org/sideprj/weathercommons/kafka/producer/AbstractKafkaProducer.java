package org.sideprj.weathercommons.kafka.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<K, V> {

    private final String sourceHeader;

    private final String versionHeader;

    protected final KafkaTemplate<K, V> kafkaTemplate;

    protected abstract String getTopic();

    protected String getDlqTopic() {
        return StringUtils.join(getTopic(), "-dlq");
    }

    public void send(K key, V message) {
        kafkaTemplate.send(prepareProducerRecord(getTopic(), key, message));
    }

    public void sendDlq(K key, V message) {
        kafkaTemplate.send(prepareProducerRecord(getDlqTopic(), key, message));
    }

    private ProducerRecord<K, V> prepareProducerRecord(String topic, K key, V message) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, key, message);
        producerRecord.headers().add("source", sourceHeader.getBytes());
        producerRecord.headers().add("version", versionHeader.getBytes());
        return producerRecord;
    }
}
