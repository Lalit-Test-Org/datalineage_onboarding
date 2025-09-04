package com.datalineage.oracle.discovery.controller;

import com.datalineage.common.dto.ApiResponse;
import com.datalineage.oracle.discovery.dto.MetadataDiscoveryRequest;
import com.datalineage.oracle.discovery.dto.MetadataDiscoveryResponse;
import com.datalineage.oracle.discovery.dto.OracleConnectionConfig;
import com.datalineage.oracle.discovery.service.OracleConnectionService;
import com.datalineage.oracle.discovery.service.OracleMetadataExtractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Oracle metadata discovery operations
 */
@RestController
@RequestMapping("/api/v1/oracle-discovery")
@CrossOrigin(origins = "*")
public class OracleDiscoveryController {
    
    @Autowired
    private OracleConnectionService connectionService;
    
    @Autowired
    private OracleMetadataExtractionService metadataExtractionService;
    
    /**
     * Tests Oracle database connection
     */
    @PostMapping("/test-connection")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testConnection(
            @Valid @RequestBody OracleConnectionConfig config) {
        
        try {
            boolean isValid = connectionService.testConnection(config);
            
            Map<String, Object> result = new HashMap<>();
            result.put("connectionValid", isValid);
            result.put("connectionId", config.getConnectionId());
            result.put("host", config.getHost());
            result.put("port", config.getPort());
            result.put("serviceName", config.getServiceName());
            result.put("authenticationType", config.getAuthenticationType());
            
            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("Connection test successful", result));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Connection test failed", "CONNECTION_FAILED"));
            }
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("connectionValid", false);
            result.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Connection test error: " + e.getMessage(), "CONNECTION_ERROR"));
        }
    }
    
    /**
     * Discovers Oracle metadata
     */
    @PostMapping("/discover")
    public ResponseEntity<ApiResponse<MetadataDiscoveryResponse>> discoverMetadata(
            @Valid @RequestBody OracleConnectionConfig connectionConfig,
            @Valid @RequestBody MetadataDiscoveryRequest discoveryRequest) {
        
        try {
            // Ensure connection ID matches
            if (!connectionConfig.getConnectionId().equals(discoveryRequest.getConnectionId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Connection ID mismatch between config and request", "ID_MISMATCH"));
            }
            
            MetadataDiscoveryResponse response = metadataExtractionService.discoverMetadata(
                connectionConfig, discoveryRequest);
            
            return ResponseEntity.ok(ApiResponse.success("Metadata discovery completed successfully", response));
            
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database error during metadata discovery: " + e.getMessage(), "SQL_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error during metadata discovery: " + e.getMessage(), "DISCOVERY_ERROR"));
        }
    }
    
    /**
     * Discovers Oracle metadata with separate endpoints for connection config and discovery request
     */
    @PostMapping("/connections/{connectionId}/discover")
    public ResponseEntity<ApiResponse<MetadataDiscoveryResponse>> discoverMetadataByConnectionId(
            @PathVariable String connectionId,
            @Valid @RequestBody OracleConnectionConfig connectionConfig,
            @RequestParam(required = false) String[] schemas,
            @RequestParam(required = false) String[] tablePatterns,
            @RequestParam(required = false) String[] tableTypes,
            @RequestParam(defaultValue = "true") boolean includeTables,
            @RequestParam(defaultValue = "true") boolean includeColumns,
            @RequestParam(defaultValue = "true") boolean includeProcedures,
            @RequestParam(defaultValue = "true") boolean includeConstraints,
            @RequestParam(defaultValue = "1000") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        
        try {
            // Create discovery request from parameters
            MetadataDiscoveryRequest discoveryRequest = new MetadataDiscoveryRequest(connectionId);
            
            if (schemas != null && schemas.length > 0) {
                discoveryRequest.setSchemas(java.util.Arrays.asList(schemas));
            }
            if (tablePatterns != null && tablePatterns.length > 0) {
                discoveryRequest.setTablePatterns(java.util.Arrays.asList(tablePatterns));
            }
            if (tableTypes != null && tableTypes.length > 0) {
                discoveryRequest.setTableTypes(java.util.Arrays.asList(tableTypes));
            }
            
            discoveryRequest.setIncludeTables(includeTables);
            discoveryRequest.setIncludeColumns(includeColumns);
            discoveryRequest.setIncludeProcedures(includeProcedures);
            discoveryRequest.setIncludeConstraints(includeConstraints);
            discoveryRequest.setLimit(limit);
            discoveryRequest.setOffset(offset);
            
            // Ensure connection ID matches
            connectionConfig.setConnectionId(connectionId);
            
            MetadataDiscoveryResponse response = metadataExtractionService.discoverMetadata(
                connectionConfig, discoveryRequest);
            
            return ResponseEntity.ok(ApiResponse.success("Metadata discovery completed successfully", response));
            
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database error during metadata discovery: " + e.getMessage(), "SQL_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error during metadata discovery: " + e.getMessage(), "DISCOVERY_ERROR"));
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