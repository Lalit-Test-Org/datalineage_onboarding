package com.datalineage.kafka.integration.entity;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/**
 * Entity representing a Kafka connection configuration
 */
@Entity
@Table(name = "kafka_connections")
public class KafkaConnectionEntity extends BaseEntity {
    
    @Column(name = "connection_id", unique = true, nullable = false)
    private String connectionId;
    
    @Column(name = "bootstrap_servers", nullable = false)
    private String bootstrapServers;
    
    @Column(name = "security_protocol")
    private String securityProtocol = "PLAINTEXT";
    
    @Column(name = "sasl_mechanism")
    private String saslMechanism;
    
    @Column(name = "sasl_username")
    private String saslUsername;
    
    @Column(name = "sasl_password_encrypted")
    private String saslPasswordEncrypted;
    
    @Column(name = "keystore_location")
    private String keystoreLocation;
    
    @Column(name = "keystore_password_encrypted")
    private String keystorePasswordEncrypted;
    
    @Column(name = "truststore_location")
    private String truststoreLocation;
    
    @Column(name = "truststore_password_encrypted")
    private String truststorePasswordEncrypted;
    
    @Column(name = "schema_registry_url")
    private String schemaRegistryUrl;
    
    @Column(name = "schema_registry_username")
    private String schemaRegistryUsername;
    
    @Column(name = "schema_registry_password_encrypted")
    private String schemaRegistryPasswordEncrypted;
    
    @Column(name = "connection_timeout")
    private Integer connectionTimeout = 30;
    
    @Column(name = "session_timeout")
    private Integer sessionTimeout = 10;
    
    @Column(name = "request_timeout")
    private Integer requestTimeout = 120;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_tested")
    private java.time.LocalDateTime lastTested;
    
    @Column(name = "connection_status")
    private String connectionStatus;
    
    // Relationship with topics
    @OneToMany(mappedBy = "kafkaConnection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<KafkaTopicEntity> topics = new HashSet<>();
    
    // Default constructor
    public KafkaConnectionEntity() {
        super();
        if (this.getId() == null) {
            this.setId(UUID.randomUUID().toString());
        }
    }
    
    // Getters and Setters
    public String getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getBootstrapServers() {
        return bootstrapServers;
    }
    
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
    
    public String getSecurityProtocol() {
        return securityProtocol;
    }
    
    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }
    
    public String getSaslMechanism() {
        return saslMechanism;
    }
    
    public void setSaslMechanism(String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }
    
    public String getSaslUsername() {
        return saslUsername;
    }
    
    public void setSaslUsername(String saslUsername) {
        this.saslUsername = saslUsername;
    }
    
    public String getSaslPasswordEncrypted() {
        return saslPasswordEncrypted;
    }
    
    public void setSaslPasswordEncrypted(String saslPasswordEncrypted) {
        this.saslPasswordEncrypted = saslPasswordEncrypted;
    }
    
    public String getKeystoreLocation() {
        return keystoreLocation;
    }
    
    public void setKeystoreLocation(String keystoreLocation) {
        this.keystoreLocation = keystoreLocation;
    }
    
    public String getKeystorePasswordEncrypted() {
        return keystorePasswordEncrypted;
    }
    
    public void setKeystorePasswordEncrypted(String keystorePasswordEncrypted) {
        this.keystorePasswordEncrypted = keystorePasswordEncrypted;
    }
    
    public String getTruststoreLocation() {
        return truststoreLocation;
    }
    
    public void setTruststoreLocation(String truststoreLocation) {
        this.truststoreLocation = truststoreLocation;
    }
    
    public String getTruststorePasswordEncrypted() {
        return truststorePasswordEncrypted;
    }
    
    public void setTruststorePasswordEncrypted(String truststorePasswordEncrypted) {
        this.truststorePasswordEncrypted = truststorePasswordEncrypted;
    }
    
    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }
    
    public void setSchemaRegistryUrl(String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
    
    public String getSchemaRegistryUsername() {
        return schemaRegistryUsername;
    }
    
    public void setSchemaRegistryUsername(String schemaRegistryUsername) {
        this.schemaRegistryUsername = schemaRegistryUsername;
    }
    
    public String getSchemaRegistryPasswordEncrypted() {
        return schemaRegistryPasswordEncrypted;
    }
    
    public void setSchemaRegistryPasswordEncrypted(String schemaRegistryPasswordEncrypted) {
        this.schemaRegistryPasswordEncrypted = schemaRegistryPasswordEncrypted;
    }
    
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public Integer getSessionTimeout() {
        return sessionTimeout;
    }
    
    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    public Integer getRequestTimeout() {
        return requestTimeout;
    }
    
    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public java.time.LocalDateTime getLastTested() {
        return lastTested;
    }
    
    public void setLastTested(java.time.LocalDateTime lastTested) {
        this.lastTested = lastTested;
    }
    
    public String getConnectionStatus() {
        return connectionStatus;
    }
    
    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    public Set<KafkaTopicEntity> getTopics() {
        return topics;
    }
    
    public void setTopics(Set<KafkaTopicEntity> topics) {
        this.topics = topics;
    }
}