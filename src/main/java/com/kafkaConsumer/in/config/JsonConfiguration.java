package com.kafkaConsumer.in.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Example: Add a custom serializer for Long values
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance); // Ensure Longs are serialized as strings
        objectMapper.registerModule(module);

        // You can add more customizations here as needed
        return objectMapper;
    }
}

