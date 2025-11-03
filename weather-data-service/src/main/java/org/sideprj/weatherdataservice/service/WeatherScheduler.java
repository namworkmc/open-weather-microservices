package org.sideprj.weatherdataservice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.sideprj.openweathermicroservices.avro.DataQuality;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatherdataservice.feign.client.openweather.OpenWeatherService;
import org.sideprj.weatherdataservice.kafka.producer.DataRawKafkaProducer;
import org.sideprj.weatherdataservice.util.DataServiceDateUtil;
import org.sideprj.weatherdataservice.util.mapper.WeatherMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherScheduler {

    private static final Pair<Integer, Integer> MIN_MAX_TEMP = Pair.of(-50, 60);
    private static final Pair<Integer, Integer> MIN_MAX_HUMIDITY = Pair.of(0, 100);
    private static final Pair<Integer, Integer> MIN_MAX_WIND_SPEED = Pair.of(0, 400);
    private static final Pair<Integer, Integer> MIN_MAX_PRESSURE = Pair.of(870, 1085);
    private static final int TIMESTAMP_HOUR_FRESHNESS = 1;

    @Value("#{'${openweather.supported-cities}'.trim().split(',')}")
    private List<String> supportedCities;

    private final WeatherMapper weatherMapper;

    private final OpenWeatherService openWeatherService;

    private final DataRawKafkaProducer dataRawProducer;

    private final CacheService cacheService;

    @Scheduled(cron = "${scheduler.weather.cron:-}")
    public void sendWeatherUpdate() {
        supportedCities
                .stream()
                .map(city -> {
                    try {
                        return openWeatherService.getWeatherByCity(city);
                    } catch (Exception e) {
                        log.error("Failed to get weather data from OpenWeather API: {}", city, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(weatherRes -> cacheService.getLastFetchedTime(weatherRes.getName()).isEmpty())
                .map(weatherRes -> weatherMapper.toWeatherEvent(weatherRes, LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .forEach(weatherEvent -> {
                    log.info("Sending weather event: {}", weatherEvent);
                    var key = weatherEvent.getCity();
                    cacheService.putLastFetchedTime(key, DataServiceDateUtil.toLocalDateTime(weatherEvent.getTimestamp()));

                    if (isWeatherTimestampValid(weatherEvent)) {
                        if (!isWeatherDataInRange(weatherEvent)) {
                            weatherEvent.setDataQuality(DataQuality.OUT_OF_RANGE);
                            dataRawProducer.sendDlq(key, weatherEvent);
                            return;
                        }
                    } else {
                        weatherEvent.setDataQuality(DataQuality.INVALID);
                        dataRawProducer.sendDlq(key, weatherEvent);
                        return;
                    }

                    weatherEvent.setDataQuality(DataQuality.VALID);
                    dataRawProducer.send(key, weatherEvent);
                });
    }

    private static boolean isWeatherTimestampValid(WeatherEvent weatherEvent) {
        var timestampLocalDateTime = LocalDateTime.ofInstant(weatherEvent.getTimestamp(), ZoneId.systemDefault());
        return DataServiceDateUtil.isEqualOrAfter(timestampLocalDateTime, getOneHourAgo())
                && DataServiceDateUtil.isEqualOrAfter(
                DataServiceDateUtil.toLocalDateTime(weatherEvent.getFetchedAt()),
                timestampLocalDateTime
        );
    }

    private static boolean isWeatherDataInRange(WeatherEvent weatherEvent) {
        return isBetween((int) weatherEvent.getTemperature(), MIN_MAX_TEMP)
                && isBetween((int) weatherEvent.getHumidity(), MIN_MAX_HUMIDITY)
                && isBetween((int) weatherEvent.getWindSpeed(), MIN_MAX_WIND_SPEED)
                && isBetween((int) weatherEvent.getPressure(), MIN_MAX_PRESSURE);
    }

    private static LocalDateTime getOneHourAgo() {
        return LocalDateTime.now().minusHours(TIMESTAMP_HOUR_FRESHNESS);
    }

    private static boolean isBetween(int value, Pair<Integer, Integer> range) {
        return range.getLeft() <= value && value <= range.getRight();
    }
}
