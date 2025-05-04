package com.kafkaConsumer.in.persistence.repository;

import com.kafkaConsumer.in.persistence.entity.UserPlatformMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPlatformMappingRepository extends JpaRepository<UserPlatformMapping, UUID> {
    Optional<UserPlatformMapping> findByUserIdAndPlatformId(UUID userId, UUID platformId);
}
