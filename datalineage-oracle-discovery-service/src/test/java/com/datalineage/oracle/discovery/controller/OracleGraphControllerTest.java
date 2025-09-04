package com.datalineage.oracle.discovery.controller;

import com.datalineage.oracle.discovery.dto.GraphData;
import com.datalineage.oracle.discovery.dto.OracleConnectionConfig;
import com.datalineage.oracle.discovery.service.OracleGraphService;
import com.datalineage.oracle.discovery.service.OracleMetadataExtractionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OracleGraphController
 */
@WebMvcTest(OracleGraphController.class)
class OracleGraphControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OracleMetadataExtractionService metadataExtractionService;

    @MockBean
    private OracleGraphService graphService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetSchemaGraph_success() throws Exception {
        // Given
        OracleConnectionConfig connectionConfig = createSampleConnectionConfig();
        GraphData mockGraphData = createMockGraphData();
        
        when(metadataExtractionService.discoverMetadata(any(), any())).thenReturn(any());
        when(graphService.transformMetadataToGraph(any())).thenReturn(mockGraphData);
        
        // When & Then
        mockMvc.perform(post("/api/v1/oracle-discovery/graph/schema/test-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectionConfig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Schema graph generated successfully"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.nodes").isArray())
                .andExpect(jsonPath("$.data.edges").isArray());
    }

    @Test
    void testGetTableGraph_success() throws Exception {
        // Given
        OracleConnectionConfig connectionConfig = createSampleConnectionConfig();
        GraphData mockGraphData = createMockGraphData();
        
        when(metadataExtractionService.discoverMetadata(any(), any())).thenReturn(any());
        when(graphService.transformTableToGraph(any(), anyString(), anyString())).thenReturn(mockGraphData);
        
        // When & Then
        mockMvc.perform(post("/api/v1/oracle-discovery/graph/table/test-connection/EMPLOYEES")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectionConfig))
                .param("owner", "HR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Table graph generated successfully"));
    }

    @Test
    void testGetSchemaGraph_invalidRequest() throws Exception {
        // Given - invalid connection config (missing required fields)
        OracleConnectionConfig invalidConfig = new OracleConnectionConfig();
        
        // When & Then
        mockMvc.perform(post("/api/v1/oracle-discovery/graph/schema/test-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidConfig)))
                .andExpect(status().isBadRequest());
    }

    private OracleConnectionConfig createSampleConnectionConfig() {
        OracleConnectionConfig config = new OracleConnectionConfig();
        config.setConnectionId("test-connection");
        config.setHost("localhost");
        config.setPort(1521);
        config.setServiceName("ORCL");
        config.setUsername("testuser");
        config.setPassword("testpass");
        config.setAuthenticationType(OracleConnectionConfig.AuthenticationType.DIRECT);
        return config;
    }

    private GraphData createMockGraphData() {
        GraphData graphData = new GraphData();
        // Add minimal mock data - in real implementation this would be more comprehensive
        graphData.setNodes(java.util.Collections.emptyList());
        graphData.setEdges(java.util.Collections.emptyList());
        
        GraphData.GraphStatistics stats = new GraphData.GraphStatistics();
        stats.setTotalNodes(0);
        stats.setTotalEdges(0);
        graphData.setStatistics(stats);
        
        return graphData;
    }
}