package org.sideprj.weatheranalyticsservice.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "weather_events")
@Data
public class WeatherEventEntity extends AbstractDocument {

    private String city;

    private double temp;

    private double tempMin;

    private double tempMax;

    private String unit;  // e.g., "K" for Kelvin
}
