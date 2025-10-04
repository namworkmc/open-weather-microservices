package org.sideprj.weatherdataservice.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.sideprj.weatherdataservice.feign.client.openweather.Model200;
import org.sideprj.weatherdataservice.feign.client.openweather.OpenWeatherService;
import org.sideprj.weatherdataservice.kafka.producer.impl.WeatherProducer;
import org.sideprj.weatherdataservice.kafka.util.mapper.WeatherMapper;
import org.sideprj.weatherdataservice.service.WeatherSchedulerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherSchedulerImpl implements WeatherSchedulerService {

    public static final int MIN_VALID_TEMP = -100;
    public static final int MAX_VALID_TEMP = 70;
    public static final int MIN_VALID_HUMIDITY = 0;
    public static final int MAX_VALID_HUMIDITY = 100;

    @Value("#{'${openweather.supported-cities}'.trim().split(',')}")
    private List<String> supportedCities;

    private final WeatherMapper weatherMapper;

    private final OpenWeatherService openWeatherService;

    private final WeatherProducer weatherProducer;

    @Override
    @Scheduled(cron = "${scheduler.weather.cron}")
    public void sendWeatherUpdate() {
        supportedCities
                .parallelStream()
                .map(city -> {
                    try {
                        return Pair.of(city, openWeatherService.getWeatherByCity(city));
                    } catch (Exception e) {
                        log.error("Failed to get weather data from OpenWeather API: {}", city, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(pair -> {
                    var key = pair.getKey();
                    var weatherRes = pair.getValue();
                    if (isWeatherValid(weatherRes)) {
                        weatherProducer.produce(key, weatherMapper.toWeatherEvent(weatherRes));
                    } else {
                        weatherProducer.produceDlq(key, weatherMapper.toWeatherEvent(weatherRes));
                    }
                });
    }

    private static boolean isWeatherValid(Model200 weatherRes) {
        return BigDecimal.valueOf(MIN_VALID_TEMP).compareTo(weatherRes.getMain().getTemp()) <= 0
                && BigDecimal.valueOf(MAX_VALID_TEMP).compareTo(weatherRes.getMain().getTemp()) >= 0
                && weatherRes.getMain().getHumidity() >= MIN_VALID_HUMIDITY && weatherRes.getMain().getHumidity() <= MAX_VALID_HUMIDITY
                && !StringUtils.isEmpty(weatherRes.getName());
    }
}
