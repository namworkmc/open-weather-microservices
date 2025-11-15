package org.sideprj.weatherdataservice.feign.client.openweather;

public interface OpenWeatherService {

    Model200 getWeatherByCity(String city);
}
