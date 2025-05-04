package com.kafkaConsumer.in.persistence.repository;

import com.kafkaConsumer.in.persistence.entity.UserEntityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEntityMappingRepository extends JpaRepository<UserEntityMapping, UUID> {
    Optional<UserEntityMapping> findByUserIdAndEntityId(UUID id, UUID id1);
    Optional<UserEntityMapping> findByUserId(UUID id);
}
