package org.sideprj.weatherdataservice.service.impl;

import java.util.List;
import java.util.Random;

import org.sideprj.weatherdataservice.feign.client.openweather.OpenWeatherApiClient;
import org.sideprj.weatherdataservice.kafka.producer.WeatherMapper;
import org.sideprj.weatherdataservice.kafka.producer.impl.WeatherProducerServiceImpl;
import org.sideprj.weatherdataservice.service.WeatherSchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherSchedulerImpl implements WeatherSchedulerService {

    public static final Random RANDOM = new Random();

    private static final List<String> CITIES = List.of(
            "Berlin", "London", "Paris", "Madrid", "Rome",
            "Amsterdam", "Vienna", "Prague", "Warsaw", "Stockholm"
    );

    private final WeatherProducerServiceImpl weatherProducerService;

    private final OpenWeatherApiClient openWeatherApiClient;

    @Override
    @Scheduled(cron = "${scheduler.weather.cron}")
    public void sendWeatherUpdate() {
        String randomCity = getRandomCity();
        var res = openWeatherApiClient.getWeatherByCity(randomCity);
        log.info("Got weather data from OpenWeather API: {}", res);
        weatherProducerService.produce(
                randomCity,
                WeatherMapper.toWeatherEvent(res)
        );
    }

    private static String getRandomCity() {
        return CITIES.get(RANDOM.nextInt(CITIES.size()));
    }
}
