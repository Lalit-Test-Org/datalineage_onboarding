package com.datalineage.oracle.discovery.service;

import com.datalineage.oracle.discovery.dto.*;
import com.datalineage.oracle.discovery.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for transforming Oracle metadata to graph data structure
 */
@Service
public class OracleGraphService {

    /**
     * Transform metadata discovery response to graph data structure
     */
    public GraphData transformMetadataToGraph(MetadataDiscoveryResponse metadata) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> edges = new ArrayList<>();

        // Create database schema node
        GraphNode schemaNode = createSchemaNode(metadata);
        nodes.add(schemaNode);

        // Create table nodes and connect to schema
        if (metadata.getTables() != null) {
            for (OracleTable table : metadata.getTables()) {
                GraphNode tableNode = createTableNode(table);
                nodes.add(tableNode);

                // Connect table to schema
                GraphEdge schemaTableEdge = createEdge(
                    "schema-table-" + table.getId(),
                    schemaNode.getId(),
                    tableNode.getId(),
                    "contains",
                    Map.of("relationship", "schema contains table")
                );
                edges.add(schemaTableEdge);
            }
        }

        // Create column nodes and connect to tables
        if (metadata.getColumns() != null) {
            for (OracleColumn column : metadata.getColumns()) {
                GraphNode columnNode = createColumnNode(column);
                nodes.add(columnNode);

                // Find parent table and connect
                String parentTableId = findTableId(metadata.getTables(), column.getOwner(), column.getTableName());
                if (parentTableId != null) {
                    GraphEdge tableColumnEdge = createEdge(
                        "table-column-" + column.getId(),
                        parentTableId,
                        columnNode.getId(),
                        "contains",
                        Map.of("relationship", "table contains column")
                    );
                    edges.add(tableColumnEdge);
                }
            }
        }

        // Create procedure nodes and connect to schema
        if (metadata.getProcedures() != null) {
            for (OracleProcedure procedure : metadata.getProcedures()) {
                GraphNode procedureNode = createProcedureNode(procedure);
                nodes.add(procedureNode);

                // Connect procedure to schema
                GraphEdge schemaProcedureEdge = createEdge(
                    "schema-procedure-" + procedure.getId(),
                    schemaNode.getId(),
                    procedureNode.getId(),
                    "contains",
                    Map.of("relationship", "schema contains procedure")
                );
                edges.add(schemaProcedureEdge);
            }
        }

        // Create constraint nodes and relationships
        if (metadata.getConstraints() != null) {
            for (OracleConstraint constraint : metadata.getConstraints()) {
                GraphNode constraintNode = createConstraintNode(constraint);
                nodes.add(constraintNode);

                // Find related table and connect
                String relatedTableId = findTableId(metadata.getTables(), constraint.getOwner(), constraint.getTableName());
                if (relatedTableId != null) {
                    GraphEdge tableConstraintEdge = createEdge(
                        "table-constraint-" + constraint.getId(),
                        relatedTableId,
                        constraintNode.getId(),
                        "relationship",
                        Map.of("relationship", "table has constraint")
                    );
                    edges.add(tableConstraintEdge);
                }

                // Handle foreign key relationships
                if ("R".equals(constraint.getConstraintType()) && 
                    constraint.getrOwner() != null && constraint.getrConstraintName() != null) {
                    
                    String referencedTableId = findTableIdByOwner(metadata.getTables(), constraint.getrOwner());
                    if (referencedTableId != null) {
                        GraphEdge foreignKeyEdge = createEdge(
                            "fk-" + constraint.getId(),
                            constraintNode.getId(),
                            referencedTableId,
                            "foreign_key",
                            Map.of(
                                "relationship", "foreign key references",
                                "referencedConstraint", constraint.getrConstraintName()
                            )
                        );
                        edges.add(foreignKeyEdge);
                    }
                }
            }
        }

        GraphData graphData = new GraphData(nodes, edges);
        graphData.setStatistics(generateGraphStatistics(nodes, edges));
        
        return graphData;
    }

    /**
     * Transform metadata for a specific table to graph data structure
     */
    public GraphData transformTableToGraph(MetadataDiscoveryResponse metadata, String tableName, String owner) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> edges = new ArrayList<>();

        // Find the specific table
        OracleTable targetTable = null;
        if (metadata.getTables() != null) {
            targetTable = metadata.getTables().stream()
                .filter(t -> t.getTableName().equals(tableName) && 
                           (owner == null || t.getOwner().equals(owner)))
                .findFirst()
                .orElse(null);
        }

        if (targetTable == null) {
            return new GraphData(nodes, edges);
        }

        // Create table node
        GraphNode tableNode = createTableNode(targetTable);
        nodes.add(tableNode);

        // Add columns for this table
        if (metadata.getColumns() != null) {
            for (OracleColumn column : metadata.getColumns()) {
                if (column.getTableName().equals(tableName) && 
                    (owner == null || column.getOwner().equals(owner))) {
                    
                    GraphNode columnNode = createColumnNode(column);
                    nodes.add(columnNode);

                    GraphEdge tableColumnEdge = createEdge(
                        "table-column-" + column.getId(),
                        tableNode.getId(),
                        columnNode.getId(),
                        "contains",
                        Map.of("relationship", "table contains column")
                    );
                    edges.add(tableColumnEdge);
                }
            }
        }

        // Add constraints for this table
        if (metadata.getConstraints() != null) {
            for (OracleConstraint constraint : metadata.getConstraints()) {
                if (constraint.getTableName().equals(tableName) && 
                    (owner == null || constraint.getOwner().equals(owner))) {
                    
                    GraphNode constraintNode = createConstraintNode(constraint);
                    nodes.add(constraintNode);

                    GraphEdge tableConstraintEdge = createEdge(
                        "table-constraint-" + constraint.getId(),
                        tableNode.getId(),
                        constraintNode.getId(),
                        "relationship",
                        Map.of("relationship", "table has constraint")
                    );
                    edges.add(tableConstraintEdge);
                }
            }
        }

        GraphData graphData = new GraphData(nodes, edges);
        graphData.setStatistics(generateGraphStatistics(nodes, edges));
        
        return graphData;
    }

    private GraphNode createSchemaNode(MetadataDiscoveryResponse metadata) {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put("connectionId", metadata.getConnectionId());
        nodeMetadata.put("type", "Oracle Schema");
        if (metadata.getStatistics() != null) {
            nodeMetadata.put("statistics", metadata.getStatistics());
        }

        return new GraphNode(
            "schema-" + metadata.getConnectionId(),
            "Schema (" + metadata.getConnectionId() + ")",
            "schema",
            nodeMetadata
        );
    }

    private GraphNode createTableNode(OracleTable table) {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put("id", table.getId());
        nodeMetadata.put("owner", table.getOwner());
        nodeMetadata.put("tableName", table.getTableName());
        nodeMetadata.put("tableType", table.getTableType());
        nodeMetadata.put("fullName", table.getOwner() + "." + table.getTableName());
        nodeMetadata.put("type", "Oracle Table");
        if (table.getNumRows() != null) {
            nodeMetadata.put("numRows", table.getNumRows());
        }
        if (table.getTablespaceName() != null) {
            nodeMetadata.put("tablespace", table.getTablespaceName());
        }

        return new GraphNode(
            "table-" + table.getId(),
            table.getOwner() + "." + table.getTableName(),
            "table",
            nodeMetadata
        );
    }

    private GraphNode createColumnNode(OracleColumn column) {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put("id", column.getId());
        nodeMetadata.put("owner", column.getOwner());
        nodeMetadata.put("tableName", column.getTableName());
        nodeMetadata.put("columnName", column.getColumnName());
        nodeMetadata.put("dataType", column.getDataType());
        nodeMetadata.put("nullable", column.getNullable());
        nodeMetadata.put("fullName", column.getOwner() + "." + column.getTableName() + "." + column.getColumnName());
        nodeMetadata.put("type", "Oracle Column");
        if (column.getDataLength() != null) {
            nodeMetadata.put("dataLength", column.getDataLength());
        }
        if (column.getDataPrecision() != null) {
            nodeMetadata.put("dataPrecision", column.getDataPrecision());
        }
        if (column.getDataScale() != null) {
            nodeMetadata.put("dataScale", column.getDataScale());
        }

        return new GraphNode(
            "column-" + column.getId(),
            column.getColumnName(),
            "column",
            nodeMetadata
        );
    }

    private GraphNode createProcedureNode(OracleProcedure procedure) {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put("id", procedure.getId());
        nodeMetadata.put("owner", procedure.getOwner());
        nodeMetadata.put("procedureName", procedure.getProcedureName());
        nodeMetadata.put("objectType", procedure.getObjectType());
        nodeMetadata.put("fullName", procedure.getOwner() + "." + procedure.getProcedureName());
        nodeMetadata.put("type", "Oracle Procedure");
        if (procedure.getStatus() != null) {
            nodeMetadata.put("status", procedure.getStatus());
        }

        return new GraphNode(
            "procedure-" + procedure.getId(),
            procedure.getOwner() + "." + procedure.getProcedureName(),
            "procedure",
            nodeMetadata
        );
    }

    private GraphNode createConstraintNode(OracleConstraint constraint) {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put("id", constraint.getId());
        nodeMetadata.put("owner", constraint.getOwner());
        nodeMetadata.put("constraintName", constraint.getConstraintName());
        nodeMetadata.put("constraintType", constraint.getConstraintType());
        nodeMetadata.put("tableName", constraint.getTableName());
        nodeMetadata.put("fullName", constraint.getOwner() + "." + constraint.getConstraintName());
        nodeMetadata.put("type", "Oracle Constraint");
        if (constraint.getStatus() != null) {
            nodeMetadata.put("status", constraint.getStatus());
        }
        if (constraint.getrOwner() != null) {
            nodeMetadata.put("referencedOwner", constraint.getrOwner());
        }
        if (constraint.getrConstraintName() != null) {
            nodeMetadata.put("referencedConstraint", constraint.getrConstraintName());
        }

        return new GraphNode(
            "constraint-" + constraint.getId(),
            constraint.getConstraintName(),
            "constraint",
            nodeMetadata
        );
    }

    private GraphEdge createEdge(String id, String source, String target, String type, Map<String, Object> metadata) {
        return new GraphEdge(id, source, target, type, metadata);
    }

    private String findTableId(List<OracleTable> tables, String owner, String tableName) {
        if (tables == null) return null;
        
        return tables.stream()
            .filter(t -> t.getOwner().equals(owner) && t.getTableName().equals(tableName))
            .findFirst()
            .map(t -> "table-" + t.getId())
            .orElse(null);
    }

    private GraphData.GraphStatistics generateGraphStatistics(List<GraphNode> nodes, List<GraphEdge> edges) {
        Map<String, Integer> nodeTypeBreakdown = new HashMap<>();
        Map<String, Integer> edgeTypeBreakdown = new HashMap<>();

        // Count node types
        for (GraphNode node : nodes) {
            nodeTypeBreakdown.merge(node.getType(), 1, Integer::sum);
        }

        // Count edge types
        for (GraphEdge edge : edges) {
            edgeTypeBreakdown.merge(edge.getType(), 1, Integer::sum);
        }

        return new GraphData.GraphStatistics(
            nodes.size(),
            edges.size(),
            nodeTypeBreakdown,
            edgeTypeBreakdown
        );
    }
}