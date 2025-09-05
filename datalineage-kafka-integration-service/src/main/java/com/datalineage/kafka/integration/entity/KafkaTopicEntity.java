package com.datalineage.kafka.integration.entity;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/**
 * Entity representing a Kafka topic
 */
@Entity
@Table(name = "kafka_topics")
public class KafkaTopicEntity extends BaseEntity {
    
    @Id
    @Override
    public String getId() {
        return super.getId();
    }
    
    @Column(name = "topic_name", nullable = false)
    private String topicName;
    
    @Column(name = "partitions")
    private Integer partitions;
    
    @Column(name = "replication_factor")
    private Short replicationFactor;
    
    @Column(name = "cleanup_policy")
    private String cleanupPolicy;
    
    @Column(name = "retention_ms")
    private Long retentionMs;
    
    @Column(name = "retention_bytes")
    private Long retentionBytes;
    
    @Column(name = "segment_ms")
    private Long segmentMs;
    
    @Column(name = "segment_bytes")
    private Long segmentBytes;
    
    @Column(name = "compression_type")
    private String compressionType;
    
    @Column(name = "min_insync_replicas")
    private Integer minInsyncReplicas;
    
    // Schema information
    @Column(name = "key_schema_id")
    private Integer keySchemaId;
    
    @Column(name = "value_schema_id")
    private Integer valueSchemaId;
    
    @Column(name = "key_schema_subject")
    private String keySchemaSubject;
    
    @Column(name = "value_schema_subject")
    private String valueSchemaSubject;
    
    // Simple connection reference (avoiding complex JPA relationships for now)
    @Column(name = "kafka_connection_id")
    private String kafkaConnectionId;
    
    // Default constructor
    public KafkaTopicEntity() {
        super();
        if (this.getId() == null) {
            this.setId(UUID.randomUUID().toString());
        }
    }
    
    // Getters and Setters
    public String getTopicName() {
        return topicName;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    public Integer getPartitions() {
        return partitions;
    }
    
    public void setPartitions(Integer partitions) {
        this.partitions = partitions;
    }
    
    public Short getReplicationFactor() {
        return replicationFactor;
    }
    
    public void setReplicationFactor(Short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
    
    public String getCleanupPolicy() {
        return cleanupPolicy;
    }
    
    public void setCleanupPolicy(String cleanupPolicy) {
        this.cleanupPolicy = cleanupPolicy;
    }
    
    public Long getRetentionMs() {
        return retentionMs;
    }
    
    public void setRetentionMs(Long retentionMs) {
        this.retentionMs = retentionMs;
    }
    
    public Long getRetentionBytes() {
        return retentionBytes;
    }
    
    public void setRetentionBytes(Long retentionBytes) {
        this.retentionBytes = retentionBytes;
    }
    
    public Long getSegmentMs() {
        return segmentMs;
    }
    
    public void setSegmentMs(Long segmentMs) {
        this.segmentMs = segmentMs;
    }
    
    public Long getSegmentBytes() {
        return segmentBytes;
    }
    
    public void setSegmentBytes(Long segmentBytes) {
        this.segmentBytes = segmentBytes;
    }
    
    public String getCompressionType() {
        return compressionType;
    }
    
    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }
    
    public Integer getMinInsyncReplicas() {
        return minInsyncReplicas;
    }
    
    public void setMinInsyncReplicas(Integer minInsyncReplicas) {
        this.minInsyncReplicas = minInsyncReplicas;
    }
    
    public Integer getKeySchemaId() {
        return keySchemaId;
    }
    
    public void setKeySchemaId(Integer keySchemaId) {
        this.keySchemaId = keySchemaId;
    }
    
    public Integer getValueSchemaId() {
        return valueSchemaId;
    }
    
    public void setValueSchemaId(Integer valueSchemaId) {
        this.valueSchemaId = valueSchemaId;
    }
    
    public String getKeySchemaSubject() {
        return keySchemaSubject;
    }
    
    public void setKeySchemaSubject(String keySchemaSubject) {
        this.keySchemaSubject = keySchemaSubject;
    }
    
    public String getValueSchemaSubject() {
        return valueSchemaSubject;
    }
    
    public void setValueSchemaSubject(String valueSchemaSubject) {
        this.valueSchemaSubject = valueSchemaSubject;
    }
    
    public String getKafkaConnectionId() {
        return kafkaConnectionId;
    }
    
    public void setKafkaConnectionId(String kafkaConnectionId) {
        this.kafkaConnectionId = kafkaConnectionId;
    }
}