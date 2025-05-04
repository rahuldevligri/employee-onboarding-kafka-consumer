package com.kafkaConsumer.in.persistence.repository;

import com.kafkaConsumer.in.persistence.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, UUID> {
    Optional<Platform> findByDomain(String domain);
    Optional<Platform> findByName(String name);
}
