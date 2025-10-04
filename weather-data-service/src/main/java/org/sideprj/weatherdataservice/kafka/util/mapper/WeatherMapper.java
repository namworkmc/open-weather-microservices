package org.sideprj.weatherdataservice.kafka.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatherdataservice.feign.client.openweather.Model200;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WeatherMapper {

    @Mapping(source = "name", target = "city")
    @Mapping(source = "sys.country", target = "country")
    @Mapping(source = "dt", target = "timestamp")
    @Mapping(source = "main.temp", target = "temp")
    @Mapping(source = "main.humidity", target = "humidity")
    @Mapping(source = "main.tempMin", target = "tempMin")
    @Mapping(source = "main.tempMax", target = "tempMax")
    WeatherEvent toWeatherEvent(Model200 weatherRes);
}
