package org.sideprj.weatheranalyticsservice.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.weather.analytics.avro.WeatherTrendEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalyticsSchedulerService {

    @Value("${analytics.avg_temperature_trends.minute_window}")
    private int avgTemperatureTrendsMinuteWindow;

    @Value("${kafka.weather.topic.trends}")
    private String weatherTrendsTopic;

    private final WeatherEventService weatherEventService;

    private final KafkaTemplate<String, WeatherTrendEvent> kafkaTemplate;

    @Scheduled(cron = "${analytics.avg_temperature_trends.cron.job}")
    public void aggregateTrendsJob() {
        var trendByCity
                = weatherEventService.getTrendsByMinuteWindow(avgTemperatureTrendsMinuteWindow)
                .stream()
                .collect(Collectors.groupingBy(WeatherEventEntity::getCity));

        trendByCity
                .entrySet()
                .stream()
                .map(entry -> {
                    List<WeatherEventEntity> weatherEvents = entry.getValue();
                    return WeatherTrendEvent.newBuilder()
                            .setCity(entry.getKey())
                            .setWindowStart(weatherEvents.stream().map(WeatherEventEntity::getTimestamp).min(Instant::compareTo).get())
                            .setWindowEnd(weatherEvents.stream().map(WeatherEventEntity::getTimestamp).max(Instant::compareTo).get())
                            .setEventCount(weatherEvents.size())
                            .setMinTemperature(weatherEvents.stream().mapToDouble(WeatherEventEntity::getTemperature).min().orElse(Double.NaN))
                            .setMaxTemperature(weatherEvents.stream().mapToDouble(WeatherEventEntity::getTemperature).max().orElse(Double.NaN))
                            .setAvgTemperature(weatherEvents.stream().mapToDouble(WeatherEventEntity::getTemperature).average().orElse(Double.NaN))
                            .setAvgHumidity(weatherEvents.stream().mapToDouble(WeatherEventEntity::getHumidity).average().orElse(Double.NaN))
                            .setAvgWindSpeed(weatherEvents.stream().mapToDouble(WeatherEventEntity::getWindSpeed).average().orElse(Double.NaN))
                            .setAvgPressure(weatherEvents.stream().mapToDouble(WeatherEventEntity::getPressure).average().orElse(Double.NaN))
                            .setCorrelationId(UUID.randomUUID())
                            .setGeneratedAt(Instant.now())
                            .build();

                })
                .forEach(weatherTrendEvent ->
                        kafkaTemplate.executeInTransaction(
                                operations -> operations.send(weatherTrendsTopic, weatherTrendEvent.getCity(), weatherTrendEvent)
                        )
                );
    }
}
