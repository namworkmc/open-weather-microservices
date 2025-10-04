package org.sideprj.weatheranalyticsservice.service.impl;

import org.sideprj.weatheranalyticsservice.entity.HotWeatherAlertEntity;
import org.sideprj.weatheranalyticsservice.entity.WeatherEventEntity;
import org.sideprj.weatheranalyticsservice.repository.HotWeatherAlertRepository;
import org.sideprj.weatheranalyticsservice.repository.WeatherEventRepository;
import org.sideprj.weatheranalyticsservice.service.WeatherEvaluatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class WeatherEvaluatorServiceImpl implements WeatherEvaluatorService {

    private final WeatherEventRepository weatherEventRepository;

    private final HotWeatherAlertRepository hotWeatherAlertRepository;

    @Override
    public void evaluateAndPersist(WeatherEventEntity event) {
        weatherEventRepository.save(event);
    }

    @Override
    public void evaluateAndPersist(HotWeatherAlertEntity alert) {
        hotWeatherAlertRepository.save(alert);
    }
}
