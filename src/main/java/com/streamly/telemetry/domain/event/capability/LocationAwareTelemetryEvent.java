package com.streamly.telemetry.domain.event.capability;

public interface LocationAwareTelemetryEvent {

    double getLatitude();

    double getLongitude();

}