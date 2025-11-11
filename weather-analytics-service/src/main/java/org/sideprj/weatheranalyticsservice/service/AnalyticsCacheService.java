package org.sideprj.weatheranalyticsservice.service;

import java.util.Optional;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherRuleSetEntity;

public interface AnalyticsCacheService {

    Optional<WeatherRuleSetEntity> getRuleSetByCity(String city);
}
