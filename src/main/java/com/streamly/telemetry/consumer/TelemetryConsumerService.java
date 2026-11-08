package com.streamly.telemetry.consumer;

import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.service.TelemetryPersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TelemetryConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryConsumerService.class);

    private final TelemetryPersistenceService persistenceService;

    public TelemetryConsumerService(
            TelemetryPersistenceService persistenceService) {

        this.persistenceService = persistenceService;
    }

    @RetryableTopic(attempts = "3", backOff = @BackOff(delay = 2000, multiplier = 2.0 // exponential backoff
    ), dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = "telemetry-events", groupId = "telemetry-group")
    public void consume(TelemetryEvent event) {

        logger.info(
                "Received telemetry event eventId={} deviceId={}",
                event.getEventId(),
                event.getDeviceId());

        if (event.getTemperature() < 0) {
            logger.warn(
                    "Invalid temperature detected eventId={} temperature={}",
                    event.getEventId(),
                    event.getTemperature());

            throw new RuntimeException("Invalid temperature value");
        }

        persistenceService.save(event);

        logger.info(
                "Telemetry persisted successfully eventId={}",
                event.getEventId());
    }
}
