package org.sideprj.weatheranalyticsservice.kafka.mapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.ReasonEnum;
import org.sideprj.openweathermicroservices.avro.SeverityEnum;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {UUID.class, Instant.class}
)
public interface HotWeatherEventMapper {

    @Mapping(target = "alertId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "timestamp", expression = "java(Instant.now())")
    @Mapping(target = "temperature", source = "temp")
    @Mapping(target = "severity", source = "temp", qualifiedByName = "mapSeverity")
    @Mapping(target = "reasons", source = "temp", qualifiedByName = "mapReasons")
    HotWeatherAlertEvent toHotWeatherAlertEvent(WeatherEvent weatherEvent);

    @Named("mapSeverity")
    default SeverityEnum mapSeverity(double temp) {
        if (temp >= 35 && temp <= 40) {
            return SeverityEnum.MEDIUM;
        }
        return SeverityEnum.HIGH;
    }

    @Named("mapReasons")
    default List<ReasonEnum> mapReasons(double temp) {
        return List.of(ReasonEnum.DRY, ReasonEnum.HOT);
    }
}
