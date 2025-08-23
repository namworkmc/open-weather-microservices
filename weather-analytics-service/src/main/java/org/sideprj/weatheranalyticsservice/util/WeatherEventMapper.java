package org.sideprj.weatheranalyticsservice.util;

import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.entity.HotWeatherAlertEntity;
import org.sideprj.weatheranalyticsservice.entity.WeatherEventEntity;

public interface WeatherEventMapper {
    static HotWeatherAlertEvent toHotWeatherAlert(WeatherEvent event) {
        return HotWeatherAlertEvent.newBuilder()
                .setCity(event.getCity())
                .setCountry(event.getCountry())
                .setTimestamp(event.getTimestamp())
                .setTemperature(event.getTemp())
                .setTempMin(event.getTempMin())
                .setTempMax(event.getTempMax())
                .build();
    }

    static WeatherEventEntity toEntity(WeatherEvent message) {
        var entity = new WeatherEventEntity();
        entity.setCity(message.getCity());
        entity.setTemp(message.getTemp());
        entity.setTempMin(message.getTempMin());
        entity.setTempMax(message.getTempMax());
        return entity;
    }

    static HotWeatherAlertEntity toEntity(HotWeatherAlertEvent event) {
        HotWeatherAlertEntity alertDoc = new HotWeatherAlertEntity();
        alertDoc.setCity(event.getCity());
        alertDoc.setTempMin(event.getTempMin());
        alertDoc.setTempMax(event.getTempMax());
        alertDoc.setReason("Hot temperature detected");
        return alertDoc;
    }
}
