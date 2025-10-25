package org.sideprj.weatherdataservice.util.mapper;

import java.time.Instant;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatherdataservice.feign.client.openweather.Model200;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {UUID.class}
)
public interface WeatherMapper {

    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "city", source = "weatherRes.name")
    @Mapping(target = "temperature", source = "weatherRes.main.temp")
    @Mapping(target = "humidity", source = "weatherRes.main.humidity")
    @Mapping(target = "windSpeed", source = "weatherRes.wind.speed")
    @Mapping(target = "pressure", source = "weatherRes.main.pressure")
    @Mapping(target = "country", source = "weatherRes.sys.country")
    @Mapping(target = "timestamp", source = "weatherRes.dt", qualifiedByName = "mapDtToTimestamp")
    @Mapping(target = "fetchedAt", source = "fetchedAt")
    @Mapping(target = "source", constant = "OpenWeatherAPI")
    @Mapping(target = "dataQuality", ignore = true)
    @Mapping(target = "correlationId", expression = "java(UUID.randomUUID())")
    WeatherEvent toWeatherEvent(Model200 weatherRes, Instant fetchedAt);

    @Named("mapDtToTimestamp")
    default Instant mapDtToTimestamp(Integer dt) {
        return Instant.ofEpochSecond(dt);
    }
}
