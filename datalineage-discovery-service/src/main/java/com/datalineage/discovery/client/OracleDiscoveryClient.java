package com.datalineage.discovery.client;

import com.datalineage.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Client for communicating with Oracle Discovery Service
 */
@Component
public class OracleDiscoveryClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${datalineage.oracle-discovery-service.url:http://localhost:8082}")
    private String oracleDiscoveryServiceUrl;
    
    public OracleDiscoveryClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Tests Oracle database connection via Oracle Discovery Service
     */
    public ApiResponse<Map<String, Object>> testConnection(Object connectionConfig) {
        try {
            String url = oracleDiscoveryServiceUrl + "/api/v1/oracle-discovery/test-connection";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Object> request = new HttpEntity<>(connectionConfig, headers);
            
            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, ApiResponse.class);
            
            return response.getBody();
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ApiResponse.error("Connection test failed: " + e.getMessage(), "CONNECTION_TEST_FAILED");
        } catch (Exception e) {
            return ApiResponse.error("Error communicating with Oracle Discovery Service: " + e.getMessage(), "SERVICE_ERROR");
        }
    }
    
    /**
     * Triggers metadata discovery via Oracle Discovery Service
     */
    public ApiResponse<Object> discoverMetadata(Object connectionConfig, Object discoveryRequest) {
        try {
            String url = oracleDiscoveryServiceUrl + "/api/v1/oracle-discovery/discover";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create a wrapper object containing both connection config and discovery request
            Map<String, Object> requestBody = Map.of(
                "connectionConfig", connectionConfig,
                "discoveryRequest", discoveryRequest
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, ApiResponse.class);
            
            return response.getBody();
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ApiResponse.error("Metadata discovery failed: " + e.getMessage(), "METADATA_DISCOVERY_FAILED");
        } catch (Exception e) {
            return ApiResponse.error("Error communicating with Oracle Discovery Service: " + e.getMessage(), "SERVICE_ERROR");
        }
    }
    
    /**
     * Checks if Oracle Discovery Service is healthy
     */
    public boolean isServiceHealthy() {
        try {
            String url = oracleDiscoveryServiceUrl + "/api/v1/oracle-discovery/health";
            ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}