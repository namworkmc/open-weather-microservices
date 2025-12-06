package org.sideprj.weatherrecommendationservice.kafka;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.sideprj.openweathermicroservices.avro.RegionalSummaryEvent;
import org.sideprj.openweathermicroservices.avro.WeatherAlertEvent;
import org.sideprj.weathercommons.kafka.SerdeFactoryFactory;
import org.sideprj.weatherrecommendationservice.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecommendationStreamsProcessor {

    private final SerdeFactoryFactory serdeFactoryFactory;

    private final RecommendationService recommendationService;

    @Autowired
    public void process(
            StreamsBuilder streamsBuilder,
            @Value("${kafka.weather.alert.topic.hot_weather}") String weatherAlertTopic,
            @Value("${kafka.weather.alert.topic.window-size}") Duration windowSize
    ) {
        streamsBuilder
                .stream(
                        weatherAlertTopic,
                        Consumed.with(Serdes.String(), serdeFactoryFactory.<WeatherAlertEvent>createAvroSerde())
                )
                .peek((key, weatherAlertEvent) -> recommendationService.summarizeWeatherEvent(weatherAlertEvent))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowSize))
                .aggregate(
                        RegionalAccumulator::new,
                        (city, event, agg) -> {
                            agg.increaseCount();
                            agg.addTemp(event.getMetrics().getTemperature());
                            agg.countSeverity(event.getSeverity());
                            return agg;
                        },
                        Materialized.as("RegionalAccumulator")
                )
                .toStream()
                .map(
                        (windowed, agg) -> new KeyValue<>(
                                windowed.key(),
                                RegionalSummaryEvent.newBuilder()
                                        .setRegionId(windowed.key())
                                        .setWindowStart(windowed.window().startTime())
                                        .setWindowEnd(windowed.window().endTime())
                                        .setAverageTemperature(agg.getTempSum() / agg.getCount())
                                        .setSeverity(
                                                agg.getSeverityCount()
                                                        .entrySet()
                                                        .stream()
                                                        .max(Map.Entry.comparingByValue())
                                                        .map(Map.Entry::getKey)
                                                        .orElse(null)
                                        )
                                        .build()
                        )
                );
    }
}
