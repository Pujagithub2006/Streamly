package com.streamly.telemetry.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;

@Service
public class TelemetryConsumerService {

    @KafkaListener(topics = "telemetry-events", groupId = "telemetry-group")

    public void consume(TelemetryEvent event) {
        System.out.println("Received telemetry event: " + event);
    }

}
