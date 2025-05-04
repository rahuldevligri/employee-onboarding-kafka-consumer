package com.kafkaConsumer.in.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaConsumer.in.dto.KafkaEvent;
import com.kafkaConsumer.in.service.KafkaEventListener;
import com.kafkaConsumer.in.service.provider.ProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmployeeOnboardConsumer {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final ProviderFactory<KafkaEventListener> factory;
    public EmployeeOnboardConsumer(final ProviderFactory<KafkaEventListener> factory) {
        this.factory = factory;
    }

    /**
     * Consume.
     * @param message the message
     */
    @KafkaListener(topics = "${kafka-config.topics.employee-created.name}", groupId = "${kafka-config.topics.employee-created.group}")
    public void consumeEmployeeCreated(final String message) {
        KafkaEvent kafkaEvent = parseMessage(message);
        if (kafkaEvent == null) {
            return;
        }
        log.info("kafka-consumer: message:{},status:received", kafkaEvent);
        List<KafkaEventListener> listeners = factory.getImplementations(KafkaEventListener.class, kafkaEvent.getMetadata().get("domain"));
        for (KafkaEventListener listener : listeners) {
            try {
                log.info("kafka-consumer: Consuming message:{},consumerName:{},status:started", message, listener.getConsumerName());
                listener.consumerEvent(kafkaEvent);
                log.info("kafka-consumer: Consuming message:{},consumerName:{},status:completed", message, listener.getConsumerName());
            } catch (Exception e) {
                log.error("kafka-consumer: Consuming message:{},consumerName:{},status:failed", message, listener.getConsumerName(), e);
            }
        }
    }
    private KafkaEvent parseMessage(final String message) {
        KafkaEvent kafkaEvent = null;
        try {
            kafkaEvent = objectMapper.readValue(message, KafkaEvent.class);
        } catch (JsonProcessingException e) {
            log.error("kafka-consumer: Error while parsing message:{}", message, e);
        }
        return kafkaEvent;
    }
}
