package com.streamly.telemetry.domain;

import java.time.Instant;

public class TelemetryEvent {

    private String eventId;

    private String deviceId;

    private double temperature;

    private double humidity;

    private Instant timestamp;

    public TelemetryEvent() {
    }

    public TelemetryEvent(
            String eventId,
            String deviceId,
            double temperature,
            double humidity,
            Instant timestamp) {

        this.eventId = eventId;
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
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

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}