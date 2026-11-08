package com.streamly.telemetry.consumer;

import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.domain.TelemetryRecord;
import com.streamly.telemetry.domain.event.VersionedTelemetryEvent;
import com.streamly.telemetry.service.TelemetryEventProcessor;
import com.streamly.telemetry.service.TelemetryEventVersionMapper;
import com.streamly.telemetry.service.TelemetryPersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TelemetryConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryConsumerService.class);

    private final TelemetryPersistenceService persistenceService;

    private final TelemetryEventVersionMapper eventVersionMapper;

    private final TelemetryEventProcessor telemetryEventProcessor;

    public TelemetryConsumerService(
            TelemetryPersistenceService persistenceService,
            TelemetryEventVersionMapper eventVersionMapper,
            TelemetryEventProcessor telemetryEventProcessor

    ) {
        this.persistenceService = persistenceService;
        this.eventVersionMapper = eventVersionMapper;
        this.telemetryEventProcessor = telemetryEventProcessor;
    }

    @RetryableTopic(attempts = "3", backOff = @BackOff(delay = 2000, multiplier = 2.0 // exponential backoff
    ), dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = "telemetry-events", groupId = "telemetry-group")
    public void consume(TelemetryEvent event) {

        // correct contract since every TelemetryEvent<Version> implicitly extends
        // Object
        VersionedTelemetryEvent versionedEvent = eventVersionMapper.map(event);

        // (Type Safety Lost) incorrect contract since now Object does not explicitly
        // know which TelemetryEvent<Version> and henceforth which methods
        // persistenceService.save(versionedEvent);

        TelemetryRecord record = telemetryEventProcessor.process(versionedEvent);

        persistenceService.save(record);

        logger.info(
                "Received telemetry event eventId={} deviceId={}",
                versionedEvent.getEventId(),
                versionedEvent.getDeviceId());

        if (versionedEvent.getTemperature() < 0) {
            logger.warn(
                    "Invalid temperature detected eventId={} temperature={}",
                    versionedEvent.getEventId(),
                    versionedEvent.getTemperature());

            throw new RuntimeException("Invalid temperature value");
        }

        logger.info(
                "Telemetry persisted successfully eventId={}",
                versionedEvent.getEventId());
    }
}
