package com.streamly.telemetry.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import com.streamly.telemetry.domain.TelemetryEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DeadLetterConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DeadLetterConsumer.class);

    @KafkaListener(topics = "telemetry-events-dlt", groupId = "telemetry-dlt-group")
    public void consumeDeadLetterEvent(
            TelemetryEvent deadEvent,

            // failure details in production env
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,

            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,

            @Header(KafkaHeaders.OFFSET) long offset,

            @Header("kafka_dlt-exception-message") String exceptionMessage

    ) {
        logger.error(
                """
                        Event moved to Dead Letter Topic
                        event={}
                        topic={}
                        partition={}
                        offset={}
                        reason={}
                        """,
                deadEvent,
                topic,
                partition,
                offset,
                exceptionMessage);
    }
}
