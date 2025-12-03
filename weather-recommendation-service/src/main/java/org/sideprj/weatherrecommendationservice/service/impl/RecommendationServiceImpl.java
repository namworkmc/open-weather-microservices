package org.sideprj.weatherrecommendationservice.service.impl;

import java.util.Map;

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

    private final ChatClient chatClient;

    @Override
    @EnabledStoreVector(metadata = "city")
    public String getRecommendation(WeatherTrendEvent weatherTrendEvent) {
        var recommendationTemplate = new SystemPromptTemplate(weatherRecommendationPrompt);
        var prompt = recommendationTemplate.create(Map.of(
                "city", weatherTrendEvent.getCity(),
                "avgTemperature", weatherTrendEvent.getAvgTemperature(),
                "minTemperature", weatherTrendEvent.getMinTemperature(),
                "maxTemperature", weatherTrendEvent.getMaxTemperature(),
                "avgHumidity", weatherTrendEvent.getAvgHumidity(),
                "avgWindSpeed", weatherTrendEvent.getAvgWindSpeed(),
                "avgPressure", weatherTrendEvent.getAvgPressure()
        ));

        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public String getRecommendation(WeatherAlertEvent weatherAlertEvent) {
        return "";
    }

    @Override
    @EnabledStoreVector(metadata = {"city", "generatedAt"})
    public String summarizeWeatherEvent(WeatherTrendEvent weatherTrendEvent) {
        var summaryTemplate = new SystemPromptTemplate(weatherSummaryPrompt);
        var prompt = summaryTemplate.create(Map.of(
                "city", weatherTrendEvent.getCity(),
                "date", weatherTrendEvent.getGeneratedAt(),
                "avgTemperature", weatherTrendEvent.getAvgTemperature(),
                "minTemperature", weatherTrendEvent.getMinTemperature(),
                "maxTemperature", weatherTrendEvent.getMaxTemperature(),
                "avgHumidity", weatherTrendEvent.getAvgHumidity(),
                "avgWindSpeed", weatherTrendEvent.getAvgWindSpeed(),
                "avgPressure", weatherTrendEvent.getAvgPressure()
        ));
        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public String summarizeWeatherEvent(WeatherAlertEvent weatherAlertEvent) {
        return "";
    }
}
