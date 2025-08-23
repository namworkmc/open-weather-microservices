package org.sideprj.weatheranalyticsservice.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "hot_weather_alerts")
@Data
public class HotWeatherAlertEntity extends AbstractDocument {

    private String city;

    private double temp;

    private double tempMin;

    private double tempMax;

    private String reason;
}
