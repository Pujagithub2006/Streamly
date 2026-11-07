// converts TelemetryEvent to TelemetryRecord

package com.streamly.telemetry.service;

import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.domain.TelemetryRecord;
import com.streamly.telemetry.repository.TelemetryRepository;

@Service
public class TelemetryPersistenceService {

    private final TelemetryRepository telemetryRepository;

    public TelemetryPersistenceService(
            TelemetryRepository telemetryRepository) {

        this.telemetryRepository = telemetryRepository;
    }

    public void save(TelemetryEvent event) {

        TelemetryRecord record = new TelemetryRecord(
                event.getDeviceId(),
                event.getTemperature(),
                event.getHumidity(),
                event.getTimestamp());

        telemetryRepository.save(record);
    }
}
