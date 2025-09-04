package com.datalineage.discovery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing Oracle database connection information
 */
@Entity
@Table(name = "oracle_connections")
public class OracleConnectionEntity {
    
    @Id
    private String id;
    
    @NotBlank
    @Column(name = "connection_name", nullable = false, unique = true)
    private String connectionName;
    
    @Column(name = "description")
    private String description;
    
    @NotBlank
    @Column(name = "host", nullable = false)
    private String host;
    
    @NotNull
    @Column(name = "port", nullable = false)
    private Integer port;
    
    @NotBlank
    @Column(name = "service_name", nullable = false)
    private String serviceName;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", nullable = false)
    private AuthenticationType authenticationType;
    
    // Direct authentication fields (encrypted)
    @Column(name = "username")
    private String username;
    
    @Column(name = "password_encrypted", length = 1000)
    private String passwordEncrypted;
    
    // Kerberos authentication fields
    @Column(name = "kerberos_realm")
    private String kerberosRealm;
    
    @Column(name = "kerberos_kdc")
    private String kerberosKdc;
    
    @Column(name = "kerberos_principal")
    private String kerberosPrincipal;
    
    @Column(name = "kerberos_keytab_path_encrypted", length = 1000)
    private String kerberosKeytabPathEncrypted;
    
    // Optional connection settings
    @Column(name = "connection_timeout")
    private Integer connectionTimeout;
    
    @Column(name = "read_timeout")
    private Integer readTimeout;
    
    @Column(name = "use_ssl")
    private Boolean useSSL = false;
    
    @Column(name = "ssl_truststore_encrypted", length = 1000)
    private String sslTruststoreEncrypted;
    
    @Column(name = "ssl_truststore_password_encrypted", length = 1000)
    private String sslTruststorePasswordEncrypted;
    
    // Status and metadata
    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, ERROR
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "last_tested_at")
    private LocalDateTime lastTestedAt;
    
    @Column(name = "last_test_result")
    private String lastTestResult;
    
    @Column(name = "last_discovery_at")
    private LocalDateTime lastDiscoveryAt;
    
    public enum AuthenticationType {
        DIRECT, KERBEROS
    }
    
    // Constructors
    public OracleConnectionEntity() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getConnectionName() {
        return connectionName;
    }
    
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }
    
    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }
    
    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }
    
    public String getKerberosRealm() {
        return kerberosRealm;
    }
    
    public void setKerberosRealm(String kerberosRealm) {
        this.kerberosRealm = kerberosRealm;
    }
    
    public String getKerberosKdc() {
        return kerberosKdc;
    }
    
    public void setKerberosKdc(String kerberosKdc) {
        this.kerberosKdc = kerberosKdc;
    }
    
    public String getKerberosPrincipal() {
        return kerberosPrincipal;
    }
    
    public void setKerberosPrincipal(String kerberosPrincipal) {
        this.kerberosPrincipal = kerberosPrincipal;
    }
    
    public String getKerberosKeytabPathEncrypted() {
        return kerberosKeytabPathEncrypted;
    }
    
    public void setKerberosKeytabPathEncrypted(String kerberosKeytabPathEncrypted) {
        this.kerberosKeytabPathEncrypted = kerberosKeytabPathEncrypted;
    }
    
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public Integer getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public Boolean getUseSSL() {
        return useSSL;
    }
    
    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }
    
    public String getSslTruststoreEncrypted() {
        return sslTruststoreEncrypted;
    }
    
    public void setSslTruststoreEncrypted(String sslTruststoreEncrypted) {
        this.sslTruststoreEncrypted = sslTruststoreEncrypted;
    }
    
    public String getSslTruststorePasswordEncrypted() {
        return sslTruststorePasswordEncrypted;
    }
    
    public void setSslTruststorePasswordEncrypted(String sslTruststorePasswordEncrypted) {
        this.sslTruststorePasswordEncrypted = sslTruststorePasswordEncrypted;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastTestedAt() {
        return lastTestedAt;
    }
    
    public void setLastTestedAt(LocalDateTime lastTestedAt) {
        this.lastTestedAt = lastTestedAt;
    }
    
    public String getLastTestResult() {
        return lastTestResult;
    }
    
    public void setLastTestResult(String lastTestResult) {
        this.lastTestResult = lastTestResult;
    }
    
    public LocalDateTime getLastDiscoveryAt() {
        return lastDiscoveryAt;
    }
    
    public void setLastDiscoveryAt(LocalDateTime lastDiscoveryAt) {
        this.lastDiscoveryAt = lastDiscoveryAt;
    }
}