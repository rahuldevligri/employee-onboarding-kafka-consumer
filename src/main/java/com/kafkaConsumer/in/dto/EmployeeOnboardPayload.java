package com.kafkaConsumer.in.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EmployeeOnboardPayload {
    private String userId;
    private String adminUserId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private String email;
    private String dateOfBirth;
    private String status;
    private String clientName;
    private String clientSeries;
    private String employeeGrade;
    private String employeeCode;
    private String reportingManager;
    private String[] otherEmails;
    private String role;
    private String platform;
    private Map<String, Object> additionalFields;
}
