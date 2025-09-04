package com.datalineage.oracle.discovery.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO for Oracle metadata discovery request
 */
public class MetadataDiscoveryRequest {
    
    @NotBlank(message = "Connection ID is required")
    private String connectionId;
    
    private List<String> schemas; // If empty, discover all accessible schemas
    private List<String> tablePatterns; // SQL LIKE patterns for table names
    private List<String> tableTypes; // TABLE, VIEW, etc.
    
    private boolean includeTables = true;
    private boolean includeColumns = true;
    private boolean includeProcedures = true;
    private boolean includeConstraints = true;
    
    // Pagination
    private Integer limit = 1000;
    private Integer offset = 0;
    
    // Constructors
    public MetadataDiscoveryRequest() {}
    
    public MetadataDiscoveryRequest(String connectionId) {
        this.connectionId = connectionId;
    }
    
    // Getters and Setters
    public String getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
    
    public List<String> getSchemas() {
        return schemas;
    }
    
    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }
    
    public List<String> getTablePatterns() {
        return tablePatterns;
    }
    
    public void setTablePatterns(List<String> tablePatterns) {
        this.tablePatterns = tablePatterns;
    }
    
    public List<String> getTableTypes() {
        return tableTypes;
    }
    
    public void setTableTypes(List<String> tableTypes) {
        this.tableTypes = tableTypes;
    }
    
    public boolean isIncludeTables() {
        return includeTables;
    }
    
    public void setIncludeTables(boolean includeTables) {
        this.includeTables = includeTables;
    }
    
    public boolean isIncludeColumns() {
        return includeColumns;
    }
    
    public void setIncludeColumns(boolean includeColumns) {
        this.includeColumns = includeColumns;
    }
    
    public boolean isIncludeProcedures() {
        return includeProcedures;
    }
    
    public void setIncludeProcedures(boolean includeProcedures) {
        this.includeProcedures = includeProcedures;
    }
    
    public boolean isIncludeConstraints() {
        return includeConstraints;
    }
    
    public void setIncludeConstraints(boolean includeConstraints) {
        this.includeConstraints = includeConstraints;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public Integer getOffset() {
        return offset;
    }
    
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}