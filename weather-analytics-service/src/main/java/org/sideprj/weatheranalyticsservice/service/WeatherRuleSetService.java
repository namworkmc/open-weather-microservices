package org.sideprj.weatheranalyticsservice.service;

import java.util.Collection;
import java.util.List;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.model.entity.WeatherRuleSetEntity;

public interface WeatherRuleSetService {

    boolean isValid(WeatherEvent weatherEvent);

    List<WeatherRuleSetEntity> findByRegionIn(Collection<String> regions);

    List<WeatherRuleSetEntity> save(List<WeatherRuleSetEntity> weatherRuleSets);
}
