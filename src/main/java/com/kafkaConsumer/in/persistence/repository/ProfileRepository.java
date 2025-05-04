package com.kafkaConsumer.in.persistence.repository;

import com.kafkaConsumer.in.persistence.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserIdAndEntityId(UUID userId, UUID entityId);
}
