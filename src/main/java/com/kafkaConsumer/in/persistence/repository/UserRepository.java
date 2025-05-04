package com.kafkaConsumer.in.persistence.repository;

import com.kafkaConsumer.in.persistence.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    @EntityGraph(attributePaths = {"userPlatformMappings"})
    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.id = :id OR u.adminUserId = :id")
    Optional<User> findByIdOrAdminUserId(@Param("id") UUID id);
}
