package org.sideprj.weatheranalyticsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WeatherEventMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    WeatherEventEntity toEntity(WeatherEvent message);
}
