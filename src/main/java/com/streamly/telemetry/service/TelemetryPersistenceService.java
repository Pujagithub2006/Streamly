// converts TelemetryEvent to TelemetryRecord

package com.streamly.telemetry.service;

import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.domain.TelemetryRecord;
import com.streamly.telemetry.repository.TelemetryRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class TelemetryPersistenceService {

    private final TelemetryRepository telemetryRepository;

    public TelemetryPersistenceService(
            TelemetryRepository telemetryRepository) {

        this.telemetryRepository = telemetryRepository;
    }

    @Transactional
    public void save(TelemetryRecord record) {

        if (telemetryRepository
                .findByEventId(record.getEventId())
                .isPresent()) {
            System.out.println("Duplicate events ignored: " + record.getEventId());
            return;
        }

        telemetryRepository.save(record);
    }
}
