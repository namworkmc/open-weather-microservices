package org.sideprj.weatherrecommendationservice.service.impl;

import java.util.Map;
import java.util.Optional;

import org.sideprj.openweathermicroservices.avro.WeatherAlertEvent;
import org.sideprj.weatherrecommendationservice.llm.EnabledStoreVector;
import org.sideprj.weatherrecommendationservice.service.RecommendationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.weather.analytics.avro.WeatherTrendEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    @Value("${prompts.weather.recommendation}")
    private String weatherRecommendationPrompt;

    @Value("${prompts.weather.summary}")
    private String weatherSummaryPrompt;

    @Value("${prompts.weather.weatherAlertSummary}")
    private String weatherAlertSummary;

    private final ChatClient chatClient;

    @Override
    @EnabledStoreVector("city")
    public String getRecommendation(WeatherTrendEvent weatherTrendEvent) {
        return chatClient.prompt(
                        new SystemPromptTemplate(weatherRecommendationPrompt)
                                .create(
                                        Map.of(
                                                "city", weatherTrendEvent.getCity(),
                                                "avgTemperature", weatherTrendEvent.getAvgTemperature(),
                                                "minTemperature", weatherTrendEvent.getMinTemperature(),
                                                "maxTemperature", weatherTrendEvent.getMaxTemperature(),
                                                "avgHumidity", weatherTrendEvent.getAvgHumidity(),
                                                "avgWindSpeed", weatherTrendEvent.getAvgWindSpeed(),
                                                "avgPressure", weatherTrendEvent.getAvgPressure()
                                        )
                                )
                )
                .call()
                .content();
    }

    @Override
    @EnabledStoreVector({"city", "generatedAt"})
    public String summarizeWeatherEvent(WeatherTrendEvent weatherTrendEvent) {
        return chatClient.prompt(
                        new SystemPromptTemplate(weatherSummaryPrompt)
                                .create(
                                        Map.of(
                                                "city", weatherTrendEvent.getCity(),
                                                "date", weatherTrendEvent.getGeneratedAt(),
                                                "avgTemperature", weatherTrendEvent.getAvgTemperature(),
                                                "minTemperature", weatherTrendEvent.getMinTemperature(),
                                                "maxTemperature", weatherTrendEvent.getMaxTemperature(),
                                                "avgHumidity", weatherTrendEvent.getAvgHumidity(),
                                                "avgWindSpeed", weatherTrendEvent.getAvgWindSpeed(),
                                                "avgPressure", weatherTrendEvent.getAvgPressure()
                                        )
                                )
                )
                .call()
                .content();
    }

    @Override
    public String summarizeWeatherEvent(WeatherAlertEvent weatherAlertEvent) {
        return chatClient.prompt(
                        new SystemPromptTemplate(weatherAlertSummary)
                                .create(
                                        Map.ofEntries(
                                                Map.entry("alertId", weatherAlertEvent.getAlertId()),
                                                Map.entry("correlationId", weatherAlertEvent.getCorrelationId()),
                                                Map.entry("sourceEventId", weatherAlertEvent.getSourceEventId()),
                                                Map.entry("city", weatherAlertEvent.getCity()),
                                                Map.entry("country", weatherAlertEvent.getCountry()),
                                                Map.entry("severity", weatherAlertEvent.getSeverity()),
                                                Map.entry("reason", Optional.ofNullable(weatherAlertEvent.getReason()).orElse("")),
                                                Map.entry("temperature", weatherAlertEvent.getMetrics().getTemperature()),
                                                Map.entry("humidity", weatherAlertEvent.getMetrics().getHumidity()),
                                                Map.entry("heatIndex", weatherAlertEvent.getMetrics().getHeatIndex()),
                                                Map.entry("dewPoint", weatherAlertEvent.getMetrics().getDewPoint()),
                                                Map.entry("temperatureDeviation", weatherAlertEvent.getMetrics().getTemperatureDeviation()),
                                                Map.entry("timestamp", weatherAlertEvent.getTimestamp())
                                        )
                                )
                )
                .call()
                .content();
    }
}
