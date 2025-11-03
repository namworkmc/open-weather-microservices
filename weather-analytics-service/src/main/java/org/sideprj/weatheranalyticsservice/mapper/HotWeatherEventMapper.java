package org.sideprj.weatheranalyticsservice.mapper;

import java.time.Instant;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.sideprj.openweathermicroservices.avro.AlertSeverity;
import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {UUID.class, Instant.class}
)
public interface HotWeatherEventMapper {

    @Mapping(target = "temperatureDeviation", ignore = true)
    @Mapping(target = "sourceEventId", ignore = true)
    @Mapping(target = "severity", source = "weatherEvent.temperature", qualifiedByName = "mapSeverity")
    @Mapping(target = "ruleId", ignore = true)
    @Mapping(target = "ruleDescription", ignore = true)
    @Mapping(target = "heatIndex", ignore = true)
    @Mapping(target = "dewPoint", ignore = true)
    @Mapping(target = "alertId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "detectedAt", ignore = true)
    HotWeatherAlertEvent toHotWeatherAlertEvent(WeatherEvent weatherEvent);

    @Named("mapSeverity")
    default AlertSeverity mapSeverity(double temp) {
        if (temp >= 35 && temp <= 40) {
            return AlertSeverity.WARNING;
        }
        return AlertSeverity.CRITICAL;
    }
}
