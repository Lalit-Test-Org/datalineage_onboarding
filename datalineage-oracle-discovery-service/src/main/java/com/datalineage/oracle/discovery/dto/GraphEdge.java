package com.datalineage.oracle.discovery.dto;

import java.util.Map;

/**
 * DTO representing an edge in the graph structure for Oracle metadata visualization
 */
public class GraphEdge {
    private String id;
    private String source;
    private String target;
    private String type; // relationship, foreign_key, contains, references, derived_from
    private String label;
    private Map<String, Object> metadata;
    private String color;
    private String style;

    // Constructors
    public GraphEdge() {}

    public GraphEdge(String id, String source, String target, String type, Map<String, Object> metadata) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
        this.metadata = metadata;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}