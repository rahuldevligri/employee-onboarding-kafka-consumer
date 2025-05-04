package com.kafkaConsumer.in.controller;

import com.kafkaConsumer.in.consumers.EmployeeOnboardConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    private EmployeeOnboardConsumer employeeOnboardConsumer;

    /**
     * @param message
     * @return message
     */
    @PostMapping("/consume")
    public ResponseEntity<String> consume(final @RequestBody String message) {
        employeeOnboardConsumer.consumeEmployeeCreated(message);
        return ResponseEntity.ok("Message consumed successfully");
    }
}


