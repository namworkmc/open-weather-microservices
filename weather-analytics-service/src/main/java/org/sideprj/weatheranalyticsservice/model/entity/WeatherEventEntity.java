package org.sideprj.weatheranalyticsservice.model.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Document(collection = "weather_events")
@Data
public class WeatherEventEntity extends AbstractDocumentEntity {

    private UUID eventId;
    private String city;
    private String country;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private double pressure;
    private Instant timestamp;
    private Instant fetchedAt;
    private String source;
    private UUID correlationId;
    private DataQuality dataQuality;

    public enum DataQuality {
        VALID,
        INVALID,
        DUPLICATE,
        OUT_OF_RANGE
    }
}
