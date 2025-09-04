package com.datalineage.oracle.discovery.controller;

import com.datalineage.common.dto.ApiResponse;
import com.datalineage.oracle.discovery.dto.*;
import com.datalineage.oracle.discovery.service.OracleConnectionService;
import com.datalineage.oracle.discovery.service.OracleGraphService;
import com.datalineage.oracle.discovery.service.OracleMetadataExtractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * REST Controller for Oracle metadata graph operations
 */
@RestController
@RequestMapping("/api/v1/oracle-discovery/graph")
@CrossOrigin(origins = "*")
public class OracleGraphController {

    @Autowired
    private OracleConnectionService connectionService;

    @Autowired
    private OracleMetadataExtractionService metadataExtractionService;

    @Autowired
    private OracleGraphService graphService;

    /**
     * Get full schema graph for a connection
     */
    @PostMapping("/schema/{connectionId}")
    public ResponseEntity<ApiResponse<GraphData>> getSchemaGraph(
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
            MetadataDiscoveryRequest discoveryRequest = createDiscoveryRequest(
                connectionId, schemas, tablePatterns, tableTypes,
                includeTables, includeColumns, includeProcedures, includeConstraints,
                limit, offset
            );

            // Ensure connection ID matches
            connectionConfig.setConnectionId(connectionId);

            // Get metadata
            MetadataDiscoveryResponse metadata = metadataExtractionService.discoverMetadata(
                connectionConfig, discoveryRequest);

            // Transform to graph
            GraphData graphData = graphService.transformMetadataToGraph(metadata);

            return ResponseEntity.ok(ApiResponse.success("Schema graph generated successfully", graphData));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database error during graph generation: " + e.getMessage(), "SQL_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error during graph generation: " + e.getMessage(), "GRAPH_ERROR"));
        }
    }

    /**
     * Get graph data for a specific table
     */
    @PostMapping("/table/{connectionId}/{tableName}")
    public ResponseEntity<ApiResponse<GraphData>> getTableGraph(
            @PathVariable String connectionId,
            @PathVariable String tableName,
            @Valid @RequestBody OracleConnectionConfig connectionConfig,
            @RequestParam(required = false) String owner,
            @RequestParam(defaultValue = "true") boolean includeColumns,
            @RequestParam(defaultValue = "true") boolean includeConstraints) {

        try {
            // Create discovery request focused on the specific table
            MetadataDiscoveryRequest discoveryRequest = new MetadataDiscoveryRequest(connectionId);
            discoveryRequest.setTablePatterns(Arrays.asList(tableName));
            if (owner != null) {
                discoveryRequest.setSchemas(Arrays.asList(owner));
            }
            discoveryRequest.setIncludeTables(true);
            discoveryRequest.setIncludeColumns(includeColumns);
            discoveryRequest.setIncludeProcedures(false);
            discoveryRequest.setIncludeConstraints(includeConstraints);
            discoveryRequest.setLimit(1000);
            discoveryRequest.setOffset(0);

            // Ensure connection ID matches
            connectionConfig.setConnectionId(connectionId);

            // Get metadata
            MetadataDiscoveryResponse metadata = metadataExtractionService.discoverMetadata(
                connectionConfig, discoveryRequest);

            // Transform to graph focused on the table
            GraphData graphData = graphService.transformTableToGraph(metadata, tableName, owner);

            return ResponseEntity.ok(ApiResponse.success("Table graph generated successfully", graphData));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database error during table graph generation: " + e.getMessage(), "SQL_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error during table graph generation: " + e.getMessage(), "GRAPH_ERROR"));
        }
    }

    /**
     * Discover and transform metadata to graph in a single call
     */
    @PostMapping("/connections/{connectionId}/discover")
    public ResponseEntity<ApiResponse<GraphData>> discoverGraph(
            @PathVariable String connectionId,
            @Valid @RequestBody GraphDiscoveryRequest request) {

        try {
            // Ensure connection ID matches
            request.getConnectionConfig().setConnectionId(connectionId);

            // Get metadata
            MetadataDiscoveryResponse metadata = metadataExtractionService.discoverMetadata(
                request.getConnectionConfig(), request.getDiscoveryRequest());

            // Transform to graph
            GraphData graphData = graphService.transformMetadataToGraph(metadata);

            return ResponseEntity.ok(ApiResponse.success("Graph discovery completed successfully", graphData));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database error during graph discovery: " + e.getMessage(), "SQL_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error during graph discovery: " + e.getMessage(), "GRAPH_ERROR"));
        }
    }

    /**
     * Get graph metadata/statistics without full graph data
     */
    @PostMapping("/metadata/{connectionId}")
    public ResponseEntity<ApiResponse<GraphData.GraphStatistics>> getGraphMetadata(
            @PathVariable String connectionId,
            @Valid @RequestBody OracleConnectionConfig connectionConfig,
            @RequestParam(required = false) String[] schemas,
            @RequestParam(required = false) String[] tablePatterns,
            @RequestParam(required = false) String[] tableTypes) {

        try {
            // Create minimal discovery request just for statistics
            MetadataDiscoveryRequest discoveryRequest = createDiscoveryRequest(
                connectionId, schemas, tablePatterns, tableTypes,
                true, true, true, true, 10000, 0
            );

            // Ensure connection ID matches
            connectionConfig.setConnectionId(connectionId);

            // Get metadata
            MetadataDiscoveryResponse metadata = metadataExtractionService.discoverMetadata(
                connectionConfig, discoveryRequest);

            // Transform to graph and extract statistics
            GraphData graphData = graphService.transformMetadataToGraph(metadata);
            
            return ResponseEntity.ok(ApiResponse.success("Graph metadata retrieved successfully", graphData.getStatistics()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database error during graph metadata retrieval: " + e.getMessage(), "SQL_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error during graph metadata retrieval: " + e.getMessage(), "GRAPH_ERROR"));
        }
    }

    private MetadataDiscoveryRequest createDiscoveryRequest(
            String connectionId, String[] schemas, String[] tablePatterns, String[] tableTypes,
            boolean includeTables, boolean includeColumns, boolean includeProcedures, boolean includeConstraints,
            Integer limit, Integer offset) {

        MetadataDiscoveryRequest discoveryRequest = new MetadataDiscoveryRequest(connectionId);

        if (schemas != null && schemas.length > 0) {
            discoveryRequest.setSchemas(Arrays.asList(schemas));
        }
        if (tablePatterns != null && tablePatterns.length > 0) {
            discoveryRequest.setTablePatterns(Arrays.asList(tablePatterns));
        }
        if (tableTypes != null && tableTypes.length > 0) {
            discoveryRequest.setTableTypes(Arrays.asList(tableTypes));
        }

        discoveryRequest.setIncludeTables(includeTables);
        discoveryRequest.setIncludeColumns(includeColumns);
        discoveryRequest.setIncludeProcedures(includeProcedures);
        discoveryRequest.setIncludeConstraints(includeConstraints);
        discoveryRequest.setLimit(limit);
        discoveryRequest.setOffset(offset);

        return discoveryRequest;
    }

    /**
     * DTO for combined graph discovery request
     */
    public static class GraphDiscoveryRequest {
        @Valid
        private OracleConnectionConfig connectionConfig;
        
        @Valid
        private MetadataDiscoveryRequest discoveryRequest;

        // Constructors
        public GraphDiscoveryRequest() {}

        public GraphDiscoveryRequest(OracleConnectionConfig connectionConfig, MetadataDiscoveryRequest discoveryRequest) {
            this.connectionConfig = connectionConfig;
            this.discoveryRequest = discoveryRequest;
        }

        // Getters and Setters
        public OracleConnectionConfig getConnectionConfig() {
            return connectionConfig;
        }

        public void setConnectionConfig(OracleConnectionConfig connectionConfig) {
            this.connectionConfig = connectionConfig;
        }

        public MetadataDiscoveryRequest getDiscoveryRequest() {
            return discoveryRequest;
        }

        public void setDiscoveryRequest(MetadataDiscoveryRequest discoveryRequest) {
            this.discoveryRequest = discoveryRequest;
        }
    }
}