package com.streamly.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class TelemetryPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelemetryPlatformApplication.class, args);
	}

}
