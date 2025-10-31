package org.sideprj.weatheranalyticsservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherRuleSetDto extends AbstractDto {

    @NotNull
    private String region;

    private Double temperatureThreshold;

    private Double humidityThreshold;

    private Double windSpeedThreshold;

    private Double pressureThreshold;
}
