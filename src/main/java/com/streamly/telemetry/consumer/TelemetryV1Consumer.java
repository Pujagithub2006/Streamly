// Using the TelemetryEventProcessorService which earlier accepted a versioned event on Consumer boundary

package com.streamly.telemetry.consumer;

import org.springframework.kafka.annotation.KafkaListener;

import com.streamly.telemetry.event.envelope.EventEnvelope;
import com.streamly.telemetry.event.payload.TelemetryEventV1;
import com.streamly.telemetry.service.TelemetryEventProcessor;

public class TelemetryV1Consumer {
    // use the earlier TelemetryEventProcessor service which currently accepts the
    // EventEnvelope event which also has the meta data
    private final TelemetryEventProcessor processor;

    public TelemetryV1Consumer(TelemetryEventProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "telemetry-events-v1", // subscibed to particular topic version
            groupId = "telemetry-v1-consumer" // distinct consumer consuming particular topic version
    )

    // consume method now accepts EventEnvelope<?>
    public void consume(EventEnvelope<TelemetryEventV1> eventV1WithMetaData) {
        System.out.println("Consumed V1 telemetry event: " + eventV1WithMetaData.getEventId());

        // process method in TelemetryEventProcessor accepts type Event, so process
        // payload of EventEnvelope<?> type eventV1WithMetaData
        processor.process(eventV1WithMetaData.getPayload());
    }

}
