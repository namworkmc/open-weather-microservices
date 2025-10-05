package org.sideprj.weatheranalyticsservice.kafka.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.entity.WeatherEventEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WeatherEventMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "city", target = "city")
    @Mapping(source = "temp", target = "temp")
    @Mapping(source = "tempMin", target = "tempMin")
    @Mapping(source = "tempMax", target = "tempMax")
    WeatherEventEntity toEntity(WeatherEvent message);
}
