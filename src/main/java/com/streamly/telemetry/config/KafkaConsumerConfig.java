package com.streamly.telemetry.config;

import com.streamly.telemetry.domain.TelemetryEvent;
import com.streamly.telemetry.event.envelope.EventEnvelope;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Bean
        public ConsumerFactory<String, EventEnvelope<TelemetryEvent>> consumerFactory() {

                JsonDeserializer<EventEnvelope<TelemetryEvent>> deserializer = new JsonDeserializer<>(
                                EventEnvelope.class);

                deserializer.addTrustedPackages("com.streamly.telemetry");

                Map<String, Object> properties = new HashMap<>();

                properties.put(
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                "kafka:9092");

                properties.put(
                                ConsumerConfig.GROUP_ID_CONFIG,
                                "telemetry-group");

                properties.put(
                                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                                "earliest");

                properties.put(
                                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                                StringDeserializer.class);

                properties.put(
                                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                                JsonDeserializer.class);

                return new DefaultKafkaConsumerFactory<>(
                                properties,
                                new StringDeserializer(),
                                deserializer);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, EventEnvelope<TelemetryEvent>> kafkaListenerContainerFactory() {

                ConcurrentKafkaListenerContainerFactory<String, EventEnvelope<TelemetryEvent>> factory = new ConcurrentKafkaListenerContainerFactory<>();

                factory.setConsumerFactory(consumerFactory());

                return factory;
        }
}