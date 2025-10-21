package org.sideprj.weathernotificationservice.entity;

import org.sideprj.weathercommons.model.entity.AbstractDocument;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "notifications")
@Data
public class Notification<T> extends AbstractDocument {

    private T message;
}
