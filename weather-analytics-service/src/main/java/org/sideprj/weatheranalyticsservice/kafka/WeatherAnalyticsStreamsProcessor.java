package org.sideprj.weatheranalyticsservice.kafka;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.sideprj.openweathermicroservices.avro.InvalidEvent;
import org.sideprj.openweathermicroservices.avro.Severity;
import org.sideprj.openweathermicroservices.avro.WeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.mapper.HotWeatherEventMapper;
import org.sideprj.weatheranalyticsservice.service.AnalyticsCacheService;
import org.sideprj.weatheranalyticsservice.service.WeatherRuleSetService;
import org.sideprj.weatheranalyticsservice.util.DerivedMetricsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeatherAnalyticsStreamsProcessor {

    @Value("${kafka.analytics.sliding-window-duration}")
    private Duration analyticsSlidingWindowDuration;

    @Value("${weather.heat-index-alert}")
    private double heatIndexAlert;

    @Value("${weather.dew-point-alert}")
    private double dewPointAlert;

    private final WeatherRuleSetService weatherRuleService;

    private final SerdeFactoryFactory serdeFactoryFactory;

    private final HotWeatherEventMapper hotWeatherEventMapper;

    private final AnalyticsCacheService analyticsCacheService;

    @Autowired
    public void process(
            StreamsBuilder streamsBuilder,
            @Value("${kafka.data.topic.raw}") String topicRaw,
            @Value("${kafka.alert.topic.hot_weather}") String alertHotWeatherTopic,
            @Value("${kafka.weather.topic.ignored}") String weatherIgnoredTopic,
            @Value("${kafka.analytics.topic.invalid}") String analyticsInvalidTopic
    ) {
        streamsBuilder
                .stream(
                        topicRaw,
                        Consumed.with(Serdes.String(), serdeFactoryFactory.<WeatherEvent>createAvroSerde())
                )
                .split()
                .branch(
                        (key, value) -> weatherRuleService.isValid(value),
                        Branched.withConsumer(
                                getValidKStreamConsumer(alertHotWeatherTopic, weatherIgnoredTopic))
                )
                .defaultBranch(Branched.withConsumer(ks -> ks
                        .mapValues(value -> InvalidEvent.newBuilder()
                                .setEventId(value.getEventId())
                                .setCity(value.getCity())
                                .setCountry(value.getCountry())
                                .setTemperature(value.getTemperature())
                                .setHumidity(value.getHumidity())
                                .setReason("Temperature out of range")
                                .setEvaluatedAt(Instant.now())
                                .build())
                        .to(analyticsInvalidTopic)));
    }

    private Consumer<KStream<String, WeatherEvent>> getValidKStreamConsumer(String alertHotWeatherTopic, String weatherIgnoredTopic) {
        return kStream -> new KafkaStreamBrancher<String, WeatherEvent>()
                .branch(
                        (key, value) -> {
                            var heatIndex = DerivedMetricsUtil.calculateHeatIndex(value.getTemperature(), value.getHumidity());
                            var dewPoint = DerivedMetricsUtil.calculateDewPoint(value.getTemperature(), value.getHumidity());
                            return heatIndex > heatIndexAlert || dewPoint > dewPointAlert;
                        },
                        ks -> {
                            KStream<String, WeatherAlertEvent> alertEventKStream = ks.mapValues(value -> {
                                var alertEvent = hotWeatherEventMapper.toWeatherAlertEvent(
                                        value,
                                        value.getEventId(),
                                        DerivedMetricsUtil.calculateHeatIndex(value.getTemperature(), value.getHumidity()),
                                        DerivedMetricsUtil.calculateDewPoint(value.getTemperature(), value.getHumidity())
                                );
                                alertEvent.setSeverity(getSeverity(value));
                                return alertEvent;
                            });

                            alertEventKStream.to(alertHotWeatherTopic);
                        }
                )
                .defaultBranch(ks -> ks.to(weatherIgnoredTopic))
                .onTopOf(kStream);
    }

    private Severity getSeverity(WeatherEvent weatherEvent) {
        var ruleSetOpt = analyticsCacheService.findRuleSetForRegion(weatherEvent.getCity());
        if (ruleSetOpt.isEmpty()) {
            return Severity.LOW;
        }

        var ruleSet = ruleSetOpt.get();
        double temperature = weatherEvent.getTemperature();
        if (temperature >= ruleSet.getTemperatureThreshold() && temperature < ruleSet.getTemperatureThreshold() + 2) {
            return Severity.MODERATE;
        } else if (temperature >= ruleSet.getTemperatureThreshold() + 2 && temperature < ruleSet.getTemperatureThreshold() + 4) {
            return Severity.HIGH;
        } else if (temperature >= 4) {
            return Severity.EXTREME;
        }

        return Severity.LOW;
    }
}
