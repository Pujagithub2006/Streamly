package com.streamly.telemetry.service;

import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryRecord;
import com.streamly.telemetry.domain.capability.BatteryAwareTelemetryEvent;
import com.streamly.telemetry.event.payload.VersionedTelemetryEvent;

@Service
public class TelemetryEventProcessor {

    public TelemetryRecord process(VersionedTelemetryEvent versionedEvent) {

        TelemetryRecord record = new TelemetryRecord(
                versionedEvent.getEventId(),
                versionedEvent.getDeviceId(),
                versionedEvent.getTemperature(),
                versionedEvent.getHumidity(),
                versionedEvent.getTimestamp());

        if (versionedEvent instanceof BatteryAwareTelemetryEvent batteryEvent) {
            System.out.println("Battery Level: " + batteryEvent.getBatteryLevel());
        }

        return record;
    }

}
