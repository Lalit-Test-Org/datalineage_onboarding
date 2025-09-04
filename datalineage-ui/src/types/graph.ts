/**
 * Types for graph visualization components
 */

export interface GraphNode {
  id: string;
  label: string;
  type: 'table' | 'column' | 'procedure' | 'constraint' | 'schema' | 'database' | 'csv' | 'xsd' | 'avro';
  metadata: Record<string, any>;
  position?: { x: number; y: number };
  size?: { width: number; height: number };
  color?: string;
  shape?: string;
}

export interface GraphEdge {
  id: string;
  source: string;
  target: string;
  type: 'relationship' | 'foreign_key' | 'contains' | 'references' | 'derived_from';
  label?: string;
  metadata: Record<string, any>;
  color?: string;
  style?: string;
}

export interface GraphData {
  nodes: GraphNode[];
  edges: GraphEdge[];
}

export interface GraphLayout {
  name: 'grid' | 'circle' | 'breadthfirst' | 'cose' | 'cola' | 'dagre' | 'spread';
  animate?: boolean;
  animationDuration?: number;
  fit?: boolean;
  padding?: number;
}

export interface GraphConfig {
  layout: GraphLayout;
  style: any[];
  enableZoom?: boolean;
  enablePan?: boolean;
  enableSelection?: boolean;
  enableTooltips?: boolean;
  minZoom?: number;
  maxZoom?: number;
}

export interface MetadataType {
  id: string;
  name: string;
  description: string;
  color: string;
  icon?: string;
  enabled: boolean;
}

export interface SearchFilter {
  query: string;
  nodeTypes: string[];
  edgeTypes: string[];
  properties: string[];
}

export interface GraphStats {
  totalNodes: number;
  totalEdges: number;
  nodeTypeBreakdown: Record<string, number>;
  edgeTypeBreakdown: Record<string, number>;
}