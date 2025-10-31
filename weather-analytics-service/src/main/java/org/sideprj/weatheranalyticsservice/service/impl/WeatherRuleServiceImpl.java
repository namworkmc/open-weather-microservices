package org.sideprj.weatheranalyticsservice.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.model.entity.WeatherRuleSetEntity;
import org.sideprj.weatheranalyticsservice.repository.WeatherRuleSetRepository;
import org.sideprj.weatheranalyticsservice.service.WeatherRuleSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class WeatherRuleServiceImpl implements WeatherRuleSetService {

    private final WeatherRuleSetRepository weatherRuleSetRepository;

    @Override
    public boolean isValid(WeatherEvent weatherEvent) {
        Optional<WeatherRuleSetEntity> ruleOpt = findRuleSetForRegion(weatherEvent.getCity());
        if (ruleOpt.isEmpty()) {
            return false;
        }

        WeatherRuleSetEntity weatherRule = ruleOpt.get();
        boolean ok = weatherRule.getTemperatureThreshold() == null ||
                weatherEvent.getTemperature() > weatherRule.getTemperatureThreshold();

        ok &= weatherRule.getHumidityThreshold() == null ||
                weatherEvent.getHumidity() < weatherRule.getHumidityThreshold();

        ok &= weatherRule.getWindSpeedThreshold() == null ||
                weatherEvent.getWindSpeed() > weatherRule.getWindSpeedThreshold();

        ok &= weatherRule.getPressureThreshold() == null ||
                weatherEvent.getPressure() < weatherRule.getPressureThreshold();

        return ok;
    }

    @Override
    public List<WeatherRuleSetEntity> findByRegionIn(Collection<String> regions) {
        return weatherRuleSetRepository.findByRegionIn(regions);
    }

    @Override
    public List<WeatherRuleSetEntity> save(List<WeatherRuleSetEntity> weatherRuleSets) {
        return weatherRuleSetRepository.saveAll(weatherRuleSets);
    }

    private Optional<WeatherRuleSetEntity> findRuleSetForRegion(String city) {
        return weatherRuleSetRepository.findByRegionIgnoreCase(city);
    }
}
