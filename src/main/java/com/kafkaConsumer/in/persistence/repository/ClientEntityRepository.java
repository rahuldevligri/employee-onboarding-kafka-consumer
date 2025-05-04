package com.kafkaConsumer.in.persistence.repository;

import com.kafkaConsumer.in.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientEntityRepository extends JpaRepository<ClientEntity, UUID> {
    @Query("SELECT c FROM ClientEntity c WHERE c.id IN :ids")
    List<ClientEntity> findByIdIn(@Param("ids") List<UUID> id);

    @Query("SELECT c FROM ClientEntity c WHERE c.series = :series")
    Optional<ClientEntity> findBySeries(@Param("series") String series);
}
