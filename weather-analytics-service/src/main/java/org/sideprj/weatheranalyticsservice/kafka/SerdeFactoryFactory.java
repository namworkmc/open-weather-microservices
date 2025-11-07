package org.sideprj.weatheranalyticsservice.kafka;

import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SerdeFactoryFactory {

    private final KafkaProperties kafkaProperties;

    public <T extends SpecificRecord> SpecificAvroSerde<T> createAvroSerde() {
        SpecificAvroSerde<T> serde = new SpecificAvroSerde<>();
        serde.configure(
                Map.of(
                        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                        kafkaProperties
                                .getProperties()
                                .get(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG),
                        "specific.avro.reader",
                        kafkaProperties
                                .getProperties()
                                .get("specific.avro.reader")
                ),
                false
        );
        return serde;
    }
}
