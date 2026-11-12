// publishes to specific topic based on the schemaVersion of the Envelope event

package com.streamly.telemetry.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.config.KafkaTopicConfig;
import com.streamly.telemetry.event.envelope.EventEnvelope;

@Service
public class VersionAwareTelemetryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public VersionAwareTelemetryProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(EventEnvelope<?> eventWithMetaData) {
        int schemaVersion = eventWithMetaData.getSchemaVersion();

        switch (schemaVersion) {
            case 1 -> {
                kafkaTemplate.send(
                        KafkaTopicConfig.KAFKA_V1_TOPIC,
                        eventWithMetaData);
            }

            case 2 -> {
                kafkaTemplate.send(
                        KafkaTopicConfig.KAFKA_V2_TOPIC,
                        eventWithMetaData);
            }

            default -> {
                throw new IllegalArgumentException("Unsupported schema version " + schemaVersion);
            }
        }
    }

}
