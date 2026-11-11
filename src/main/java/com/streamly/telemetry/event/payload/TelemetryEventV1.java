package com.streamly.telemetry.event.payload;

import java.time.Instant;

public class TelemetryEventV1 implements VersionedTelemetryEvent {

    private int eventVersion = 1;

    private String eventId;

    private String deviceId;

    private double temperature;

    private double humidity;

    private Instant timestamp;

    public TelemetryEventV1() {
    }

    public TelemetryEventV1(String eventId, String deviceId, double temperature, double humidity, Instant timestamp) {
        this.eventId = eventId;
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    public int getEventVersion() { // only getter for eventVersion
        return eventVersion;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public double getHumidity() {
        return humidity;
    }

    @Override
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