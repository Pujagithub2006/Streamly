package com.streamly.telemetry.service;

import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.domain.event.TelemetryEventV1;
import com.streamly.telemetry.domain.event.TelemetryEventV2;

@Service
public class TelemetryEventVersionMapper {

    public TelemetryEventV1 mapToV1(TelemetryEvent event) {

        return new TelemetryEventV1(
                event.getEventId(),
                event.getDeviceId(),
                event.getTemperature(),
                event.getHumidity(),
                event.getTimestamp());
    }

    public TelemetryEventV2 mapToV2(TelemetryEvent event) {

        return new TelemetryEventV2(
                event.getEventId(),
                event.getDeviceId(),
                event.getTemperature(),
                event.getHumidity(),
                event.getBatteryLevel(),
                event.getTimestamp());
    }

}
