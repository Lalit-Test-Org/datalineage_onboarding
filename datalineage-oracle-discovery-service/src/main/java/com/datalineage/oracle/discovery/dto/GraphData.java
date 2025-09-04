package com.datalineage.oracle.discovery.dto;

import java.util.List;

/**
 * DTO representing graph data structure for Oracle metadata visualization
 */
public class GraphData {
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;
    private GraphStatistics statistics;

    // Constructors
    public GraphData() {}

    public GraphData(List<GraphNode> nodes, List<GraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    // Getters and Setters
    public List<GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<GraphEdge> edges) {
        this.edges = edges;
    }

    public GraphStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(GraphStatistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Inner class for graph statistics
     */
    public static class GraphStatistics {
        private int totalNodes;
        private int totalEdges;
        private java.util.Map<String, Integer> nodeTypeBreakdown;
        private java.util.Map<String, Integer> edgeTypeBreakdown;

        // Constructors
        public GraphStatistics() {}

        public GraphStatistics(int totalNodes, int totalEdges, 
                              java.util.Map<String, Integer> nodeTypeBreakdown, 
                              java.util.Map<String, Integer> edgeTypeBreakdown) {
            this.totalNodes = totalNodes;
            this.totalEdges = totalEdges;
            this.nodeTypeBreakdown = nodeTypeBreakdown;
            this.edgeTypeBreakdown = edgeTypeBreakdown;
        }

        // Getters and Setters
        public int getTotalNodes() {
            return totalNodes;
        }

        public void setTotalNodes(int totalNodes) {
            this.totalNodes = totalNodes;
        }

        public int getTotalEdges() {
            return totalEdges;
        }

        public void setTotalEdges(int totalEdges) {
            this.totalEdges = totalEdges;
        }

        public java.util.Map<String, Integer> getNodeTypeBreakdown() {
            return nodeTypeBreakdown;
        }

        public void setNodeTypeBreakdown(java.util.Map<String, Integer> nodeTypeBreakdown) {
            this.nodeTypeBreakdown = nodeTypeBreakdown;
        }

        public java.util.Map<String, Integer> getEdgeTypeBreakdown() {
            return edgeTypeBreakdown;
        }

        public void setEdgeTypeBreakdown(java.util.Map<String, Integer> edgeTypeBreakdown) {
            this.edgeTypeBreakdown = edgeTypeBreakdown;
        }
    }
}