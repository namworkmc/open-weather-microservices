package org.sideprj.weatherrecommendationservice.kafka;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sideprj.weatherrecommendationservice.service.RecommendationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.weather.analytics.avro.WeatherTrendEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationConsumer {

    private final RecommendationService recommendationService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @KafkaListener(topics = "${kafka.weather.topic.trends}")
    public void consumeWeatherTrends(WeatherTrendEvent weatherTrendEvent) {
        executorService.execute(() -> recommendationService.getRecommendation(weatherTrendEvent));
        executorService.execute(() -> recommendationService.summarizeWeatherEvent(weatherTrendEvent));
    }
}
