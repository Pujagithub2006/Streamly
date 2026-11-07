package com.streamly.telemetry.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.service.TelemetryPersistenceService;

@Service
public class TelemetryConsumerService {

    private final TelemetryPersistenceService persistenceService;

    public TelemetryConsumerService(
            TelemetryPersistenceService persistenceService) {

        this.persistenceService = persistenceService;
    }

    @KafkaListener(topics = "telemetry-events", groupId = "telemetry-group")

    public void consume(TelemetryEvent event) {

        System.out.println("Received telemetry event: " + event);

        persistenceService.save(event);
    }
}
