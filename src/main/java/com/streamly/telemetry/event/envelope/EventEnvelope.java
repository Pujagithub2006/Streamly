// only transport metadata and no telemetry related fields

package com.streamly.telemetry.event.envelope;

import java.time.Instant;
import java.util.UUID;

public class EventEnvelope<T> {
    private UUID eventId;

    private EventType eventType;

    private int schemaVersion;

    private Instant occurredAt;

    private String source;

    private T payload;

    public EventEnvelope() {
    };

    public EventEnvelope(UUID eventId, EventType eventType, int schemaVersion, Instant occurredAt, String source,
            T payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.schemaVersion = schemaVersion;
        this.occurredAt = occurredAt;
        this.source = source;
        this.payload = payload;
    }

    public UUID getEventId() {
        return eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getSource() {
        return source;
    }

    public T getPayload() {
        return payload;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

}
