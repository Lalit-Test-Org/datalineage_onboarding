package com.datalineage.oracle.discovery.controller;

import com.datalineage.common.dto.ApiResponse;
import com.datalineage.oracle.discovery.dto.OracleConnectionResponse;
import com.datalineage.oracle.discovery.dto.OracleOnboardingRequest;
import com.datalineage.oracle.discovery.service.OracleOnboardingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Oracle database onboarding operations
 */
@RestController
@RequestMapping("/api/v1/oracle")
@CrossOrigin(origins = "*")
public class OracleOnboardingController {
    
    private static final Logger logger = LoggerFactory.getLogger(OracleOnboardingController.class);
    
    @Autowired
    private OracleOnboardingService oracleOnboardingService;
    
    /**
     * Onboards a new Oracle database connection
     */
    @PostMapping("/onboard")
    public ResponseEntity<ApiResponse<OracleConnectionResponse>> onboardOracleDatabase(
            @Valid @RequestBody OracleOnboardingRequest request) {
        
        try {
            logger.info("Received Oracle onboarding request for connection: {}", request.getConnectionName());
            
            OracleConnectionResponse response = oracleOnboardingService.onboardOracleDatabase(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Oracle database onboarded successfully", response));
                
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid onboarding request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request: " + e.getMessage(), "INVALID_REQUEST"));
                
        } catch (RuntimeException e) {
            logger.error("Runtime error during onboarding: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), "ONBOARDING_FAILED"));
                
        } catch (Exception e) {
            logger.error("Unexpected error during Oracle onboarding: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error during onboarding", "INTERNAL_ERROR"));
        }
    }
    
    /**
     * Gets all Oracle connections
     */
    @GetMapping("/connections")
    public ResponseEntity<ApiResponse<List<OracleConnectionResponse>>> getAllConnections() {
        
        try {
            List<OracleConnectionResponse> connections = oracleOnboardingService.getAllConnections();
            
            return ResponseEntity.ok(ApiResponse.success("Oracle connections retrieved successfully", connections));
            
        } catch (Exception e) {
            logger.error("Error retrieving Oracle connections: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving connections", "RETRIEVAL_ERROR"));
        }
    }
    
    /**
     * Gets Oracle connection by ID
     */
    @GetMapping("/connections/{id}")
    public ResponseEntity<ApiResponse<OracleConnectionResponse>> getConnectionById(@PathVariable String id) {
        
        try {
            Optional<OracleConnectionResponse> connection = oracleOnboardingService.getConnectionById(id);
            
            if (connection.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Oracle connection retrieved successfully", connection.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Oracle connection not found", "CONNECTION_NOT_FOUND"));
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving Oracle connection {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving connection", "RETRIEVAL_ERROR"));
        }
    }
    
    /**
     * Updates Oracle connection
     */
    @PutMapping("/connections/{id}")
    public ResponseEntity<ApiResponse<OracleConnectionResponse>> updateConnection(
            @PathVariable String id,
            @Valid @RequestBody OracleOnboardingRequest request) {
        
        try {
            logger.info("Updating Oracle connection: {}", id);
            
            OracleConnectionResponse response = oracleOnboardingService.updateConnection(id, request);
            
            return ResponseEntity.ok(ApiResponse.success("Oracle connection updated successfully", response));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid update request for connection {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request: " + e.getMessage(), "INVALID_REQUEST"));
                
        } catch (RuntimeException e) {
            logger.error("Runtime error updating connection {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), "UPDATE_FAILED"));
                
        } catch (Exception e) {
            logger.error("Unexpected error updating Oracle connection {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error during update", "INTERNAL_ERROR"));
        }
    }
    
    /**
     * Deletes Oracle connection
     */
    @DeleteMapping("/connections/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConnection(@PathVariable String id) {
        
        try {
            logger.info("Deleting Oracle connection: {}", id);
            
            oracleOnboardingService.deleteConnection(id);
            
            return ResponseEntity.ok(ApiResponse.success("Oracle connection deleted successfully", null));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Connection not found for deletion: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Oracle connection not found", "CONNECTION_NOT_FOUND"));
                
        } catch (Exception e) {
            logger.error("Error deleting Oracle connection {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error deleting connection", "DELETION_ERROR"));
        }
    }
    
    /**
     * Tests Oracle connection
     */
    @PostMapping("/connections/{id}/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testConnection(@PathVariable String id) {
        
        try {
            logger.info("Testing Oracle connection: {}", id);
            
            Map<String, Object> result = oracleOnboardingService.testConnection(id);
            
            boolean isValid = (Boolean) result.get("connectionValid");
            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("Connection test successful", result));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Connection test failed", "CONNECTION_TEST_FAILED"));
            }
            
        } catch (IllegalArgumentException e) {
            logger.warn("Connection not found for testing: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Oracle connection not found", "CONNECTION_NOT_FOUND"));
                
        } catch (Exception e) {
            logger.error("Error testing Oracle connection {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error testing connection", "TEST_ERROR"));
        }
    }
    
    /**
     * Triggers metadata discovery for Oracle connection
     */
    @PostMapping("/connections/{id}/discover")
    public ResponseEntity<ApiResponse<Map<String, Object>>> triggerMetadataDiscovery(@PathVariable String id) {
        
        try {
            logger.info("Triggering metadata discovery for Oracle connection: {}", id);
            
            Map<String, Object> result = oracleOnboardingService.triggerMetadataDiscovery(id);
            
            return ResponseEntity.ok(ApiResponse.success("Metadata discovery triggered successfully", result));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Connection not found for metadata discovery: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Oracle connection not found", "CONNECTION_NOT_FOUND"));
                
        } catch (RuntimeException e) {
            logger.error("Runtime error during metadata discovery for connection {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), "DISCOVERY_FAILED"));
                
        } catch (Exception e) {
            logger.error("Error triggering metadata discovery for Oracle connection {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error triggering metadata discovery", "DISCOVERY_ERROR"));
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "oracle-discovery-service");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", health));
    }
}