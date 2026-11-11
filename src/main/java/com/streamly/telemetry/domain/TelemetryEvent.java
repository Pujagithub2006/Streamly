package com.streamly.telemetry.domain;

import java.time.Instant;

public class TelemetryEvent {

    private int eventVersion;

    private String eventId;

    private String deviceId;

    private double temperature;

    private double humidity;

    private double batteryLevel;

    private Instant timestamp;

    public TelemetryEvent() {
    }

    public TelemetryEvent(
            int eventVersion,
            String eventId,
            String deviceId,
            double temperature,
            double humidity,
            double batteryLevel,
            Instant timestamp) {

        this.eventVersion = eventVersion;
        this.eventId = eventId;
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.batteryLevel = batteryLevel;
        this.timestamp = timestamp;
    }

    public int getEventVersion() {
        return eventVersion;
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

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setEventVersion(int eventVersion) {
        this.eventVersion = eventVersion;
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

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}