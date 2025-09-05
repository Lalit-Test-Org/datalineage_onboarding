package com.datalineage.kafka.integration.repository;

import com.datalineage.kafka.integration.entity.KafkaTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Kafka topic entities
 */
@Repository
public interface KafkaTopicRepository extends JpaRepository<KafkaTopicEntity, String> {
    
    List<KafkaTopicEntity> findByKafkaConnectionId(String connectionId);
    
    Optional<KafkaTopicEntity> findByTopicNameAndKafkaConnectionId(String topicName, String connectionId);
    
    List<KafkaTopicEntity> findByTopicNameContainingIgnoreCase(String topicName);
    
    void deleteByKafkaConnectionId(String connectionId);
}