package com.datalineage.kafka.integration.entity;

import com.datalineage.common.model.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entity representing a field in an Avro schema
 */
@Entity
@Table(name = "avro_fields")
public class AvroFieldEntity extends BaseEntity {
    
    @Column(name = "field_name", nullable = false)
    private String fieldName;
    
    @Column(name = "field_type", nullable = false)
    private String fieldType;
    
    @Column(name = "field_position")
    private Integer fieldPosition;
    
    @Column(name = "is_nullable")
    private Boolean isNullable = false;
    
    @Column(name = "default_value")
    private String defaultValue;
    
    @Column(name = "field_doc", columnDefinition = "TEXT")
    private String fieldDoc;
    
    @Column(name = "logical_type")
    private String logicalType;
    
    @Column(name = "precision_value")
    private Integer precision;
    
    @Column(name = "scale_value")
    private Integer scale;
    
    @Column(name = "is_array")
    private Boolean isArray = false;
    
    @Column(name = "is_map")
    private Boolean isMap = false;
    
    @Column(name = "is_union")
    private Boolean isUnion = false;
    
    @Column(name = "array_item_type")
    private String arrayItemType;
    
    @Column(name = "map_value_type")
    private String mapValueType;
    
    @Column(name = "union_types")
    private String unionTypes; // JSON array as string
    
    // Relationship with schema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avro_schema_id", nullable = false)
    private AvroSchemaEntity schema;
    
    // Default constructor
    public AvroFieldEntity() {
        super();
        if (this.getId() == null) {
            this.setId(UUID.randomUUID().toString());
        }
    }
    
    // Getters and Setters
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
    
    public Integer getFieldPosition() {
        return fieldPosition;
    }
    
    public void setFieldPosition(Integer fieldPosition) {
        this.fieldPosition = fieldPosition;
    }
    
    public Boolean getIsNullable() {
        return isNullable;
    }
    
    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getFieldDoc() {
        return fieldDoc;
    }
    
    public void setFieldDoc(String fieldDoc) {
        this.fieldDoc = fieldDoc;
    }
    
    public String getLogicalType() {
        return logicalType;
    }
    
    public void setLogicalType(String logicalType) {
        this.logicalType = logicalType;
    }
    
    public Integer getPrecision() {
        return precision;
    }
    
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }
    
    public Integer getScale() {
        return scale;
    }
    
    public void setScale(Integer scale) {
        this.scale = scale;
    }
    
    public Boolean getIsArray() {
        return isArray;
    }
    
    public void setIsArray(Boolean isArray) {
        this.isArray = isArray;
    }
    
    public Boolean getIsMap() {
        return isMap;
    }
    
    public void setIsMap(Boolean isMap) {
        this.isMap = isMap;
    }
    
    public Boolean getIsUnion() {
        return isUnion;
    }
    
    public void setIsUnion(Boolean isUnion) {
        this.isUnion = isUnion;
    }
    
    public String getArrayItemType() {
        return arrayItemType;
    }
    
    public void setArrayItemType(String arrayItemType) {
        this.arrayItemType = arrayItemType;
    }
    
    public String getMapValueType() {
        return mapValueType;
    }
    
    public void setMapValueType(String mapValueType) {
        this.mapValueType = mapValueType;
    }
    
    public String getUnionTypes() {
        return unionTypes;
    }
    
    public void setUnionTypes(String unionTypes) {
        this.unionTypes = unionTypes;
    }
    
    public AvroSchemaEntity getSchema() {
        return schema;
    }
    
    public void setSchema(AvroSchemaEntity schema) {
        this.schema = schema;
    }
}