package com.streamly.telemetry.domain;

import java.time.Instant;

public class TelemetryEvent {

    private String deviceId;

    private double temperature;

    private double humidity;

    private Instant timestamp;

    public TelemetryEvent() {
    }

    public TelemetryEvent(
            String deviceId,
            double temperature,
            double humidity,
            Instant timestamp) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}