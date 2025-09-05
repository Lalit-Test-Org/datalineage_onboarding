package com.datalineage.kafka.integration.repository;

import com.datalineage.kafka.integration.entity.KafkaConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Kafka connection entities
 */
@Repository
public interface KafkaConnectionRepository extends JpaRepository<KafkaConnectionEntity, String> {
    
    Optional<KafkaConnectionEntity> findByConnectionId(String connectionId);
    
    List<KafkaConnectionEntity> findByIsActiveTrue();
    
    boolean existsByConnectionId(String connectionId);
    
    void deleteByConnectionId(String connectionId);
}