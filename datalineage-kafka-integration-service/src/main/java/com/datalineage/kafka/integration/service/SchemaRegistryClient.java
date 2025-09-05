package com.datalineage.kafka.integration.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * Simple Schema Registry client for retrieving Avro schemas
 */
@Service
public class SchemaRegistryClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public SchemaRegistryClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Test Schema Registry connection
     */
    public boolean testConnection(String schemaRegistryUrl, String username, String password) {
        try {
            HttpHeaders headers = createHeaders(username, password);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                schemaRegistryUrl + "/subjects", 
                HttpMethod.GET, 
                entity, 
                String.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            return false;
        }
    }
    
    /**
     * Get all subjects from Schema Registry
     */
    public List<String> getSubjects(String schemaRegistryUrl, String username, String password) {
        try {
            HttpHeaders headers = createHeaders(username, password);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String[]> response = restTemplate.exchange(
                schemaRegistryUrl + "/subjects", 
                HttpMethod.GET, 
                entity, 
                String[].class
            );
            
            return response.getBody() != null ? Arrays.asList(response.getBody()) : new ArrayList<>();
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all versions for a subject
     */
    public List<Integer> getVersions(String schemaRegistryUrl, String subject, String username, String password) {
        try {
            HttpHeaders headers = createHeaders(username, password);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Integer[]> response = restTemplate.exchange(
                schemaRegistryUrl + "/subjects/" + subject + "/versions", 
                HttpMethod.GET, 
                entity, 
                Integer[].class
            );
            
            return response.getBody() != null ? Arrays.asList(response.getBody()) : new ArrayList<>();
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get schema for a specific subject and version
     */
    public SchemaInfo getSchema(String schemaRegistryUrl, String subject, Integer version, String username, String password) {
        try {
            HttpHeaders headers = createHeaders(username, password);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = schemaRegistryUrl + "/subjects/" + subject + "/versions/" + version;
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
            );
            
            if (response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return new SchemaInfo(
                    jsonNode.get("id").asInt(),
                    jsonNode.get("subject").asText(),
                    jsonNode.get("version").asInt(),
                    jsonNode.get("schema").asText()
                );
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }
    
    /**
     * Get latest schema for a subject
     */
    public SchemaInfo getLatestSchema(String schemaRegistryUrl, String subject, String username, String password) {
        return getSchema(schemaRegistryUrl, subject, -1, username, password); // -1 means latest
    }
    
    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (username != null && password != null) {
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
        }
        
        return headers;
    }
    
    /**
     * Schema information holder
     */
    public static class SchemaInfo {
        private final Integer id;
        private final String subject;
        private final Integer version;
        private final String schema;
        
        public SchemaInfo(Integer id, String subject, Integer version, String schema) {
            this.id = id;
            this.subject = subject;
            this.version = version;
            this.schema = schema;
        }
        
        public Integer getId() { return id; }
        public String getSubject() { return subject; }
        public Integer getVersion() { return version; }
        public String getSchema() { return schema; }
    }
}