package com.streamly.telemetry.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class KafkaTopicConfig {

    // different Kafka topics
    /*
     * NewTopic(
     * topic,
     * partition,
     * replicas
     * );
     */
    // Kafka Topic names
    public static final String KAFKA_V1_TOPIC = "telemetry-events-v1"; // visible to outside package 'producer'
    public static final String KAFKA_V2_TOPIC = "telemetry-events-v2";

    @Bean
    public NewTopic telemetryV1Topic() {
        return new NewTopic(
                KAFKA_V1_TOPIC,
                1,
                (short) 1);
    }

    @Bean
    public NewTopic telemetryV2Topic() {
        return new NewTopic(
                KAFKA_V2_TOPIC,
                1,
                (short) 1);
    }

    // do not need to manually create retry topic beans when using @RetryableTopic
    /*
     * @Retryable
     * 
     * @Bean
     * public NewTopic retryTelemetryTopic() {
     * return new NewTopic(
     * "telemetry-events-dlt",
     * 1,
     * (short) 1
     * );
     * }
     */

    @Bean
    public DefaultKafkaProducerFactoryCustomizer producerFactoryCustomizer() {
        return producerFactory -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            producerFactory.setValueSerializerSupplier(() -> new JsonSerializer<>(objectMapper));
        };
    }

}