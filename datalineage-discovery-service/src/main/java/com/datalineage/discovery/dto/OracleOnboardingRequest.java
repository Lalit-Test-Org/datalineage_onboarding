package com.datalineage.discovery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * DTO for Oracle database onboarding request
 */
public class OracleOnboardingRequest {
    
    @NotBlank(message = "Connection name is required")
    private String connectionName;
    
    private String description;
    
    @NotBlank(message = "Host is required")
    private String host;
    
    @NotNull(message = "Port is required")
    @Min(value = 1, message = "Port must be greater than 0")
    @Max(value = 65535, message = "Port must be less than 65536")
    private Integer port;
    
    @NotBlank(message = "Service name is required")
    private String serviceName;
    
    @NotNull(message = "Authentication type is required")
    private AuthenticationType authenticationType;
    
    // Direct authentication fields
    private String username;
    private String password;
    
    // Kerberos authentication fields
    private String kerberosRealm;
    private String kerberosKdc;
    private String kerberosPrincipal;
    private String kerberosKeytabPath;
    
    // Optional connection settings
    private Integer connectionTimeout;
    private Integer readTimeout;
    private Boolean useSSL = false;
    private String sslTruststore;
    private String sslTruststorePassword;
    
    // Discovery options
    private boolean autoDiscoverMetadata = true;
    
    public enum AuthenticationType {
        DIRECT, KERBEROS
    }
    
    // Constructors
    public OracleOnboardingRequest() {}
    
    // Getters and Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public boolean isAutoDiscoverMetadata() {
        return autoDiscoverMetadata;
    }
    
    public void setAutoDiscoverMetadata(boolean autoDiscoverMetadata) {
        this.autoDiscoverMetadata = autoDiscoverMetadata;
    }
}