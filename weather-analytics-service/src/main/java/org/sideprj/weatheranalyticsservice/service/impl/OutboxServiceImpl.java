package org.sideprj.weatheranalyticsservice.service.impl;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.sideprj.weatheranalyticsservice.model.entity.OutboxEvent;
import org.sideprj.weatheranalyticsservice.model.enums.OutboxStatusEnum;
import org.sideprj.weatheranalyticsservice.repository.OutboxRepository;
import org.sideprj.weatheranalyticsservice.service.OutboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class OutboxServiceImpl implements OutboxService {

    @Value("${kafka.header.source}")
    String sourceHeader;

    @Value("${kafka.header.version}")
    String versionHeader;

    private final OutboxRepository outboxRepository;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void createOutboxMessage(String source, Object key, Object message) {
        OutboxEvent outbox = new OutboxEvent(message);
        outbox.setSource(source);
        outbox.setKey(key);
        outbox.putHeader("source", source);
        outbox.putHeader("version", versionHeader);
        outboxRepository.save(outbox);
    }

    @Override
    public void sendOutboxEvents() {
        List<OutboxEvent> outboxes = outboxRepository.findAllByStatusOrderByCreatedAtAsc(OutboxStatusEnum.NEW);
        outboxes.forEach(outbox -> outbox.setStatus(OutboxStatusEnum.SENT));
        outboxRepository.saveAll(outboxes);

        outboxes.forEach(outbox -> {
                    var producerRecord = new ProducerRecord<>(outbox.getSource(), outbox.getKey(), outbox.getMessage());
                    producerRecord.headers().add("source", sourceHeader.getBytes());
                    producerRecord.headers().add("version", versionHeader.getBytes());
                    kafkaTemplate.send(producerRecord);
                });
    }
}
