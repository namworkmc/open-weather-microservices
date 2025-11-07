package org.sideprj.weatheranalyticsservice.mapper;

import java.time.Instant;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.sideprj.openweathermicroservices.avro.Severity;
import org.sideprj.openweathermicroservices.avro.WeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.openweathermicroservices.avro.WeatherMetrics;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {UUID.class, Instant.class, Severity.class}
)
public interface HotWeatherEventMapper {

    @Mapping(target = "severity", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "metricsBuilder", ignore = true)
    @Mapping(target = "metrics", expression = "java(toWeatherMetrics(weatherEvent, heatIndex, dewPoint))")
    @Mapping(target = "alertId", expression = "java(UUID.randomUUID())")
    WeatherAlertEvent toWeatherAlertEvent(
            WeatherEvent weatherEvent,
            UUID sourceEventId,
            double heatIndex,
            double dewPoint
    );

    @Mapping(target = "temperatureDeviation", expression = "java(Double.NaN)")
    WeatherMetrics toWeatherMetrics(
            WeatherEvent weatherEvent,
            double heatIndex,
            double dewPoint
    );
}
