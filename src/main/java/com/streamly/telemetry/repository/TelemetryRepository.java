package com.streamly.telemetry.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.streamly.telemetry.domain.TelemetryRecord;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryRecord, Long> {
    Optional<TelemetryRecord> findByEventId(String eventId);

}
