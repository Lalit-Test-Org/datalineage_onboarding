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
      name: 'cose', // Add required layout name
      animate: false, // Disable animation to address Cytoscape lifecycle race condition (see PR description)
      fit: true,
      padding: 30
    },
    style: [
      {
        selector: 'node',
        style: {
          'background-color': '#9E9E9E',
          'label': 'data(label)',
          'text-valign': 'center',
          'text-halign': 'center',
          'font-size': '12px',
          'font-weight': 'bold',
          'color': '#333',
          'text-outline-width': 2,
          'text-outline-color': '#fff',
          'width': 50,
          'height': 50,
          'shape': 'ellipse',
          'border-width': 2,
          'border-color': '#ccc'
        }
      },
      {
        selector: 'node[type="database"]',
        style: {
          'background-color': '#4CAF50',
          'width': 80,
          'height': 80,
          'shape': 'round-rectangle'
        }
      },
      {
        selector: 'node[type="schema"]',
        style: {
          'background-color': '#2196F3',
          'width': 70,
          'height': 70,
          'shape': 'round-rectangle'
        }
      },
      {
        selector: 'node[type="table"]',
        style: {
          'background-color': '#FF9800',
          'width': 60,
          'height': 60,
          'shape': 'rectangle'
        }
      },
      {
        selector: 'node[type="column"]',
        style: {
          'background-color': '#9C27B0',
          'width': 40,
          'height': 40,
          'shape': 'ellipse'
        }
      },
      {
        selector: 'node[type="procedure"]',
        style: {
          'background-color': '#F44336',
          'width': 50,
          'height': 50,
          'shape': 'triangle'
        }
      },
      {
        selector: 'node[type="constraint"]',
        style: {
          'background-color': '#795548',
          'width': 35,
          'height': 35,
          'shape': 'diamond'
        }
      },
      {
        selector: 'node:selected',
        style: {
          'border-color': '#007bff',
          'border-width': 4
        }
      },
      {
        selector: 'edge',
        style: {
          'width': 2,
          'line-color': '#666',
          'target-arrow-color': '#666',
          'target-arrow-shape': 'triangle',
          'curve-style': 'bezier',
          'label': 'data(label)',
          'font-size': '10px',
          'text-rotation': 'autorotate',
          'text-margin-y': -10
        }
      },
      {
        selector: 'edge[type="foreign_key"]',
        style: {
          'width': 3,
          'line-color': '#e91e63',
          'target-arrow-color': '#e91e63',
          'target-arrow-shape': 'triangle-backcurve'
        }
      },
      {
        selector: 'edge[type="references"]',
        style: {
          'width': 2,
          'line-color': '#3f51b5',
          'target-arrow-color': '#3f51b5'
        }
      },
      {
        selector: 'edge:selected',
        style: {
          'line-color': '#007bff',
          'target-arrow-color': '#007bff',
          'width': 4
        }
      },
      {
        selector: '.highlighted',
        style: {
          'background-color': '#ffeb3b',
          'line-color': '#ffeb3b',
          'target-arrow-color': '#ffeb3b'
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

  // Initialize Cytoscape
  const initializeCytoscape = useCallback(() => {
    if (!containerRef.current || !data) return;

    // Cleanup existing instance first
    if (cyRef.current) {
      try {
        cyRef.current.destroy();
      } catch (error) {
        console.warn('Error destroying existing cytoscape instance:', error);
      }
      cyRef.current = null;
    }

    setIsLoading(true);

    // Transform data for Cytoscape with proper data attributes
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

    try {
      // Create Cytoscape instance with stable configuration
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

      // Simple event handlers with null checks
      const handleNodeTap = (event: any) => {
        if (!cyRef.current) return;
        const node = event.target;
        const nodeData = data.nodes.find(n => n.id === node.id());
        if (nodeData && onNodeClick) {
          onNodeClick(nodeData);
        }
      };

      const handleEdgeTap = (event: any) => {
        if (!cyRef.current) return;
        const edge = event.target;
        const edgeData = data.edges.find(e => e.id === edge.id());
        if (edgeData && onEdgeClick) {
          onEdgeClick(edgeData);
        }
      };

      const handleSelectionChange = () => {
        if (!cyRef.current) return;
        const cy = cyRef.current;
        
        try {
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
        } catch (error) {
          console.warn('Error handling selection change:', error);
        }
      };

      // Bind events
      cy.on('tap', 'node', handleNodeTap);
      cy.on('tap', 'edge', handleEdgeTap);
      cy.on('select unselect', handleSelectionChange);

      // Handle layout completion
      cy.one('layoutready', () => {
        if (cyRef.current) {
          setIsLoading(false);
        }
      });

      // Fallback for ready state
      cy.ready(() => {
        if (cyRef.current) {
          setIsLoading(false);
        }
      });

      // Run layout
      cy.layout(finalConfig.layout).run();

    } catch (error) {
      console.error('Error initializing cytoscape:', error);
      setIsLoading(false);
    }

  }, [data, finalConfig, onNodeClick, onEdgeClick, onSelectionChange]);

  // Apply search filter
  const applySearchFilter = useCallback(() => {
    if (!cyRef.current || !searchFilter) return;

    try {
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
    } catch (error) {
      console.warn('Error applying search filter:', error);
    }
  }, [searchFilter]);

  // Effects
  useEffect(() => {
    initializeCytoscape();
    
    return () => {
      if (cyRef.current) {
        try {
          // Remove all event listeners to prevent memory leaks
          cyRef.current.removeAllListeners();
          // Stop any running layouts
          cyRef.current.stop();
          // Destroy the instance
          cyRef.current.destroy();
        } catch (error) {
          console.warn('Error during cytoscape cleanup:', error);
        }
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
      try {
        cyRef.current.fit();
      } catch (error) {
        console.warn('Error fitting to content:', error);
      }
    }
  };

  const centerGraph = () => {
    if (cyRef.current) {
      try {
        cyRef.current.center();
      } catch (error) {
        console.warn('Error centering graph:', error);
      }
    }
  };

  const resetZoom = () => {
    if (cyRef.current) {
      try {
        cyRef.current.zoom(1);
        cyRef.current.center();
      } catch (error) {
        console.warn('Error resetting zoom:', error);
      }
    }
  };

  const exportImage = (format: 'png' | 'jpg' = 'png') => {
    if (cyRef.current) {
      try {
        const url = cyRef.current.png({ 
          output: 'blob-promise',
          bg: '#ffffff',
          full: true,
          scale: 2
        });
        return url;
      } catch (error) {
        console.warn('Error exporting image:', error);
        return null;
      }
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