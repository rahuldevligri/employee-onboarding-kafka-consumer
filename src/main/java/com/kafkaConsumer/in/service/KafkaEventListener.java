package com.kafkaConsumer.in.service;

import com.kafkaConsumer.in.dto.KafkaEvent;
import com.kafkaConsumer.in.service.provider.ISimpleProvider;

public interface KafkaEventListener extends ISimpleProvider {
    void consumerEvent(KafkaEvent kafkaEvent) throws Exception;
    String getConsumerName();
}
