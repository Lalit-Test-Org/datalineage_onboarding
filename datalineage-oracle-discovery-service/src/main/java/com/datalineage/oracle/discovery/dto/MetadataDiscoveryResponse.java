package com.datalineage.oracle.discovery.dto;

import com.datalineage.oracle.discovery.model.*;
import java.util.List;

/**
 * DTO for Oracle metadata discovery response
 */
public class MetadataDiscoveryResponse {
    
    private String connectionId;
    private List<OracleTable> tables;
    private List<OracleColumn> columns;
    private List<OracleProcedure> procedures;
    private List<OracleConstraint> constraints;
    
    private DiscoveryStatistics statistics;
    
    public static class DiscoveryStatistics {
        private int totalTables;
        private int totalColumns;
        private int totalProcedures;
        private int totalConstraints;
        private long discoveryTimeMs;
        
        // Constructors
        public DiscoveryStatistics() {}
        
        public DiscoveryStatistics(int totalTables, int totalColumns, 
                                 int totalProcedures, int totalConstraints, long discoveryTimeMs) {
            this.totalTables = totalTables;
            this.totalColumns = totalColumns;
            this.totalProcedures = totalProcedures;
            this.totalConstraints = totalConstraints;
            this.discoveryTimeMs = discoveryTimeMs;
        }
        
        // Getters and Setters
        public int getTotalTables() {
            return totalTables;
        }
        
        public void setTotalTables(int totalTables) {
            this.totalTables = totalTables;
        }
        
        public int getTotalColumns() {
            return totalColumns;
        }
        
        public void setTotalColumns(int totalColumns) {
            this.totalColumns = totalColumns;
        }
        
        public int getTotalProcedures() {
            return totalProcedures;
        }
        
        public void setTotalProcedures(int totalProcedures) {
            this.totalProcedures = totalProcedures;
        }
        
        public int getTotalConstraints() {
            return totalConstraints;
        }
        
        public void setTotalConstraints(int totalConstraints) {
            this.totalConstraints = totalConstraints;
        }
        
        public long getDiscoveryTimeMs() {
            return discoveryTimeMs;
        }
        
        public void setDiscoveryTimeMs(long discoveryTimeMs) {
            this.discoveryTimeMs = discoveryTimeMs;
        }
    }
    
    // Constructors
    public MetadataDiscoveryResponse() {}
    
    public MetadataDiscoveryResponse(String connectionId) {
        this.connectionId = connectionId;
    }
    
    // Getters and Setters
    public String getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
    
    public List<OracleTable> getTables() {
        return tables;
    }
    
    public void setTables(List<OracleTable> tables) {
        this.tables = tables;
    }
    
    public List<OracleColumn> getColumns() {
        return columns;
    }
    
    public void setColumns(List<OracleColumn> columns) {
        this.columns = columns;
    }
    
    public List<OracleProcedure> getProcedures() {
        return procedures;
    }
    
    public void setProcedures(List<OracleProcedure> procedures) {
        this.procedures = procedures;
    }
    
    public List<OracleConstraint> getConstraints() {
        return constraints;
    }
    
    public void setConstraints(List<OracleConstraint> constraints) {
        this.constraints = constraints;
    }
    
    public DiscoveryStatistics getStatistics() {
        return statistics;
    }
    
    public void setStatistics(DiscoveryStatistics statistics) {
        this.statistics = statistics;
    }
}