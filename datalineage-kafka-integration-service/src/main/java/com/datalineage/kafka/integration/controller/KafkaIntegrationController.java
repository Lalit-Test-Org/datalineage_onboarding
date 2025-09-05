package com.datalineage.kafka.integration.controller;

import com.datalineage.common.dto.ApiResponse;
import com.datalineage.kafka.integration.dto.KafkaConnectionConfig;
import com.datalineage.kafka.integration.entity.KafkaConnectionEntity;
import com.datalineage.kafka.integration.entity.KafkaTopicEntity;
import com.datalineage.kafka.integration.service.KafkaConnectionService;
import com.datalineage.kafka.integration.service.SchemaRegistryClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for Kafka integration endpoints
 */
@RestController
@RequestMapping("/api/v1/kafka")
@CrossOrigin(origins = "*")
public class KafkaIntegrationController {
    
    @Autowired
    private KafkaConnectionService kafkaConnectionService;
    
    @Autowired
    private SchemaRegistryClient schemaRegistryClient;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Kafka Integration Service is running"));
    }
    
    /**
     * Test Kafka connection
     */
    @PostMapping("/test-connection")
    public ResponseEntity<ApiResponse<Boolean>> testConnection(@Valid @RequestBody KafkaConnectionConfig config) {
        try {
            boolean isConnected = kafkaConnectionService.testConnection(config);
            if (isConnected) {
                return ResponseEntity.ok(ApiResponse.success("Connection successful", true));
            } else {
                return ResponseEntity.ok(ApiResponse.error("Connection failed", "CONNECTION_FAILED"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Connection test failed: " + e.getMessage(), "TEST_FAILED"));
        }
    }
    
    /**
     * Save Kafka connection
     */
    @PostMapping("/connections")
    public ResponseEntity<ApiResponse<KafkaConnectionEntity>> saveConnection(@Valid @RequestBody KafkaConnectionConfig config) {
        try {
            KafkaConnectionEntity savedConnection = kafkaConnectionService.saveConnection(config);
            return ResponseEntity.ok(ApiResponse.success("Connection saved successfully", savedConnection));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to save connection: " + e.getMessage(), "SAVE_FAILED"));
        }
    }
    
    /**
     * Get all active connections
     */
    @GetMapping("/connections")
    public ResponseEntity<ApiResponse<List<KafkaConnectionEntity>>> getConnections() {
        try {
            List<KafkaConnectionEntity> connections = kafkaConnectionService.getActiveConnections();
            return ResponseEntity.ok(ApiResponse.success("Connections retrieved successfully", connections));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to retrieve connections: " + e.getMessage(), "RETRIEVAL_FAILED"));
        }
    }
    
    /**
     * Get connection by ID
     */
    @GetMapping("/connections/{connectionId}")
    public ResponseEntity<ApiResponse<KafkaConnectionEntity>> getConnection(@PathVariable String connectionId) {
        try {
            Optional<KafkaConnectionEntity> connection = kafkaConnectionService.getConnection(connectionId);
            if (connection.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Connection retrieved successfully", connection.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("Connection not found", "NOT_FOUND"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to retrieve connection: " + e.getMessage(), "RETRIEVAL_FAILED"));
        }
    }
    
    /**
     * Delete connection
     */
    @DeleteMapping("/connections/{connectionId}")
    public ResponseEntity<ApiResponse<String>> deleteConnection(@PathVariable String connectionId) {
        try {
            kafkaConnectionService.deleteConnection(connectionId);
            return ResponseEntity.ok(ApiResponse.success("Connection deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to delete connection: " + e.getMessage(), "DELETE_FAILED"));
        }
    }
    
    /**
     * Discover topics for a connection
     */
    @PostMapping("/connections/{connectionId}/discover-topics")
    public ResponseEntity<ApiResponse<List<KafkaTopicEntity>>> discoverTopics(@PathVariable String connectionId) {
        try {
            List<KafkaTopicEntity> topics = kafkaConnectionService.discoverTopics(connectionId);
            return ResponseEntity.ok(ApiResponse.success("Topics discovered successfully", topics));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to discover topics: " + e.getMessage(), "DISCOVERY_FAILED"));
        }
    }
    
    /**
     * Test Schema Registry connection
     */
    @PostMapping("/test-schema-registry")
    public ResponseEntity<ApiResponse<Boolean>> testSchemaRegistry(@RequestBody SchemaRegistryTestRequest request) {
        try {
            boolean isConnected = schemaRegistryClient.testConnection(
                request.getSchemaRegistryUrl(), 
                request.getUsername(), 
                request.getPassword()
            );
            if (isConnected) {
                return ResponseEntity.ok(ApiResponse.success("Schema Registry connection successful", true));
            } else {
                return ResponseEntity.ok(ApiResponse.error("Schema Registry connection failed", "CONNECTION_FAILED"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Schema Registry test failed: " + e.getMessage(), "TEST_FAILED"));
        }
    }
    
    /**
     * Get subjects from Schema Registry
     */
    @PostMapping("/schema-registry/subjects")
    public ResponseEntity<ApiResponse<List<String>>> getSchemaSubjects(@RequestBody SchemaRegistryTestRequest request) {
        try {
            List<String> subjects = schemaRegistryClient.getSubjects(
                request.getSchemaRegistryUrl(), 
                request.getUsername(), 
                request.getPassword()
            );
            return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully", subjects));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to retrieve subjects: " + e.getMessage(), "RETRIEVAL_FAILED"));
        }
    }
    
    /**
     * DTO for Schema Registry test requests
     */
    public static class SchemaRegistryTestRequest {
        private String schemaRegistryUrl;
        private String username;
        private String password;
        
        // Getters and setters
        public String getSchemaRegistryUrl() { return schemaRegistryUrl; }
        public void setSchemaRegistryUrl(String schemaRegistryUrl) { this.schemaRegistryUrl = schemaRegistryUrl; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}