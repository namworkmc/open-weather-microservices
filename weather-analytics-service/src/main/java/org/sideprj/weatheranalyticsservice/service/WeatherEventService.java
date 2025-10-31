package org.sideprj.weatheranalyticsservice.service;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;

public interface WeatherEventService {

    void save(WeatherEventEntity event);

    double getDeviation(String city, double currentTemp);
}
