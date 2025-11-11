package org.sideprj.weatheranalyticsservice.service;

import java.util.List;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;

public interface WeatherEventService {

    void save(WeatherEventEntity event);

    double getDeviation(String city, double currentTemp);

    List<WeatherEventEntity> getTrendsByMinuteWindow(int avgTemperatureTrendsSize);
}
