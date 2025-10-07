package org.sideprj.weatheranalyticsservice.service;

import org.sideprj.weatheranalyticsservice.model.entity.HotWeatherAlertEntity;
import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;

public interface WeatherEvaluatorService {
    void evaluateAndPersist(WeatherEventEntity event);

    void evaluateAndPersist(HotWeatherAlertEntity alert);
}
