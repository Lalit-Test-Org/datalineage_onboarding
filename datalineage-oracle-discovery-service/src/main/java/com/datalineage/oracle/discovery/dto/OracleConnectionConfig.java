package com.datalineage.oracle.discovery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for Oracle connection configuration with direct credentials
 */
public class OracleConnectionConfig {
    
    @NotBlank(message = "Connection ID is required")
    private String connectionId;
    
    @NotBlank(message = "Host is required")
    private String host;
    
    @Positive(message = "Port must be positive")
    private int port;
    
    @NotBlank(message = "Service name or SID is required")
    private String serviceName;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotNull(message = "Authentication type is required")
    private AuthenticationType authenticationType;
    
    // Kerberos specific fields
    private String kerberosRealm;
    private String kerberosKdc;
    private String kerberosPrincipal;
    private String kerberosKeytabPath;
    
    // Connection properties
    private Integer connectionTimeout = 30; // seconds
    private Integer readTimeout = 60; // seconds
    private Boolean useSSL = false;
    private String sslTruststore;
    private String sslTruststorePassword;
    
    public enum AuthenticationType {
        DIRECT, KERBEROS
    }
    
    // Constructors
    public OracleConnectionConfig() {}
    
    public OracleConnectionConfig(String connectionId, String host, int port, String serviceName, 
                                 String username, String password, AuthenticationType authenticationType) {
        this.connectionId = connectionId;
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
        this.authenticationType = authenticationType;
    }
    
    // Getters and Setters
    public String getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }
    
    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
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
    
    public String getKerberosKeytabPath() {
        return kerberosKeytabPath;
    }
    
    public void setKerberosKeytabPath(String kerberosKeytabPath) {
        this.kerberosKeytabPath = kerberosKeytabPath;
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
    
    public String getSslTruststore() {
        return sslTruststore;
    }
    
    public void setSslTruststore(String sslTruststore) {
        this.sslTruststore = sslTruststore;
    }
    
    public String getSslTruststorePassword() {
        return sslTruststorePassword;
    }
    
    public void setSslTruststorePassword(String sslTruststorePassword) {
        this.sslTruststorePassword = sslTruststorePassword;
    }
}