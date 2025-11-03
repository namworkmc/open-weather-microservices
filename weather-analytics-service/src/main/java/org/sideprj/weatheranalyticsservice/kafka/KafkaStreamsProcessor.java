package org.sideprj.weatheranalyticsservice.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.sideprj.weatheranalyticsservice.service.WeatherRuleSetService;
import org.sideprj.weatheranalyticsservice.util.DerivedMetricsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.stereotype.Component;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaStreamsProcessor {

    @Value("${kafka.data.topic.raw}")
    private String topicRaw;

    @Value("${kafka.alert.topic.hot_weather}")
    private String alertHotWeatherTopic;

    @Value("${kafka.notification.topic.ignore}")
    private String notificationIgnoreTopic;

    @Value("${weather.heat-index-alert}")
    private double heatIndexAlert;

    @Value("${weather.dew-point-alert}")
    private double dewPointAlert;

    @Value("${weather.temp-deviation-alert}")
    private double tempDeviationAlert;

    private final WeatherRuleSetService weatherRuleService;

    private final KafkaProperties kafkaProperties;

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {
        SpecificAvroSerde<WeatherEvent> serde = new SpecificAvroSerde<>();
        serde.configure(
                Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getProperties().get(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG)),
                false
        );

        KStream<String, WeatherEvent> weatherEventKStream = streamsBuilder.stream(topicRaw, Consumed.with(Serdes.String(), serde))
                .filter((key, value) -> weatherRuleService.isValid(value));
        new KafkaStreamBrancher<String, WeatherEvent>()
                .branch(
                        (key, value) -> {
                            var heatIndex = DerivedMetricsUtil.calculateHeatIndex(value.getTemperature(), value.getHumidity());
                            var dewPoint = DerivedMetricsUtil.calculateDewPoint(value.getTemperature(), value.getHumidity());
                            return heatIndex > heatIndexAlert || dewPoint > dewPointAlert;
                        },
                        ks -> ks.to(alertHotWeatherTopic)
                )
                .defaultBranch(ks -> ks.to(notificationIgnoreTopic))
                .onTopOf(weatherEventKStream);
    }
}
