package com.kafkaConsumer.in.persistence.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.kafkaConsumer.in.dto.ProfileStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProfileStatus status;

    @Column(name = "change_status_after")
    private Timestamp changeStatusAfter;

    @Column(name = "other_email")
    private List<String> otherEmails;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "emp_code")
    private String empCode;

    @Column(name = "department")
    private String department;

    @Column(name = "location")
    private String location;

    @Type(JsonType.class)
    @Column(name = "cost_centers")
    private List<JsonNode> costCenters;

    @Column(name = "reporting_manager")
    private String reportingManager;

    @Type(JsonType.class)
    @Column(name = "client_data")
    private JsonNode clientData;

    @Type(JsonType.class)
    @Column(name = "approvers")
    private List<JsonNode> approvers;

    @Type(JsonType.class)
    @Column(name = "signature_data")
    private JsonNode signatureData;

    @Type(JsonType.class)
    @Column(name = "metadata")
    private JsonNode metadata;
}
