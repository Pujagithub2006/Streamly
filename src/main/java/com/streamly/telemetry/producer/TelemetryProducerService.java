package com.streamly.telemetry.producer;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.event.envelope.EventEnvelope;
import com.streamly.telemetry.event.envelope.EventType;

@Service
public class TelemetryProducerService {
    private static final String TOPIC = "telemetry-events";
    private static final String SOURCE = "telemetry-producer";

    // changed from TelemetryEvent to EventEnvelope<TelemetryEvent>
    private final KafkaTemplate<String, EventEnvelope<TelemetryEvent>> kafkaTemplate;

    public TelemetryProducerService(KafkaTemplate<String, EventEnvelope<TelemetryEvent>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // event would be sent but extract metadata from it
    public void publish(TelemetryEvent event) {
        EventEnvelope<TelemetryEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.TELEMETRY,
                event.getEventVersion(),
                Instant.now(),
                SOURCE,
                event);

        kafkaTemplate.send(
                TOPIC,
                event.getDeviceId(),
                envelope);
    }

}
