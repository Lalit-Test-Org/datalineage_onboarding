import React, { useEffect, useRef, useState, useCallback, useMemo } from 'react';
import cytoscape, { Core } from 'cytoscape';
import { GraphData, GraphNode, GraphEdge, GraphConfig, SearchFilter } from '../../types/graph';
import './GraphVisualization.css';

interface GraphVisualizationProps {
  data: GraphData;
  config?: Partial<GraphConfig>;
  onNodeClick?: (node: GraphNode) => void;
  onEdgeClick?: (edge: GraphEdge) => void;
  onSelectionChange?: (selectedNodes: GraphNode[], selectedEdges: GraphEdge[]) => void;
  searchFilter?: SearchFilter;
  className?: string;
}

export const GraphVisualization: React.FC<GraphVisualizationProps> = ({
  data,
  config = {},
  onNodeClick,
  onEdgeClick,
  onSelectionChange,
  searchFilter,
  className = ''
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const cyRef = useRef<Core | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedElements, setSelectedElements] = useState<{
    nodes: GraphNode[];
    edges: GraphEdge[];
  }>({ nodes: [], edges: [] });

  // Default configuration
  const defaultConfig: GraphConfig = useMemo(() => ({
    layout: {
      name: 'cose',
      animate: true,
      animationDuration: 1000,
      fit: true,
      padding: 30
    },
    style: [
      {
        selector: 'node',
        style: {
          'background-color': (node: any) => getNodeColor(node.data('type')),
          'label': 'data(label)',
          'text-valign': 'center',
          'text-halign': 'center',
          'font-size': '12px',
          'font-weight': 'bold',
          'color': '#333',
          'text-outline-width': 2,
          'text-outline-color': '#fff',
          'width': (node: any) => getNodeSize(node.data('type')).width,
          'height': (node: any) => getNodeSize(node.data('type')).height,
          'shape': (node: any) => getNodeShape(node.data('type')),
          'border-width': 2,
          'border-color': '#ccc',
          'cursor': 'pointer'
        }
      },
      {
        selector: 'node:selected',
        style: {
          'border-color': '#007bff',
          'border-width': 4,
          'background-color': (node: any) => lightenColor(getNodeColor(node.data('type')), 0.2)
        }
      },
      {
        selector: 'node:hover',
        style: {
          'border-color': '#0056b3',
          'border-width': 3
        }
      },
      {
        selector: 'edge',
        style: {
          'width': (edge: any) => getEdgeWidth(edge.data('type')),
          'line-color': (edge: any) => getEdgeColor(edge.data('type')),
          'target-arrow-color': (edge: any) => getEdgeColor(edge.data('type')),
          'target-arrow-shape': (edge: any) => getEdgeArrow(edge.data('type')),
          'curve-style': 'bezier',
          'label': 'data(label)',
          'font-size': '10px',
          'text-rotation': 'autorotate',
          'text-margin-y': -10,
          'cursor': 'pointer'
        }
      },
      {
        selector: 'edge:selected',
        style: {
          'line-color': '#007bff',
          'target-arrow-color': '#007bff',
          'width': (edge: any) => getEdgeWidth(edge.data('type')) + 2
        }
      },
      {
        selector: 'edge:hover',
        style: {
          'line-color': '#0056b3',
          'target-arrow-color': '#0056b3'
        }
      },
      {
        selector: '.highlighted',
        style: {
          'background-color': '#ffeb3b',
          'line-color': '#ffeb3b',
          'target-arrow-color': '#ffeb3b',
          'transition-property': 'background-color, line-color, target-arrow-color',
          'transition-duration': '0.3s'
        }
      },
      {
        selector: '.dimmed',
        style: {
          'opacity': 0.3
        }
      }
    ],
    enableZoom: true,
    enablePan: true,
    enableSelection: true,
    enableTooltips: true,
    minZoom: 0.1,
    maxZoom: 3.0
  }), []);

  const finalConfig = useMemo(() => ({ ...defaultConfig, ...config }), [defaultConfig, config]);

  // Helper functions for styling
  const getNodeColor = (type: string): string => {
    const colorMap: Record<string, string> = {
      'database': '#4CAF50',
      'schema': '#2196F3',
      'table': '#FF9800',
      'column': '#9C27B0',
      'procedure': '#F44336',
      'constraint': '#795548',
      'csv': '#00BCD4',
      'xsd': '#CDDC39',
      'avro': '#FF5722'
    };
    return colorMap[type] || '#9E9E9E';
  };

  const getNodeSize = (type: string): { width: number; height: number } => {
    const sizeMap: Record<string, { width: number; height: number }> = {
      'database': { width: 80, height: 80 },
      'schema': { width: 70, height: 70 },
      'table': { width: 60, height: 60 },
      'column': { width: 40, height: 40 },
      'procedure': { width: 50, height: 50 },
      'constraint': { width: 35, height: 35 },
      'csv': { width: 55, height: 55 },
      'xsd': { width: 55, height: 55 },
      'avro': { width: 55, height: 55 }
    };
    return sizeMap[type] || { width: 50, height: 50 };
  };

  const getNodeShape = (type: string): string => {
    const shapeMap: Record<string, string> = {
      'database': 'round-rectangle',
      'schema': 'round-rectangle',
      'table': 'rectangle',
      'column': 'ellipse',
      'procedure': 'triangle',
      'constraint': 'diamond',
      'csv': 'hexagon',
      'xsd': 'octagon',
      'avro': 'star'
    };
    return shapeMap[type] || 'ellipse';
  };

  const getEdgeColor = (type: string): string => {
    const colorMap: Record<string, string> = {
      'contains': '#666',
      'relationship': '#333',
      'foreign_key': '#e91e63',
      'references': '#3f51b5',
      'derived_from': '#9c27b0'
    };
    return colorMap[type] || '#999';
  };

  const getEdgeWidth = (type: string): number => {
    const widthMap: Record<string, number> = {
      'contains': 2,
      'relationship': 1,
      'foreign_key': 3,
      'references': 2,
      'derived_from': 2
    };
    return widthMap[type] || 1;
  };

  const getEdgeArrow = (type: string): string => {
    const arrowMap: Record<string, string> = {
      'contains': 'triangle',
      'relationship': 'triangle',
      'foreign_key': 'triangle-backcurve',
      'references': 'triangle',
      'derived_from': 'triangle'
    };
    return arrowMap[type] || 'triangle';
  };

  const lightenColor = (color: string, amount: number): string => {
    // Simple color lightening function
    const hex = color.replace('#', '');
    const num = parseInt(hex, 16);
    const amt = Math.round(2.55 * amount * 100);
    const R = ((num >> 16)) + amt;
    const G = ((num >> 8) & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;
    return `#${(0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
      (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
      (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)}`;
  };

  // Initialize Cytoscape
  const initializeCytoscape = useCallback(() => {
    if (!containerRef.current || !data) return;

    setIsLoading(true);

    // Transform data for Cytoscape
    const elements = [
      ...data.nodes.map(node => ({
        data: {
          id: node.id,
          label: node.label,
          type: node.type,
          metadata: node.metadata
        }
      })),
      ...data.edges.map(edge => ({
        data: {
          id: edge.id,
          source: edge.source,
          target: edge.target,
          label: edge.label,
          type: edge.type,
          metadata: edge.metadata
        }
      }))
    ];

    // Create Cytoscape instance
    cyRef.current = cytoscape({
      container: containerRef.current,
      elements,
      style: finalConfig.style,
      layout: finalConfig.layout,
      minZoom: finalConfig.minZoom,
      maxZoom: finalConfig.maxZoom,
      zoomingEnabled: finalConfig.enableZoom,
      panningEnabled: finalConfig.enablePan,
      selectionType: finalConfig.enableSelection ? 'single' : undefined,
      boxSelectionEnabled: false,
      autoungrabify: false,
      autounselectify: false
    });

    const cy = cyRef.current;

    // Event handlers
    cy.on('tap', 'node', (event) => {
      const node = event.target;
      const nodeData = data.nodes.find(n => n.id === node.id());
      if (nodeData && onNodeClick) {
        onNodeClick(nodeData);
      }
    });

    cy.on('tap', 'edge', (event) => {
      const edge = event.target;
      const edgeData = data.edges.find(e => e.id === edge.id());
      if (edgeData && onEdgeClick) {
        onEdgeClick(edgeData);
      }
    });

    cy.on('select unselect', () => {
      const selectedNodes = cy.$('node:selected').map(node => 
        data.nodes.find(n => n.id === node.id())
      ).filter(Boolean) as GraphNode[];
      
      const selectedEdges = cy.$('edge:selected').map(edge => 
        data.edges.find(e => e.id === edge.id())
      ).filter(Boolean) as GraphEdge[];

      setSelectedElements({ nodes: selectedNodes, edges: selectedEdges });
      
      if (onSelectionChange) {
        onSelectionChange(selectedNodes, selectedEdges);
      }
    });

    // Layout complete
    cy.on('layoutready', () => {
      setIsLoading(false);
    });

    cy.ready(() => {
      setIsLoading(false);
    });

  }, [data, finalConfig, onNodeClick, onEdgeClick, onSelectionChange]);

  // Apply search filter
  const applySearchFilter = useCallback(() => {
    if (!cyRef.current || !searchFilter) return;

    const cy = cyRef.current;
    
    // Reset all elements
    cy.elements().removeClass('highlighted dimmed');
    
    if (!searchFilter.query && 
        searchFilter.nodeTypes.length === 0 && 
        searchFilter.edgeTypes.length === 0) {
      return;
    }

    const matchingElements = cy.elements().filter((element) => {
      const data = element.data();
      
      // Check query match
      let queryMatch = true;
      if (searchFilter.query) {
        const query = searchFilter.query.toLowerCase();
        queryMatch = data.label?.toLowerCase().includes(query) ||
                    JSON.stringify(data.metadata).toLowerCase().includes(query);
      }
      
      // Check type match
      let typeMatch = true;
      if (element.isNode() && searchFilter.nodeTypes.length > 0) {
        typeMatch = searchFilter.nodeTypes.includes(data.type);
      } else if (element.isEdge() && searchFilter.edgeTypes.length > 0) {
        typeMatch = searchFilter.edgeTypes.includes(data.type);
      }
      
      return queryMatch && typeMatch;
    });

    if (matchingElements.length > 0) {
      // Highlight matching elements
      matchingElements.addClass('highlighted');
      // Dim non-matching elements
      cy.elements().difference(matchingElements).addClass('dimmed');
    }
  }, [searchFilter]);

  // Effects
  useEffect(() => {
    initializeCytoscape();
    
    return () => {
      if (cyRef.current) {
        cyRef.current.destroy();
        cyRef.current = null;
      }
    };
  }, [initializeCytoscape]);

  useEffect(() => {
    applySearchFilter();
  }, [applySearchFilter]);

  // Public methods
  const fitToContent = () => {
    if (cyRef.current) {
      cyRef.current.fit();
    }
  };

  const centerGraph = () => {
    if (cyRef.current) {
      cyRef.current.center();
    }
  };

  const resetZoom = () => {
    if (cyRef.current) {
      cyRef.current.zoom(1);
      cyRef.current.center();
    }
  };

  const exportImage = (format: 'png' | 'jpg' = 'png') => {
    if (cyRef.current) {
      const url = cyRef.current.png({ 
        output: 'blob-promise',
        bg: '#ffffff',
        full: true,
        scale: 2
      });
      return url;
    }
    return null;
  };

  return (
    <div className={`graph-visualization ${className}`}>
      {isLoading && (
        <div className="graph-loading">
          <div className="loading-spinner"></div>
          <div>Loading graph...</div>
        </div>
      )}
      
      <div className="graph-controls">
        <button 
          onClick={fitToContent}
          className="graph-control-btn"
          title="Fit to content"
        >
          📐
        </button>
        <button 
          onClick={centerGraph}
          className="graph-control-btn"
          title="Center graph"
        >
          🎯
        </button>
        <button 
          onClick={resetZoom}
          className="graph-control-btn"
          title="Reset zoom"
        >
          🔍
        </button>
        <button 
          onClick={() => exportImage('png')}
          className="graph-control-btn"
          title="Export as image"
        >
          📷
        </button>
      </div>

      <div 
        ref={containerRef} 
        className="graph-container"
        style={{ width: '100%', height: '100%' }}
      />
      
      {selectedElements.nodes.length > 0 || selectedElements.edges.length > 0 ? (
        <div className="graph-selection-info">
          {selectedElements.nodes.length > 0 && (
            <div>Selected Nodes: {selectedElements.nodes.length}</div>
          )}
          {selectedElements.edges.length > 0 && (
            <div>Selected Edges: {selectedElements.edges.length}</div>
          )}
        </div>
      ) : null}
    </div>
  );
};