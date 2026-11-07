package com.streamly.telemetry.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;

@Service
public class TelemetryProducerService {
    private static final String TOPIC = "telemetry-events";
    private final KafkaTemplate<String, TelemetryEvent> kafkaTemplate;

    public TelemetryProducerService(KafkaTemplate<String, TelemetryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TelemetryEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.getDeviceId(),
                event);
    }

}
