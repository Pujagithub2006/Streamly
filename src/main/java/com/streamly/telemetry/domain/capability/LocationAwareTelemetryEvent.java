package com.streamly.telemetry.domain.capability;

public interface LocationAwareTelemetryEvent {

    double getLatitude();

    double getLongitude();

}