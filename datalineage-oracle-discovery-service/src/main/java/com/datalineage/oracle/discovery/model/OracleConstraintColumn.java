package com.datalineage.oracle.discovery.model;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing constraint column mapping in Oracle
 */
@Entity
@Table(name = "oracle_constraint_columns")
public class OracleConstraintColumn extends BaseEntity {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "owner", nullable = false)
    private String owner;
    
    @NotBlank
    @Column(name = "constraint_name", nullable = false)
    private String constraintName;
    
    @NotBlank
    @Column(name = "table_name", nullable = false)
    private String tableName;
    
    @NotBlank
    @Column(name = "column_name", nullable = false)
    private String columnName;
    
    @Column(name = "position")
    private Integer position;
    
    @NotBlank
    @Column(name = "oracle_connection_id", nullable = false)
    private String oracleConnectionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oracle_constraint_id")
    private OracleConstraint oracleConstraint;
    
    // Constructors
    public OracleConstraintColumn() {
        super();
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public OracleConstraintColumn(String owner, String constraintName, String tableName, 
                                 String columnName, String oracleConnectionId) {
        this();
        this.owner = owner;
        this.constraintName = constraintName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.oracleConnectionId = oracleConnectionId;
    }
    
    // Getters and Setters
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getConstraintName() {
        return constraintName;
    }
    
    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public String getOracleConnectionId() {
        return oracleConnectionId;
    }
    
    public void setOracleConnectionId(String oracleConnectionId) {
        this.oracleConnectionId = oracleConnectionId;
    }
    
    public OracleConstraint getOracleConstraint() {
        return oracleConstraint;
    }
    
    public void setOracleConstraint(OracleConstraint oracleConstraint) {
        this.oracleConstraint = oracleConstraint;
    }
}