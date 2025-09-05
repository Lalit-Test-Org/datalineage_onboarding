package com.datalineage.kafka.integration.entity;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/**
 * Entity representing an Avro schema from Schema Registry
 */
@Entity
@Table(name = "avro_schemas")
public class AvroSchemaEntity extends BaseEntity {
    
    @Id
    @Override
    public String getId() {
        return super.getId();
    }
    
    @Column(name = "schema_id", nullable = false)
    private Integer schemaId;
    
    @Column(name = "subject", nullable = false)
    private String subject;
    
    @Column(name = "version", nullable = false)
    private Integer version;
    
    @Column(name = "schema_content", columnDefinition = "TEXT")
    private String schemaContent;
    
    @Column(name = "schema_type")
    private String schemaType = "AVRO";
    
    @Column(name = "compatibility_level")
    private String compatibilityLevel;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "schema_fingerprint")
    private String schemaFingerprint;
    
    // Simple topic reference (avoiding complex JPA relationships for now)
    @Column(name = "kafka_topic_id")
    private String topicId;
    
    // Default constructor
    public AvroSchemaEntity() {
        super();
        if (this.getId() == null) {
            this.setId(UUID.randomUUID().toString());
        }
    }
    
    // Getters and Setters
    public Integer getSchemaId() {
        return schemaId;
    }
    
    public void setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public String getSchemaContent() {
        return schemaContent;
    }
    
    public void setSchemaContent(String schemaContent) {
        this.schemaContent = schemaContent;
    }
    
    public String getSchemaType() {
        return schemaType;
    }
    
    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }
    
    public String getCompatibilityLevel() {
        return compatibilityLevel;
    }
    
    public void setCompatibilityLevel(String compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getSchemaFingerprint() {
        return schemaFingerprint;
    }
    
    public void setSchemaFingerprint(String schemaFingerprint) {
        this.schemaFingerprint = schemaFingerprint;
    }
    
    public String getTopicId() {
        return topicId;
    }
    
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}