package com.kafkaConsumer.in.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "entities")
public class ClientEntity {
    @Id
//    @GeneratedValue // Commented as for now we are not using auto increment
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "series")
    private String series;

    @Column(name = "two_factor_authentication")
    private Boolean twoFactorAuthentication;

    @Column(name = "password_expiry_enabled")
    private Boolean passwordExpiryEnabled;

    @Column(name = "password_expiry_days")
    private Integer passwordExpiryDays;

    @Column(name = "parent_entity_id")
    private UUID parentEntityId;

    @Column(name = "time_zone")
    private String timeZone;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "entity", fetch = FetchType.LAZY)
    private List<UserEntityMapping> userEntityMappings;
}
