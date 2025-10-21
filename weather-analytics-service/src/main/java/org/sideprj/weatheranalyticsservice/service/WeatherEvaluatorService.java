package org.sideprj.weatheranalyticsservice.service;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;

public interface WeatherEvaluatorService {

    void evaluateAndPersist(WeatherEventEntity event);
}
