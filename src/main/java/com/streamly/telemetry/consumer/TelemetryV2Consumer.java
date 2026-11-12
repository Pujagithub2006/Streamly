// Using the TelemetryEventProcessorService which earlier accepted a versioned event on Consumer boundary

package com.streamly.telemetry.consumer;

import org.springframework.kafka.annotation.KafkaListener;

import com.streamly.telemetry.event.envelope.EventEnvelope;
import com.streamly.telemetry.event.payload.TelemetryEventV2;
import com.streamly.telemetry.service.TelemetryEventProcessor;

public class TelemetryV2Consumer {
    // use the earlier TelemetryEventProcessor service which currently accepts the
    // EventEnvelope event which also has the meta data
    private final TelemetryEventProcessor processor;

    public TelemetryV2Consumer(TelemetryEventProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "telemetry-events-v2", // subscibed to particular topic version
            groupId = "telemetry-v2-consumer" // distinct consumer consuming particular topic version
    )

    // consume method now accepts EventEnvelope<?>
    public void consume(EventEnvelope<TelemetryEventV2> eventV2WithMetaData) {
        System.out.println("Consumed V2 telemetry event: " + eventV2WithMetaData.getEventId());

        // process method in TelemetryEventProcessor accepts type Event, so process
        // payload of EventEnvelope<?> type eventV2WithMetaData
        processor.process(eventV2WithMetaData.getPayload());
    }

}
