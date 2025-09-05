package com.datalineage.kafka.integration.repository;

import com.datalineage.kafka.integration.entity.AvroSchemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Avro schema entities
 */
@Repository
public interface AvroSchemaRepository extends JpaRepository<AvroSchemaEntity, String> {
    
    Optional<AvroSchemaEntity> findBySchemaId(Integer schemaId);
    
    List<AvroSchemaEntity> findBySubject(String subject);
    
    Optional<AvroSchemaEntity> findBySubjectAndVersion(String subject, Integer version);
    
    List<AvroSchemaEntity> findByTopicId(String topicId);
    
    List<AvroSchemaEntity> findByIsDeletedFalse();
}