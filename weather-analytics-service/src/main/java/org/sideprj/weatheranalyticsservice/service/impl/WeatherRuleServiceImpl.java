package org.sideprj.weatheranalyticsservice.service.impl;

import java.util.List;
import java.util.Optional;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.model.entity.WeatherRuleSetEntity;
import org.sideprj.weatheranalyticsservice.repository.WeatherRuleSetRepository;
import org.sideprj.weatheranalyticsservice.service.AnalyticsCacheService;
import org.sideprj.weatheranalyticsservice.service.WeatherRuleSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class WeatherRuleServiceImpl implements WeatherRuleSetService {

    private final WeatherRuleSetRepository weatherRuleSetRepository;

    private final AnalyticsCacheService cacheService;

    @Override
    public boolean isValid(WeatherEvent weatherEvent) {
        Optional<WeatherRuleSetEntity> ruleOpt = cacheService.getRuleSetByCity(weatherEvent.getCity());
        return ruleOpt
                .map(rule ->
                        (rule.getTemperatureThreshold() == null || weatherEvent.getTemperature() > rule.getTemperatureThreshold())
                                && (rule.getHumidityThreshold() == null || weatherEvent.getHumidity() < rule.getHumidityThreshold())
                                && (rule.getWindSpeedThreshold() == null || weatherEvent.getWindSpeed() > rule.getWindSpeedThreshold())
                                && (rule.getPressureThreshold() == null || weatherEvent.getPressure() < rule.getPressureThreshold())
                )
                .orElse(true);
    }

    @Override
    public List<WeatherRuleSetEntity> save(List<WeatherRuleSetEntity> weatherRuleSets) {
        return weatherRuleSetRepository.saveAll(weatherRuleSets);
    }
}
