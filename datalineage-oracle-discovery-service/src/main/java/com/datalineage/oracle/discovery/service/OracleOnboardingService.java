package com.datalineage.oracle.discovery.service;

import com.datalineage.oracle.discovery.dto.OracleConnectionResponse;
import com.datalineage.oracle.discovery.dto.OracleOnboardingRequest;
import com.datalineage.oracle.discovery.dto.MetadataDiscoveryRequest;
import com.datalineage.oracle.discovery.dto.MetadataDiscoveryResponse;
import com.datalineage.oracle.discovery.dto.OracleConnectionConfig;
import com.datalineage.oracle.discovery.entity.OracleConnectionEntity;
import com.datalineage.oracle.discovery.repository.OracleConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for Oracle database onboarding and connection management
 */
@Service
@Transactional
public class OracleOnboardingService {
    
    private static final Logger logger = LoggerFactory.getLogger(OracleOnboardingService.class);
    
    @Autowired
    private OracleConnectionRepository connectionRepository;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired
    private OracleConnectionService oracleConnectionService;
    
    @Autowired
    private OracleMetadataExtractionService metadataExtractionService;
    
    /**
     * Onboards a new Oracle database connection
     */
    public OracleConnectionResponse onboardOracleDatabase(OracleOnboardingRequest request) {
        logger.info("Starting Oracle database onboarding for connection: {}", request.getConnectionName());
        
        // Validate request
        validateOnboardingRequest(request);
        
        // Check if connection name already exists
        if (connectionRepository.existsByConnectionName(request.getConnectionName())) {
            throw new IllegalArgumentException("Connection with name '" + request.getConnectionName() + "' already exists");
        }
        
        // Create entity from request
        OracleConnectionEntity entity = createEntityFromRequest(request);
        
        // Test connection before saving
        if (!testConnectionInternal(entity)) {
            throw new RuntimeException("Connection test failed. Please verify your connection parameters.");
        }
        
        // Save connection
        entity = connectionRepository.save(entity);
        logger.info("Oracle connection saved with ID: {}", entity.getId());
        
        // Trigger metadata discovery if requested
        if (request.isAutoDiscoverMetadata()) {
            try {
                triggerMetadataDiscovery(entity.getId());
                entity.setLastDiscoveryAt(LocalDateTime.now());
                connectionRepository.save(entity);
            } catch (Exception e) {
                logger.warn("Failed to trigger automatic metadata discovery for connection {}: {}", 
                    entity.getId(), e.getMessage());
            }
        }
        
        return convertToResponse(entity);
    }
    
    /**
     * Gets all Oracle connections
     */
    @Transactional(readOnly = true)
    public List<OracleConnectionResponse> getAllConnections() {
        return connectionRepository.findByStatusOrderByCreatedAtDesc("ACTIVE")
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets Oracle connection by ID
     */
    @Transactional(readOnly = true)
    public Optional<OracleConnectionResponse> getConnectionById(String id) {
        return connectionRepository.findById(id)
            .map(this::convertToResponse);
    }
    
    /**
     * Updates Oracle connection
     */
    public OracleConnectionResponse updateConnection(String id, OracleOnboardingRequest request) {
        OracleConnectionEntity entity = connectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Connection not found with ID: " + id));
        
        // Validate request
        validateOnboardingRequest(request);
        
        // Check if connection name already exists (excluding current connection)
        if (!entity.getConnectionName().equals(request.getConnectionName()) &&
            connectionRepository.existsByConnectionName(request.getConnectionName())) {
            throw new IllegalArgumentException("Connection with name '" + request.getConnectionName() + "' already exists");
        }
        
        // Update entity from request
        updateEntityFromRequest(entity, request);
        
        // Test connection before saving
        if (!testConnectionInternal(entity)) {
            throw new RuntimeException("Connection test failed. Please verify your connection parameters.");
        }
        
        entity = connectionRepository.save(entity);
        logger.info("Oracle connection updated: {}", entity.getId());
        
        return convertToResponse(entity);
    }
    
    /**
     * Deletes Oracle connection
     */
    public void deleteConnection(String id) {
        OracleConnectionEntity entity = connectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Connection not found with ID: " + id));
        
        connectionRepository.delete(entity);
        logger.info("Oracle connection deleted: {}", id);
    }
    
    /**
     * Tests Oracle connection
     */
    public Map<String, Object> testConnection(String id) {
        OracleConnectionEntity entity = connectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Connection not found with ID: " + id));
        
        boolean isValid = testConnectionInternal(entity);
        
        // Update test results
        entity.setLastTestedAt(LocalDateTime.now());
        entity.setLastTestResult(isValid ? "SUCCESS" : "FAILED");
        connectionRepository.save(entity);
        
        Map<String, Object> result = new HashMap<>();
        result.put("connectionValid", isValid);
        result.put("connectionId", id);
        result.put("testedAt", LocalDateTime.now());
        
        return result;
    }
    
    /**
     * Triggers metadata discovery for Oracle connection
     */
    public Map<String, Object> triggerMetadataDiscovery(String id) {
        OracleConnectionEntity entity = connectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Connection not found with ID: " + id));
        
        try {
            // Create Oracle connection config
            OracleConnectionConfig connectionConfig = createConnectionConfig(entity);
            
            // Create metadata discovery request
            MetadataDiscoveryRequest discoveryRequest = new MetadataDiscoveryRequest(id);
            discoveryRequest.setIncludeTables(true);
            discoveryRequest.setIncludeColumns(true);
            discoveryRequest.setIncludeProcedures(true);
            discoveryRequest.setIncludeConstraints(true);
            discoveryRequest.setLimit(1000);
            discoveryRequest.setOffset(0);
            
            // Call metadata extraction service directly
            MetadataDiscoveryResponse response = metadataExtractionService.discoverMetadata(connectionConfig, discoveryRequest);
            
            // Update last discovery time
            entity.setLastDiscoveryAt(LocalDateTime.now());
            connectionRepository.save(entity);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("connectionId", id);
            result.put("discoveredAt", LocalDateTime.now());
            result.put("metadata", response);
            
            return result;
            
        } catch (SQLException e) {
            logger.error("Metadata discovery failed for connection {}: {}", id, e.getMessage());
            throw new RuntimeException("Metadata discovery failed: " + e.getMessage());
        }
    }
    
    private void validateOnboardingRequest(OracleOnboardingRequest request) {
        if (request.getAuthenticationType() == OracleOnboardingRequest.AuthenticationType.DIRECT) {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required for direct authentication");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required for direct authentication");
            }
        } else if (request.getAuthenticationType() == OracleOnboardingRequest.AuthenticationType.KERBEROS) {
            if (request.getKerberosRealm() == null || request.getKerberosRealm().trim().isEmpty()) {
                throw new IllegalArgumentException("Kerberos realm is required for Kerberos authentication");
            }
            if (request.getKerberosKdc() == null || request.getKerberosKdc().trim().isEmpty()) {
                throw new IllegalArgumentException("Kerberos KDC is required for Kerberos authentication");
            }
        }
    }
    
    private OracleConnectionEntity createEntityFromRequest(OracleOnboardingRequest request) {
        OracleConnectionEntity entity = new OracleConnectionEntity();
        updateEntityFromRequest(entity, request);
        return entity;
    }
    
    private void updateEntityFromRequest(OracleConnectionEntity entity, OracleOnboardingRequest request) {
        entity.setConnectionName(request.getConnectionName());
        entity.setDescription(request.getDescription());
        entity.setHost(request.getHost());
        entity.setPort(request.getPort());
        entity.setServiceName(request.getServiceName());
        entity.setAuthenticationType(OracleConnectionEntity.AuthenticationType.valueOf(request.getAuthenticationType().name()));
        
        // Handle authentication fields with encryption
        if (request.getAuthenticationType() == OracleOnboardingRequest.AuthenticationType.DIRECT) {
            entity.setUsername(request.getUsername());
            entity.setPasswordEncrypted(encryptionService.encrypt(request.getPassword()));
        } else {
            entity.setKerberosRealm(request.getKerberosRealm());
            entity.setKerberosKdc(request.getKerberosKdc());
            entity.setKerberosPrincipal(request.getKerberosPrincipal());
            entity.setKerberosKeytabPathEncrypted(encryptionService.encrypt(request.getKerberosKeytabPath()));
        }
        
        // Optional settings
        entity.setConnectionTimeout(request.getConnectionTimeout());
        entity.setReadTimeout(request.getReadTimeout());
        entity.setUseSSL(request.getUseSSL());
        entity.setSslTruststoreEncrypted(encryptionService.encrypt(request.getSslTruststore()));
        entity.setSslTruststorePasswordEncrypted(encryptionService.encrypt(request.getSslTruststorePassword()));
    }
    
    private boolean testConnectionInternal(OracleConnectionEntity entity) {
        try {
            OracleConnectionConfig connectionConfig = createConnectionConfig(entity);
            try (Connection connection = oracleConnectionService.createConnection(connectionConfig)) {
                return connection != null && !connection.isClosed();
            }
        } catch (Exception e) {
            logger.error("Error testing connection for entity {}: {}", entity.getId(), e.getMessage());
            return false;
        }
    }
    
    private OracleConnectionConfig createConnectionConfig(OracleConnectionEntity entity) {
        OracleConnectionConfig config = new OracleConnectionConfig();
        config.setConnectionId(entity.getId());
        config.setHost(entity.getHost());
        config.setPort(entity.getPort());
        config.setServiceName(entity.getServiceName());
        config.setAuthenticationType(OracleConnectionConfig.AuthenticationType.valueOf(entity.getAuthenticationType().name()));
        
        if (entity.getAuthenticationType() == OracleConnectionEntity.AuthenticationType.DIRECT) {
            config.setUsername(entity.getUsername());
            config.setPassword(encryptionService.decrypt(entity.getPasswordEncrypted()));
        } else {
            config.setKerberosRealm(entity.getKerberosRealm());
            config.setKerberosKdc(entity.getKerberosKdc());
            config.setKerberosPrincipal(entity.getKerberosPrincipal());
            config.setKerberosKeytabPath(encryptionService.decrypt(entity.getKerberosKeytabPathEncrypted()));
        }
        
        if (entity.getConnectionTimeout() != null) {
            config.setConnectionTimeout(entity.getConnectionTimeout());
        }
        if (entity.getReadTimeout() != null) {
            config.setReadTimeout(entity.getReadTimeout());
        }
        if (entity.getUseSSL() != null) {
            config.setUseSSL(entity.getUseSSL());
        }
        if (entity.getSslTruststoreEncrypted() != null) {
            config.setSslTruststore(encryptionService.decrypt(entity.getSslTruststoreEncrypted()));
        }
        if (entity.getSslTruststorePasswordEncrypted() != null) {
            config.setSslTruststorePassword(encryptionService.decrypt(entity.getSslTruststorePasswordEncrypted()));
        }
        
        return config;
    }
    
    private OracleConnectionResponse convertToResponse(OracleConnectionEntity entity) {
        OracleConnectionResponse response = new OracleConnectionResponse();
        response.setId(entity.getId());
        response.setConnectionName(entity.getConnectionName());
        response.setDescription(entity.getDescription());
        response.setHost(entity.getHost());
        response.setPort(entity.getPort());
        response.setServiceName(entity.getServiceName());
        response.setAuthenticationType(OracleConnectionResponse.AuthenticationType.valueOf(entity.getAuthenticationType().name()));
        response.setUsername(entity.getUsername()); // Only return username, never password
        response.setKerberosRealm(entity.getKerberosRealm());
        response.setKerberosKdc(entity.getKerberosKdc());
        response.setKerberosPrincipal(entity.getKerberosPrincipal());
        response.setConnectionTimeout(entity.getConnectionTimeout());
        response.setReadTimeout(entity.getReadTimeout());
        response.setUseSSL(entity.getUseSSL());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setLastTestedAt(entity.getLastTestedAt());
        response.setLastTestResult(entity.getLastTestResult());
        response.setLastDiscoveryAt(entity.getLastDiscoveryAt());
        
        return response;
    }
}