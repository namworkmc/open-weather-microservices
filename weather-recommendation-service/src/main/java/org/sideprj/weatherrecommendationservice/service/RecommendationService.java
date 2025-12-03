package org.sideprj.weatherrecommendationservice.service;

import org.sideprj.openweathermicroservices.avro.WeatherAlertEvent;

import com.weather.analytics.avro.WeatherTrendEvent;

public interface RecommendationService {

    String getRecommendation(WeatherTrendEvent weatherTrendEvent);

    String getRecommendation(WeatherAlertEvent weatherAlertEvent);

    String summarizeWeatherEvent(WeatherTrendEvent weatherTrendEvent);

    String summarizeWeatherEvent(WeatherAlertEvent weatherAlertEvent);
}
