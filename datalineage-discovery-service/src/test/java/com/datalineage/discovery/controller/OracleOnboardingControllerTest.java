package com.datalineage.discovery.controller;

import com.datalineage.discovery.dto.OracleConnectionResponse;
import com.datalineage.discovery.dto.OracleOnboardingRequest;
import com.datalineage.discovery.service.OracleOnboardingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for OracleOnboardingController
 */
@WebMvcTest(OracleOnboardingController.class)
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
@ActiveProfiles("test")
class OracleOnboardingControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OracleOnboardingService oracleOnboardingService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testOnboardOracleDatabase_Success() throws Exception {
        // Given
        OracleOnboardingRequest request = createValidOnboardingRequest();
        OracleConnectionResponse response = createMockConnectionResponse();
        
        when(oracleOnboardingService.onboardOracleDatabase(any(OracleOnboardingRequest.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/v1/oracle/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Oracle database onboarded successfully"))
                .andExpect(jsonPath("$.data.id").value("test-id"))
                .andExpect(jsonPath("$.data.connectionName").value("Test Oracle DB"));
    }
    
    @Test
    void testOnboardOracleDatabase_InvalidRequest() throws Exception {
        // Given
        OracleOnboardingRequest request = new OracleOnboardingRequest();
        // Missing required fields
        
        // When & Then
        mockMvc.perform(post("/api/v1/oracle/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetAllConnections_Success() throws Exception {
        // Given
        OracleConnectionResponse connection1 = createMockConnectionResponse();
        OracleConnectionResponse connection2 = createMockConnectionResponse();
        connection2.setId("test-id-2");
        connection2.setConnectionName("Test Oracle DB 2");
        
        when(oracleOnboardingService.getAllConnections())
            .thenReturn(Arrays.asList(connection1, connection2));
        
        // When & Then
        mockMvc.perform(get("/api/v1/oracle/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("test-id"))
                .andExpect(jsonPath("$.data[1].id").value("test-id-2"));
    }
    
    @Test
    void testGetConnectionById_Success() throws Exception {
        // Given
        String connectionId = "test-id";
        OracleConnectionResponse response = createMockConnectionResponse();
        
        when(oracleOnboardingService.getConnectionById(connectionId))
            .thenReturn(Optional.of(response));
        
        // When & Then
        mockMvc.perform(get("/api/v1/oracle/connections/{id}", connectionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("test-id"))
                .andExpect(jsonPath("$.data.connectionName").value("Test Oracle DB"));
    }
    
    @Test
    void testGetConnectionById_NotFound() throws Exception {
        // Given
        String connectionId = "non-existent-id";
        
        when(oracleOnboardingService.getConnectionById(connectionId))
            .thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/v1/oracle/connections/{id}", connectionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("CONNECTION_NOT_FOUND"));
    }
    
    @Test
    void testTestConnection_Success() throws Exception {
        // Given
        String connectionId = "test-id";
        Map<String, Object> testResult = new HashMap<>();
        testResult.put("connectionValid", true);
        testResult.put("connectionId", connectionId);
        testResult.put("testedAt", LocalDateTime.now().toString());
        
        when(oracleOnboardingService.testConnection(connectionId))
            .thenReturn(testResult);
        
        // When & Then
        mockMvc.perform(post("/api/v1/oracle/connections/{id}/test", connectionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.connectionValid").value(true));
    }
    
    @Test
    void testDeleteConnection_Success() throws Exception {
        // Given
        String connectionId = "test-id";
        
        // When & Then
        mockMvc.perform(delete("/api/v1/oracle/connections/{id}", connectionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/oracle/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("oracle-onboarding-service"));
    }
    
    private OracleOnboardingRequest createValidOnboardingRequest() {
        OracleOnboardingRequest request = new OracleOnboardingRequest();
        request.setConnectionName("Test Oracle DB");
        request.setDescription("Test connection");
        request.setHost("localhost");
        request.setPort(1521);
        request.setServiceName("XE");
        request.setAuthenticationType(OracleOnboardingRequest.AuthenticationType.DIRECT);
        request.setUsername("testuser");
        request.setPassword("testpass");
        request.setAutoDiscoverMetadata(true);
        return request;
    }
    
    private OracleConnectionResponse createMockConnectionResponse() {
        OracleConnectionResponse response = new OracleConnectionResponse();
        response.setId("test-id");
        response.setConnectionName("Test Oracle DB");
        response.setDescription("Test connection");
        response.setHost("localhost");
        response.setPort(1521);
        response.setServiceName("XE");
        response.setAuthenticationType(OracleConnectionResponse.AuthenticationType.DIRECT);
        response.setUsername("testuser");
        response.setStatus("ACTIVE");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }
}