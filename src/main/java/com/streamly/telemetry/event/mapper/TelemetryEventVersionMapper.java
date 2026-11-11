package com.streamly.telemetry.service;

import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.domain.event.TelemetryEventV1;
import com.streamly.telemetry.domain.event.TelemetryEventV2;
import com.streamly.telemetry.domain.event.VersionedTelemetryEvent;

@Service
public class TelemetryEventVersionMapper {

    // mapping if version = 1
    public TelemetryEventV1 mapToV1(TelemetryEvent event) {

        return new TelemetryEventV1(
                event.getEventId(),
                event.getDeviceId(),
                event.getTemperature(),
                event.getHumidity(),
                event.getTimestamp());
    }

    // mapping if version = 2
    public TelemetryEventV2 mapToV2(TelemetryEvent event) {

        return new TelemetryEventV2(
                event.getEventId(),
                event.getDeviceId(),
                event.getTemperature(),
                event.getHumidity(),
                event.getBatteryLevel(),
                event.getTimestamp());
    }

    // choose mapping according to eventVerion
    public VersionedTelemetryEvent map(TelemetryEvent event) {

        return switch (event.getEventVersion()) {

            case 1 -> mapToV1(event);
            case 2 -> mapToV2(event);

            default -> throw new IllegalArgumentException("Unsupported Event Version: " + event.getEventVersion());
        };
    }

}
