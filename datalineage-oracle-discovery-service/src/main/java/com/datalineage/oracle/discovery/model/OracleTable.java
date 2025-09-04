package com.datalineage.oracle.discovery.model;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an Oracle table metadata
 */
@Entity
@Table(name = "oracle_tables")
public class OracleTable extends BaseEntity {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "owner", nullable = false)
    private String owner;
    
    @NotBlank
    @Column(name = "table_name", nullable = false)
    private String tableName;
    
    @Column(name = "table_type")
    private String tableType;
    
    @Column(name = "tablespace_name")
    private String tablespaceName;
    
    @Column(name = "num_rows")
    private Long numRows;
    
    @Column(name = "blocks")
    private Long blocks;
    
    @Column(name = "avg_row_len")
    private Long avgRowLen;
    
    @Column(name = "sample_size")
    private Long sampleSize;
    
    @Column(name = "compression")
    private String compression;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "temporary")
    private String temporary;
    
    @Column(name = "comments", length = 4000)
    private String comments;
    
    @NotBlank
    @Column(name = "oracle_connection_id", nullable = false)
    private String oracleConnectionId;
    
    @OneToMany(mappedBy = "oracleTable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OracleColumn> columns = new ArrayList<>();
    
    @OneToMany(mappedBy = "oracleTable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OracleConstraint> constraints = new ArrayList<>();
    
    // Constructors
    public OracleTable() {
        super();
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public OracleTable(String owner, String tableName, String oracleConnectionId) {
        this();
        this.owner = owner;
        this.tableName = tableName;
        this.oracleConnectionId = oracleConnectionId;
    }
    
    // Getters and Setters
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableType() {
        return tableType;
    }
    
    public void setTableType(String tableType) {
        this.tableType = tableType;
    }
    
    public String getTablespaceName() {
        return tablespaceName;
    }
    
    public void setTablespaceName(String tablespaceName) {
        this.tablespaceName = tablespaceName;
    }
    
    public Long getNumRows() {
        return numRows;
    }
    
    public void setNumRows(Long numRows) {
        this.numRows = numRows;
    }
    
    public Long getBlocks() {
        return blocks;
    }
    
    public void setBlocks(Long blocks) {
        this.blocks = blocks;
    }
    
    public Long getAvgRowLen() {
        return avgRowLen;
    }
    
    public void setAvgRowLen(Long avgRowLen) {
        this.avgRowLen = avgRowLen;
    }
    
    public Long getSampleSize() {
        return sampleSize;
    }
    
    public void setSampleSize(Long sampleSize) {
        this.sampleSize = sampleSize;
    }
    
    public String getCompression() {
        return compression;
    }
    
    public void setCompression(String compression) {
        this.compression = compression;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTemporary() {
        return temporary;
    }
    
    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public String getOracleConnectionId() {
        return oracleConnectionId;
    }
    
    public void setOracleConnectionId(String oracleConnectionId) {
        this.oracleConnectionId = oracleConnectionId;
    }
    
    public List<OracleColumn> getColumns() {
        return columns;
    }
    
    public void setColumns(List<OracleColumn> columns) {
        this.columns = columns;
    }
    
    public List<OracleConstraint> getConstraints() {
        return constraints;
    }
    
    public void setConstraints(List<OracleConstraint> constraints) {
        this.constraints = constraints;
    }
}