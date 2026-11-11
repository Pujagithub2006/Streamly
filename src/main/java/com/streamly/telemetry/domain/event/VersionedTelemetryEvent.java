package com.streamly.telemetry.domain.event;

import java.time.Instant;

public interface VersionedTelemetryEvent {

    // abstract getter methods (common in all TelemetryEvent<Version> (s))
    String getEventId();

    String getDeviceId();

    double getTemperature();

    double getHumidity();

    Instant getTimestamp();

    // no setters for this contract since every implementing class does not allow
    // mutability of these data members
}
