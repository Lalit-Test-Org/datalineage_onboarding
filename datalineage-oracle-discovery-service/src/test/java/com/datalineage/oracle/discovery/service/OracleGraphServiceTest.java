package com.datalineage.oracle.discovery.service;

import com.datalineage.oracle.discovery.dto.GraphData;
import com.datalineage.oracle.discovery.dto.MetadataDiscoveryResponse;
import com.datalineage.oracle.discovery.model.OracleColumn;
import com.datalineage.oracle.discovery.model.OracleConstraint;
import com.datalineage.oracle.discovery.model.OracleProcedure;
import com.datalineage.oracle.discovery.model.OracleTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OracleGraphService
 */
class OracleGraphServiceTest {

    private OracleGraphService graphService;

    @BeforeEach
    void setUp() {
        graphService = new OracleGraphService();
    }

    @Test
    void testTransformMetadataToGraph_withBasicTable() {
        // Given
        MetadataDiscoveryResponse metadata = createSampleMetadata();
        
        // When
        GraphData graphData = graphService.transformMetadataToGraph(metadata);
        
        // Then
        assertNotNull(graphData);
        assertNotNull(graphData.getNodes());
        assertNotNull(graphData.getEdges());
        
        // Should have schema node + table node + column nodes + procedure node + constraint node
        assertTrue(graphData.getNodes().size() >= 5);
        
        // Should have edges connecting schema to table, table to columns, etc.
        assertTrue(graphData.getEdges().size() >= 4);
        
        // Verify statistics are generated
        assertNotNull(graphData.getStatistics());
        assertTrue(graphData.getStatistics().getTotalNodes() > 0);
        assertTrue(graphData.getStatistics().getTotalEdges() > 0);
    }

    @Test
    void testTransformTableToGraph_specificTable() {
        // Given
        MetadataDiscoveryResponse metadata = createSampleMetadata();
        String tableName = "EMPLOYEES";
        String owner = "HR";
        
        // When
        GraphData graphData = graphService.transformTableToGraph(metadata, tableName, owner);
        
        // Then
        assertNotNull(graphData);
        assertNotNull(graphData.getNodes());
        assertNotNull(graphData.getEdges());
        
        // Should have table node + its columns + constraints
        assertTrue(graphData.getNodes().size() >= 3);
        
        // Verify that the main table node exists
        boolean tableNodeExists = graphData.getNodes().stream()
            .anyMatch(node -> node.getType().equals("table") && 
                     node.getLabel().contains(tableName));
        assertTrue(tableNodeExists);
    }

    @Test
    void testTransformMetadataToGraph_emptyMetadata() {
        // Given
        MetadataDiscoveryResponse metadata = new MetadataDiscoveryResponse("test-connection");
        
        // When
        GraphData graphData = graphService.transformMetadataToGraph(metadata);
        
        // Then
        assertNotNull(graphData);
        assertNotNull(graphData.getNodes());
        assertNotNull(graphData.getEdges());
        
        // Should have at least the schema node
        assertEquals(1, graphData.getNodes().size());
        assertEquals("schema", graphData.getNodes().get(0).getType());
        assertEquals(0, graphData.getEdges().size());
    }

    @Test
    void testTransformMetadataToGraph_constraintRelationships() {
        // Given
        MetadataDiscoveryResponse metadata = createMetadataWithConstraints();
        
        // When
        GraphData graphData = graphService.transformMetadataToGraph(metadata);
        
        // Then
        assertNotNull(graphData);
        
        // Verify foreign key edges are created
        boolean foreignKeyEdgeExists = graphData.getEdges().stream()
            .anyMatch(edge -> edge.getType().equals("foreign_key"));
        assertTrue(foreignKeyEdgeExists);
    }

    private MetadataDiscoveryResponse createSampleMetadata() {
        MetadataDiscoveryResponse metadata = new MetadataDiscoveryResponse("test-connection");
        
        // Create sample table
        OracleTable table = new OracleTable("HR", "EMPLOYEES", "test-connection");
        table.setTableType("TABLE");
        table.setNumRows(100L);
        metadata.setTables(Arrays.asList(table));
        
        // Create sample columns
        OracleColumn col1 = new OracleColumn("HR", "EMPLOYEES", "ID", "test-connection");
        col1.setDataType("NUMBER");
        col1.setNullable("N");
        
        OracleColumn col2 = new OracleColumn("HR", "EMPLOYEES", "NAME", "test-connection");
        col2.setDataType("VARCHAR2");
        col2.setDataLength(100L);
        col2.setNullable("Y");
        
        metadata.setColumns(Arrays.asList(col1, col2));
        
        // Create sample procedure
        OracleProcedure procedure = new OracleProcedure("HR", "GET_EMPLOYEE", "test-connection");
        procedure.setObjectType("PROCEDURE");
        procedure.setStatus("VALID");
        metadata.setProcedures(Arrays.asList(procedure));
        
        // Create sample constraint
        OracleConstraint constraint = new OracleConstraint("HR", "EMP_PK", "EMPLOYEES", "test-connection");
        constraint.setConstraintType("P");
        constraint.setStatus("ENABLED");
        metadata.setConstraints(Arrays.asList(constraint));
        
        // Create statistics
        MetadataDiscoveryResponse.DiscoveryStatistics stats = new MetadataDiscoveryResponse.DiscoveryStatistics(
            1, 2, 1, 1, 1000L
        );
        metadata.setStatistics(stats);
        
        return metadata;
    }

    private MetadataDiscoveryResponse createMetadataWithConstraints() {
        MetadataDiscoveryResponse metadata = new MetadataDiscoveryResponse("test-connection");
        
        // Create two tables
        OracleTable table1 = new OracleTable("HR", "EMPLOYEES", "test-connection");
        OracleTable table2 = new OracleTable("HR", "DEPARTMENTS", "test-connection");
        metadata.setTables(Arrays.asList(table1, table2));
        
        // Create foreign key constraint
        OracleConstraint fkConstraint = new OracleConstraint("HR", "EMP_DEPT_FK", "EMPLOYEES", "test-connection");
        fkConstraint.setConstraintType("R");
        fkConstraint.setrOwner("HR");
        fkConstraint.setrConstraintName("DEPT_PK");
        metadata.setConstraints(Arrays.asList(fkConstraint));
        
        return metadata;
    }
}