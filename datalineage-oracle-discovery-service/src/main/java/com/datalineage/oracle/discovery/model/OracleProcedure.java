package com.datalineage.oracle.discovery.model;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing an Oracle stored procedure metadata
 */
@Entity
@Table(name = "oracle_procedures")
public class OracleProcedure extends BaseEntity {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "owner", nullable = false)
    private String owner;
    
    @NotBlank
    @Column(name = "object_name", nullable = false)
    private String objectName;
    
    @Column(name = "procedure_name")
    private String procedureName;
    
    @Column(name = "object_type")
    private String objectType;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "aggregate")
    private String aggregate;
    
    @Column(name = "pipelined")
    private String pipelined;
    
    @Column(name = "impltypeowner")
    private String implTypeOwner;
    
    @Column(name = "impltypename")
    private String implTypeName;
    
    @Column(name = "parallel")
    private String parallel;
    
    @Column(name = "interface")
    private String interfaceType;
    
    @Column(name = "deterministic")
    private String deterministic;
    
    @Column(name = "authid")
    private String authId;
    
    @Column(name = "result_cache")
    private String resultCache;
    
    @Column(name = "origin_con_id")
    private Integer originConId;
    
    @Column(name = "polymorphic")
    private String polymorphic;
    
    @NotBlank
    @Column(name = "oracle_connection_id", nullable = false)
    private String oracleConnectionId;
    
    // Constructors
    public OracleProcedure() {
        super();
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public OracleProcedure(String owner, String objectName, String oracleConnectionId) {
        this();
        this.owner = owner;
        this.objectName = objectName;
        this.oracleConnectionId = oracleConnectionId;
    }
    
    // Getters and Setters
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getObjectName() {
        return objectName;
    }
    
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public String getProcedureName() {
        return procedureName;
    }
    
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }
    
    public String getObjectType() {
        return objectType;
    }
    
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAggregate() {
        return aggregate;
    }
    
    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }
    
    public String getPipelined() {
        return pipelined;
    }
    
    public void setPipelined(String pipelined) {
        this.pipelined = pipelined;
    }
    
    public String getImplTypeOwner() {
        return implTypeOwner;
    }
    
    public void setImplTypeOwner(String implTypeOwner) {
        this.implTypeOwner = implTypeOwner;
    }
    
    public String getImplTypeName() {
        return implTypeName;
    }
    
    public void setImplTypeName(String implTypeName) {
        this.implTypeName = implTypeName;
    }
    
    public String getParallel() {
        return parallel;
    }
    
    public void setParallel(String parallel) {
        this.parallel = parallel;
    }
    
    public String getInterfaceType() {
        return interfaceType;
    }
    
    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }
    
    public String getDeterministic() {
        return deterministic;
    }
    
    public void setDeterministic(String deterministic) {
        this.deterministic = deterministic;
    }
    
    public String getAuthId() {
        return authId;
    }
    
    public void setAuthId(String authId) {
        this.authId = authId;
    }
    
    public String getResultCache() {
        return resultCache;
    }
    
    public void setResultCache(String resultCache) {
        this.resultCache = resultCache;
    }
    
    public Integer getOriginConId() {
        return originConId;
    }
    
    public void setOriginConId(Integer originConId) {
        this.originConId = originConId;
    }
    
    public String getPolymorphic() {
        return polymorphic;
    }
    
    public void setPolymorphic(String polymorphic) {
        this.polymorphic = polymorphic;
    }
    
    public String getOracleConnectionId() {
        return oracleConnectionId;
    }
    
    public void setOracleConnectionId(String oracleConnectionId) {
        this.oracleConnectionId = oracleConnectionId;
    }
}