package org.sideprj.weatheranalyticsservice.service.impl;

import java.util.Optional;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherRuleSetEntity;
import org.sideprj.weatheranalyticsservice.repository.WeatherRuleSetRepository;
import org.sideprj.weatheranalyticsservice.service.AnalyticsCacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class, readOnly = true)
public class CacheServiceImpl implements AnalyticsCacheService {

    private final WeatherRuleSetRepository weatherRuleSetRepository;

    @Cacheable(cacheNames = "RULE_SET_FOR_REGION", key = "#city")
    @Override
    public Optional<WeatherRuleSetEntity> findRuleSetForRegion(String city) {
        return weatherRuleSetRepository.findByRegionIgnoreCase(city);
    }
}
