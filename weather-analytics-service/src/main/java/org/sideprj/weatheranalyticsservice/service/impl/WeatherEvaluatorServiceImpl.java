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

    private final WeatherEventRepository weatherEventRepo;

    private final HotWeatherAlertRepository alertRepo;

    @Override
    public void evaluateAndPersist(WeatherEventEntity event) {
        weatherEventRepo.save(event);
    }

    @Override
    public void evaluateAndPersist(HotWeatherAlertEntity alert) {
        alertRepo.save(alert);
    }
}
