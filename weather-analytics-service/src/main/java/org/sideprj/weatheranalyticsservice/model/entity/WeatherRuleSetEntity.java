package org.sideprj.weatheranalyticsservice.model.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "weather_rule_sets")
@Data
public class WeatherRuleSetEntity extends AbstractDocumentEntity {

    @Field("region")
    @Indexed(unique = true)
    private String region;

    @Field("temperature_threshold")
    private Double temperatureThreshold;

    @Field("humidity_threshold")
    private Double humidityThreshold;

    @Field("wind_speed_threshold")
    private Double windSpeedThreshold;

    @Field("pressure_threshold")
    private Double pressureThreshold;
}
