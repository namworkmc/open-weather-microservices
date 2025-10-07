package org.sideprj.weatheranalyticsservice.model.entity;

import java.util.HashMap;
import java.util.Map;

import org.sideprj.weatheranalyticsservice.model.enums.OutboxStatusEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "outbox")
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboxEvent extends AbstractDocument {

    private OutboxStatusEnum status;

    private String source;

    private Object key;

    private Map<String, Object> headers = new HashMap<>();

    private Object message = new HashMap<>();

    public OutboxEvent() {
    }

    public OutboxEvent(Object message) {
        this.message = message;
        this.status = OutboxStatusEnum.NEW;
    }

    public void putHeader(String key, Object value) {
        headers.put(key, value);
    }
}
