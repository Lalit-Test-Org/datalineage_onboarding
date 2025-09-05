package com.datalineage.kafka.integration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for Kafka connection configuration
 */
public class KafkaConnectionConfig {
    
    @NotBlank(message = "Connection ID is required")
    private String connectionId;
    
    @NotBlank(message = "Bootstrap servers are required")
    private String bootstrapServers;
    
    private String securityProtocol = "PLAINTEXT";
    
    private String saslMechanism;
    
    private String saslUsername;
    
    private String saslPassword;
    
    private String keystoreLocation;
    
    private String keystorePassword;
    
    private String truststoreLocation;
    
    private String truststorePassword;
    
    // Schema Registry configuration
    private String schemaRegistryUrl;
    
    private String schemaRegistryUsername;
    
    private String schemaRegistryPassword;
    
    // Connection properties
    private Integer connectionTimeout = 30; // seconds
    private Integer sessionTimeout = 10; // seconds
    private Integer requestTimeout = 120; // seconds
    
    // Default constructor
    public KafkaConnectionConfig() {}
    
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
    
    public String getSaslPassword() {
        return saslPassword;
    }
    
    public void setSaslPassword(String saslPassword) {
        this.saslPassword = saslPassword;
    }
    
    public String getKeystoreLocation() {
        return keystoreLocation;
    }
    
    public void setKeystoreLocation(String keystoreLocation) {
        this.keystoreLocation = keystoreLocation;
    }
    
    public String getKeystorePassword() {
        return keystorePassword;
    }
    
    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }
    
    public String getTruststoreLocation() {
        return truststoreLocation;
    }
    
    public void setTruststoreLocation(String truststoreLocation) {
        this.truststoreLocation = truststoreLocation;
    }
    
    public String getTruststorePassword() {
        return truststorePassword;
    }
    
    public void setTruststorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
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
    
    public String getSchemaRegistryPassword() {
        return schemaRegistryPassword;
    }
    
    public void setSchemaRegistryPassword(String schemaRegistryPassword) {
        this.schemaRegistryPassword = schemaRegistryPassword;
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
    
    @Override
    public String toString() {
        return "KafkaConnectionConfig{" +
                "connectionId='" + connectionId + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", securityProtocol='" + securityProtocol + '\'' +
                ", saslMechanism='" + saslMechanism + '\'' +
                ", schemaRegistryUrl='" + schemaRegistryUrl + '\'' +
                ", connectionTimeout=" + connectionTimeout +
                ", sessionTimeout=" + sessionTimeout +
                ", requestTimeout=" + requestTimeout +
                '}';
    }
}