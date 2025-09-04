package com.datalineage.oracle.discovery.model;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing an Oracle column metadata
 */
@Entity
@Table(name = "oracle_columns")
public class OracleColumn extends BaseEntity {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank
    @Column(name = "owner", nullable = false)
    private String owner;
    
    @NotBlank
    @Column(name = "table_name", nullable = false)
    private String tableName;
    
    @NotBlank
    @Column(name = "column_name", nullable = false)
    private String columnName;
    
    @Column(name = "data_type")
    private String dataType;
    
    @Column(name = "data_type_mod")
    private String dataTypeMod;
    
    @Column(name = "data_type_owner")
    private String dataTypeOwner;
    
    @Column(name = "data_length")
    private Long dataLength;
    
    @Column(name = "data_precision")
    private Integer dataPrecision;
    
    @Column(name = "data_scale")
    private Integer dataScale;
    
    @Column(name = "nullable")
    private String nullable;
    
    @Column(name = "column_id")
    private Integer columnId;
    
    @Column(name = "default_length")
    private Long defaultLength;
    
    @Column(name = "data_default", length = 4000)
    private String dataDefault;
    
    @Column(name = "num_distinct")
    private Long numDistinct;
    
    @Column(name = "low_value")
    private String lowValue;
    
    @Column(name = "high_value")
    private String highValue;
    
    @Column(name = "density")
    private Double density;
    
    @Column(name = "num_nulls")
    private Long numNulls;
    
    @Column(name = "num_buckets")
    private Integer numBuckets;
    
    @Column(name = "character_set_name")
    private String characterSetName;
    
    @Column(name = "char_col_decl_length")
    private Integer charColDeclLength;
    
    @Column(name = "global_stats")
    private String globalStats;
    
    @Column(name = "user_stats")
    private String userStats;
    
    @Column(name = "avg_col_len")
    private Double avgColLen;
    
    @Column(name = "char_length")
    private Integer charLength;
    
    @Column(name = "char_used")
    private String charUsed;
    
    @Column(name = "comments", length = 4000)
    private String comments;
    
    @NotBlank
    @Column(name = "oracle_connection_id", nullable = false)
    private String oracleConnectionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oracle_table_id")
    private OracleTable oracleTable;
    
    // Constructors
    public OracleColumn() {
        super();
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public OracleColumn(String owner, String tableName, String columnName, String oracleConnectionId) {
        this();
        this.owner = owner;
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
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getDataTypeMod() {
        return dataTypeMod;
    }
    
    public void setDataTypeMod(String dataTypeMod) {
        this.dataTypeMod = dataTypeMod;
    }
    
    public String getDataTypeOwner() {
        return dataTypeOwner;
    }
    
    public void setDataTypeOwner(String dataTypeOwner) {
        this.dataTypeOwner = dataTypeOwner;
    }
    
    public Long getDataLength() {
        return dataLength;
    }
    
    public void setDataLength(Long dataLength) {
        this.dataLength = dataLength;
    }
    
    public Integer getDataPrecision() {
        return dataPrecision;
    }
    
    public void setDataPrecision(Integer dataPrecision) {
        this.dataPrecision = dataPrecision;
    }
    
    public Integer getDataScale() {
        return dataScale;
    }
    
    public void setDataScale(Integer dataScale) {
        this.dataScale = dataScale;
    }
    
    public String getNullable() {
        return nullable;
    }
    
    public void setNullable(String nullable) {
        this.nullable = nullable;
    }
    
    public Integer getColumnId() {
        return columnId;
    }
    
    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }
    
    public Long getDefaultLength() {
        return defaultLength;
    }
    
    public void setDefaultLength(Long defaultLength) {
        this.defaultLength = defaultLength;
    }
    
    public String getDataDefault() {
        return dataDefault;
    }
    
    public void setDataDefault(String dataDefault) {
        this.dataDefault = dataDefault;
    }
    
    public Long getNumDistinct() {
        return numDistinct;
    }
    
    public void setNumDistinct(Long numDistinct) {
        this.numDistinct = numDistinct;
    }
    
    public String getLowValue() {
        return lowValue;
    }
    
    public void setLowValue(String lowValue) {
        this.lowValue = lowValue;
    }
    
    public String getHighValue() {
        return highValue;
    }
    
    public void setHighValue(String highValue) {
        this.highValue = highValue;
    }
    
    public Double getDensity() {
        return density;
    }
    
    public void setDensity(Double density) {
        this.density = density;
    }
    
    public Long getNumNulls() {
        return numNulls;
    }
    
    public void setNumNulls(Long numNulls) {
        this.numNulls = numNulls;
    }
    
    public Integer getNumBuckets() {
        return numBuckets;
    }
    
    public void setNumBuckets(Integer numBuckets) {
        this.numBuckets = numBuckets;
    }
    
    public String getCharacterSetName() {
        return characterSetName;
    }
    
    public void setCharacterSetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }
    
    public Integer getCharColDeclLength() {
        return charColDeclLength;
    }
    
    public void setCharColDeclLength(Integer charColDeclLength) {
        this.charColDeclLength = charColDeclLength;
    }
    
    public String getGlobalStats() {
        return globalStats;
    }
    
    public void setGlobalStats(String globalStats) {
        this.globalStats = globalStats;
    }
    
    public String getUserStats() {
        return userStats;
    }
    
    public void setUserStats(String userStats) {
        this.userStats = userStats;
    }
    
    public Double getAvgColLen() {
        return avgColLen;
    }
    
    public void setAvgColLen(Double avgColLen) {
        this.avgColLen = avgColLen;
    }
    
    public Integer getCharLength() {
        return charLength;
    }
    
    public void setCharLength(Integer charLength) {
        this.charLength = charLength;
    }
    
    public String getCharUsed() {
        return charUsed;
    }
    
    public void setCharUsed(String charUsed) {
        this.charUsed = charUsed;
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
    
    public OracleTable getOracleTable() {
        return oracleTable;
    }
    
    public void setOracleTable(OracleTable oracleTable) {
        this.oracleTable = oracleTable;
    }
}