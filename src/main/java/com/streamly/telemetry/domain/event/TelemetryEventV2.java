package com.streamly.telemetry.domain.event;

import java.time.Instant;

import com.streamly.telemetry.domain.event.capability.BatteryAwareTelemetryEvent;

public class TelemetryEventV2
        implements VersionedTelemetryEvent, BatteryAwareTelemetryEvent {

    private int eventVersion = 2;

    private String eventId;

    private String deviceId;

    private double temperature;

    private double humidity;

    private double batteryLevel;

    private Instant timestamp;

    public TelemetryEventV2() {
    }

    public TelemetryEventV2(String eventId, String deviceId, double temperature, double humidity, double batteryLevel,
            Instant timestamp) {
        this.eventId = eventId;
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.batteryLevel = batteryLevel;
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
    public double getBatteryLevel() {
        return batteryLevel;
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

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}