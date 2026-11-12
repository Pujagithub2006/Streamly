package com.streamly.telemetry.producer;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.event.envelope.EventEnvelope;
import com.streamly.telemetry.event.envelope.EventType;
import com.streamly.telemetry.event.mapper.TelemetryEventVersionMapper;
import com.streamly.telemetry.event.payload.VersionedTelemetryEvent;

@Service
public class TelemetryProducerService {
    private static final String TOPIC = "telemetry-events";
    private static final String SOURCE = "telemetry-producer";

    // changed from TelemetryEvent to EventEnvelope<TelemetryEvent> and then replace
    // with the VersionAwareTelemetryProducer which does the same work as of
    // KaftaTemplate but handles versions aswell
    // private final KafkaTemplate<String, EventEnvelope<TelemetryEvent>>
    // kafkaTemplate;

    private final VersionAwareTelemetryProducer versionAwareTelemetryProducer;

    private final TelemetryEventVersionMapper versionMapper; // particular event version to be mapped to particular
                                                             // topic

    public TelemetryProducerService(VersionAwareTelemetryProducer versionAwareTelemetryProducer,
            TelemetryEventVersionMapper versionMapper) {
        this.versionAwareTelemetryProducer = versionAwareTelemetryProducer;
        this.versionMapper = versionMapper;
    }

    // event would be sent but extract metadata from it
    public void publish(TelemetryEvent event) {

        VersionedTelemetryEvent versionedEvent = versionMapper.map(event); // creates versioned event

        // EventEnvelope now accepts a versioned event
        EventEnvelope<VersionedTelemetryEvent> versionedEnvelope = new EventEnvelope<>(
                UUID.randomUUID(),
                EventType.TELEMETRY,
                event.getEventVersion(),
                Instant.now(),
                SOURCE,
                versionedEvent); // versioned event payload

        // now publishes the versionedEnvelope
        versionAwareTelemetryProducer.publish(versionedEnvelope); // accepts only the EventEnvelope<?> event and handles
                                                                  // versions
        // and then sends (uses the KafkaTemplate method wrapped around
        // new publish method)
    }

}
