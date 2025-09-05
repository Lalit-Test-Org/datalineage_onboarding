package com.datalineage.oracle.discovery.dto;

import java.time.LocalDateTime;

/**
 * DTO for Oracle connection response
 */
public class OracleConnectionResponse {
    
    private String id;
    private String connectionName;
    private String description;
    private String host;
    private Integer port;
    private String serviceName;
    private AuthenticationType authenticationType;
    private String username; // Only for response, password never returned
    private String kerberosRealm;
    private String kerberosKdc;
    private String kerberosPrincipal;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private Boolean useSSL;
    private String status; // ACTIVE, INACTIVE, ERROR
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTestedAt;
    private String lastTestResult;
    private LocalDateTime lastDiscoveryAt;
    
    public enum AuthenticationType {
        DIRECT, KERBEROS
    }
    
    // Constructors
    public OracleConnectionResponse() {}
    
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