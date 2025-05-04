package com.kafkaConsumer.in.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaEvent {
    private String version;
    private String app;
    @JsonAlias
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("user_id")
    private String userId;
    private Map<String, String> metadata;
    private JsonNode data;
}
