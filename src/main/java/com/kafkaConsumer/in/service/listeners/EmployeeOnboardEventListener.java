package com.kafkaConsumer.in.service.listeners;

import com.kafkaConsumer.in.dto.KafkaEvent;
import com.kafkaConsumer.in.service.EmployeeOnboardService;
import com.kafkaConsumer.in.service.KafkaEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeOnboardEventListener implements KafkaEventListener {
    @Autowired
    private EmployeeOnboardService employeeOnboardService;
    private static final String EMPLOYEE_CREATED = "employee-created";

    /**
     *
     * @param kafkaEvent
     * @throws Exception
     */
    @Override
    public void consumerEvent(final KafkaEvent kafkaEvent) {
        employeeOnboardService.onboard(kafkaEvent);
    }

    /**
     * Gets the consumer name.
     * Subclasses may override to provide a unique name.
     * @return The consumer name.
     */
    @Override
    public String getConsumerName() {
        return EMPLOYEE_CREATED;
    }

    /**
     * Gets the provider identifier.
     * Subclasses may override to specify a different identifier.
     * @return The provider identifier.
     */
    @Override
    public String getProviderIdentifier() {
        return EMPLOYEE_CREATED;
    }

    /**
     * Indicates whether this is the default provider.
     * Subclasses may override as needed.
     * @return {@code true} if default, otherwise {@code false}.
     */
    @Override
    public boolean isDefault() {
        return false;
    }
}
