package com.streamly.telemetry.config;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.producer.TelemetryProducerService;

@Configuration
public class DataInitializer {
    public DataInitializer() {
        System.out.println("DataInitializer bean created");
    }

    @Bean
    CommandLineRunner sendTelemetry(TelemetryProducerService producer) {
        return args -> {
            System.out.println("Data Initializer started");

            TelemetryEvent event = new TelemetryEvent(
                    1,
                    "event-001",
                    "sensor-001",
                    27.5,
                    65.2,
<<<<<<< HEAD
                    2,
=======
                    1,
>>>>>>> 2d68811eca6ecbd3f8ea8719cb1822fd79d3b40e
                    Instant.now());
            producer.publish(event);

            System.out.println("Telemetry event published to Kafka broker successfully");
        };
    }

}
