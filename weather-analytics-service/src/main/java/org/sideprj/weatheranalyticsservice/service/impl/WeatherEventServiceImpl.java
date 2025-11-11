package org.sideprj.weatheranalyticsservice.service.impl;

import java.time.Instant;
import java.util.List;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;
import org.sideprj.weatheranalyticsservice.repository.WeatherEventRepository;
import org.sideprj.weatheranalyticsservice.service.WeatherEventService;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class WeatherEventServiceImpl implements WeatherEventService {

    private static final int SLIDING_WINDOW_SIZE = 10;

    private final WeatherEventRepository weatherEventRepository;

    @Override
    public void save(WeatherEventEntity event) {
        weatherEventRepository.save(event);
    }

    @Override
    public double getDeviation(String city, double currentTemp) {
        var avg = weatherEventRepository.findByCityOrderByCreatedAtDesc(city, Limit.of(SLIDING_WINDOW_SIZE))
                .stream()
                .mapToDouble(WeatherEventEntity::getTemperature)
                .average()
                .orElse(Double.NaN);
        return currentTemp - avg;
    }

    @Transactional(readOnly = true)
    @Override
    public List<WeatherEventEntity> getTrendsByMinuteWindow(int avgTemperatureTrendsMinuteWindow) {
        return weatherEventRepository.getByTimestampGreaterThanEqual(Instant.now().minusSeconds(avgTemperatureTrendsMinuteWindow * 60L));
    }
}
