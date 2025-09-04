package com.datalineage.oracle.discovery.model;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an Oracle constraint metadata
 */
@Entity
@Table(name = "oracle_constraints")
public class OracleConstraint extends BaseEntity {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "owner", nullable = false)
    private String owner;
    
    @NotBlank
    @Column(name = "constraint_name", nullable = false)
    private String constraintName;
    
    @Column(name = "constraint_type")
    private String constraintType;
    
    @NotBlank
    @Column(name = "table_name", nullable = false)
    private String tableName;
    
    @Column(name = "search_condition", length = 4000)
    private String searchCondition;
    
    @Column(name = "search_condition_vc", length = 4000)
    private String searchConditionVc;
    
    @Column(name = "r_owner")
    private String rOwner;
    
    @Column(name = "r_constraint_name")
    private String rConstraintName;
    
    @Column(name = "delete_rule")
    private String deleteRule;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "deferrable")
    private String deferrable;
    
    @Column(name = "deferred")
    private String deferred;
    
    @Column(name = "validated")
    private String validated;
    
    @Column(name = "generated")
    private String generated;
    
    @Column(name = "bad")
    private String bad;
    
    @Column(name = "rely")
    private String rely;
    
    @Column(name = "enable_disable")
    private String enableDisable;
    
    @Column(name = "index_owner")
    private String indexOwner;
    
    @Column(name = "index_name")
    private String indexName;
    
    @Column(name = "invalid")
    private String invalid;
    
    @Column(name = "view_related")
    private String viewRelated;
    
    @Column(name = "origin_con_id")
    private Integer originConId;
    
    @NotBlank
    @Column(name = "oracle_connection_id", nullable = false)
    private String oracleConnectionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oracle_table_id")
    private OracleTable oracleTable;
    
    @OneToMany(mappedBy = "oracleConstraint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OracleConstraintColumn> constraintColumns = new ArrayList<>();
    
    // Constructors
    public OracleConstraint() {
        super();
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public OracleConstraint(String owner, String constraintName, String tableName, String oracleConnectionId) {
        this();
        this.owner = owner;
        this.constraintName = constraintName;
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
    
    public String getConstraintName() {
        return constraintName;
    }
    
    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }
    
    public String getConstraintType() {
        return constraintType;
    }
    
    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getSearchCondition() {
        return searchCondition;
    }
    
    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }
    
    public String getSearchConditionVc() {
        return searchConditionVc;
    }
    
    public void setSearchConditionVc(String searchConditionVc) {
        this.searchConditionVc = searchConditionVc;
    }
    
    public String getrOwner() {
        return rOwner;
    }
    
    public void setrOwner(String rOwner) {
        this.rOwner = rOwner;
    }
    
    public String getrConstraintName() {
        return rConstraintName;
    }
    
    public void setrConstraintName(String rConstraintName) {
        this.rConstraintName = rConstraintName;
    }
    
    public String getDeleteRule() {
        return deleteRule;
    }
    
    public void setDeleteRule(String deleteRule) {
        this.deleteRule = deleteRule;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDeferrable() {
        return deferrable;
    }
    
    public void setDeferrable(String deferrable) {
        this.deferrable = deferrable;
    }
    
    public String getDeferred() {
        return deferred;
    }
    
    public void setDeferred(String deferred) {
        this.deferred = deferred;
    }
    
    public String getValidated() {
        return validated;
    }
    
    public void setValidated(String validated) {
        this.validated = validated;
    }
    
    public String getGenerated() {
        return generated;
    }
    
    public void setGenerated(String generated) {
        this.generated = generated;
    }
    
    public String getBad() {
        return bad;
    }
    
    public void setBad(String bad) {
        this.bad = bad;
    }
    
    public String getRely() {
        return rely;
    }
    
    public void setRely(String rely) {
        this.rely = rely;
    }
    
    public String getEnableDisable() {
        return enableDisable;
    }
    
    public void setEnableDisable(String enableDisable) {
        this.enableDisable = enableDisable;
    }
    
    public String getIndexOwner() {
        return indexOwner;
    }
    
    public void setIndexOwner(String indexOwner) {
        this.indexOwner = indexOwner;
    }
    
    public String getIndexName() {
        return indexName;
    }
    
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    
    public String getInvalid() {
        return invalid;
    }
    
    public void setInvalid(String invalid) {
        this.invalid = invalid;
    }
    
    public String getViewRelated() {
        return viewRelated;
    }
    
    public void setViewRelated(String viewRelated) {
        this.viewRelated = viewRelated;
    }
    
    public Integer getOriginConId() {
        return originConId;
    }
    
    public void setOriginConId(Integer originConId) {
        this.originConId = originConId;
    }
    
    public String getOracleConnectionId() {
        return oracleConnectionId;
    }
    
    public void setOracleConnectionId(String oracleConnectionId) {
        this.oracleConnectionId = oracleConnectionId;
    }
    
    public OracleTable getOracleTable() {
        return oracleTable;
    }
    
    public void setOracleTable(OracleTable oracleTable) {
        this.oracleTable = oracleTable;
    }
    
    public List<OracleConstraintColumn> getConstraintColumns() {
        return constraintColumns;
    }
    
    public void setConstraintColumns(List<OracleConstraintColumn> constraintColumns) {
        this.constraintColumns = constraintColumns;
    }
}