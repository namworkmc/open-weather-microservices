package org.sideprj.weatherdataservice.kafka.producer;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatherdataservice.feign.client.openweather.OpenWeatherResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WeatherMapper {

    public static WeatherEvent toWeatherEvent(OpenWeatherResponse response) {
        WeatherEvent event = new WeatherEvent();

        event.setCity(response.getName());
        event.setCountry(response.getSys().getCountry());
        event.setTimestamp(response.getDt());

        event.setTemp(response.getMain().getTemp());
        event.setFeelsLike(response.getMain().getFeelsLike());
        event.setTempMin(response.getMain().getTempMin());
        event.setTempMax(response.getMain().getTempMax());

        return event;
    }
}
