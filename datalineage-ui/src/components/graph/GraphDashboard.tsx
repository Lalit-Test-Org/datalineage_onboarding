import React, { useState, useEffect, useCallback } from 'react';
import { GraphVisualization } from './GraphVisualization';
import { GraphSearchAndFilter } from './GraphSearchAndFilter';
import { MetadataDetailPanel } from './MetadataDetailPanel';
import { GraphApiService, OracleConnectionConfig } from '../../services/graphApiService';
import { GraphData, GraphNode, GraphEdge, SearchFilter, GraphStats } from '../../types/graph';
import { OracleConnection, AuthenticationType } from '../../types/oracle';
import './GraphDashboard.css';

interface GraphDashboardProps {
  className?: string;
}

export const GraphDashboard: React.FC<GraphDashboardProps> = ({ className = '' }) => {
  // State management
  const [graphData, setGraphData] = useState<GraphData>({ nodes: [], edges: [] });
  const [searchFilter, setSearchFilter] = useState<SearchFilter>({
    query: '',
    nodeTypes: [],
    edgeTypes: [],
    properties: []
  });
  const [selectedNode, setSelectedNode] = useState<GraphNode | undefined>();
  const [selectedEdge, setSelectedEdge] = useState<GraphEdge | undefined>();
  const [isDetailPanelOpen, setIsDetailPanelOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [availableConnections, setAvailableConnections] = useState<OracleConnection[]>([]);
  const [selectedConnectionId, setSelectedConnectionId] = useState<string>('');
  const [graphStats, setGraphStats] = useState<GraphStats>({
    totalNodes: 0,
    totalEdges: 0,
    nodeTypeBreakdown: {},
    edgeTypeBreakdown: {}
  });

  // Load available connections on component mount
  useEffect(() => {
    loadAvailableConnections();
  }, []);

  const loadAvailableConnections = async () => {
    try {
      // This would typically fetch from the connection management API
      // For now, we'll use a placeholder
      const connections: OracleConnection[] = [
        {
          id: 'sample-connection-1',
          connectionName: 'Sample Oracle DB',
          description: 'Sample connection for demo purposes',
          host: 'localhost',
          port: 1521,
          serviceName: 'XE',
          username: 'sample_user',
          authenticationType: AuthenticationType.DIRECT,
          status: 'ACTIVE',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          lastTestedAt: new Date().toISOString()
        }
      ];
      setAvailableConnections(connections);
      if (connections.length > 0) {
        setSelectedConnectionId(connections[0].id);
      }
    } catch (error) {
      console.error('Failed to load connections:', error);
      setError('Failed to load available connections');
    }
  };

  const calculateGraphStats = useCallback((data: GraphData): GraphStats => {
    const nodeTypeBreakdown: Record<string, number> = {};
    const edgeTypeBreakdown: Record<string, number> = {};

    data.nodes.forEach(node => {
      nodeTypeBreakdown[node.type] = (nodeTypeBreakdown[node.type] || 0) + 1;
    });

    data.edges.forEach(edge => {
      edgeTypeBreakdown[edge.type] = (edgeTypeBreakdown[edge.type] || 0) + 1;
    });

    return {
      totalNodes: data.nodes.length,
      totalEdges: data.edges.length,
      nodeTypeBreakdown,
      edgeTypeBreakdown
    };
  }, []);

  const loadGraphData = useCallback(async (connectionId: string) => {
    if (!connectionId) return;

    setIsLoading(true);
    setError(null);

    try {
      const connection = availableConnections.find(c => c.id === connectionId);
      if (!connection) {
        throw new Error('Connection not found');
      }

      // Create connection config for API call
      const connectionConfig: OracleConnectionConfig = {
        connectionId: connection.id,
        host: connection.host,
        port: connection.port,
        serviceName: connection.serviceName,
        username: connection.username || 'unknown',
        password: 'password', // This should come from secure storage
        authenticationType: 'BASIC'
      };

      const data = await GraphApiService.fetchOracleGraphData(
        connectionId,
        connectionConfig,
        {
          includeTables: true,
          includeColumns: true,
          includeProcedures: true,
          includeConstraints: true,
          limit: 1000
        }
      );

      setGraphData(data);
      setGraphStats(calculateGraphStats(data));
    } catch (error) {
      console.error('Failed to load graph data:', error);
      setError(error instanceof Error ? error.message : 'Failed to load graph data');
      
      // For demo purposes, let's create some sample data if the API fails
      const sampleData: GraphData = {
        nodes: [
          {
            id: 'schema-1',
            label: 'HR Schema',
            type: 'schema',
            metadata: { connectionId: 'sample', type: 'Oracle Schema' }
          },
          {
            id: 'table-1',
            label: 'EMPLOYEES',
            type: 'table',
            metadata: { owner: 'HR', tableName: 'EMPLOYEES', type: 'Oracle Table' }
          },
          {
            id: 'column-1',
            label: 'EMPLOYEE_ID',
            type: 'column',
            metadata: { owner: 'HR', tableName: 'EMPLOYEES', columnName: 'EMPLOYEE_ID', dataType: 'NUMBER' }
          },
          {
            id: 'column-2',
            label: 'FIRST_NAME',
            type: 'column',
            metadata: { owner: 'HR', tableName: 'EMPLOYEES', columnName: 'FIRST_NAME', dataType: 'VARCHAR2' }
          }
        ],
        edges: [
          {
            id: 'edge-1',
            source: 'schema-1',
            target: 'table-1',
            type: 'contains',
            metadata: { relationship: 'schema contains table' }
          },
          {
            id: 'edge-2',
            source: 'table-1',
            target: 'column-1',
            type: 'contains',
            metadata: { relationship: 'table contains column' }
          },
          {
            id: 'edge-3',
            source: 'table-1',
            target: 'column-2',
            type: 'contains',
            metadata: { relationship: 'table contains column' }
          }
        ]
      };
      setGraphData(sampleData);
      setGraphStats(calculateGraphStats(sampleData));
    } finally {
      setIsLoading(false);
    }
  }, [availableConnections, calculateGraphStats]);

  // Load graph data when connection changes
  useEffect(() => {
    if (selectedConnectionId) {
      loadGraphData(selectedConnectionId);
    }
  }, [selectedConnectionId, loadGraphData]);

  const handleNodeClick = useCallback((node: GraphNode) => {
    setSelectedNode(node);
    setSelectedEdge(undefined);
    setIsDetailPanelOpen(true);
  }, []);

  const handleEdgeClick = useCallback((edge: GraphEdge) => {
    setSelectedEdge(edge);
    setSelectedNode(undefined);
    setIsDetailPanelOpen(true);
  }, []);

  const handleSelectionChange = useCallback((selectedNodes: GraphNode[], selectedEdges: GraphEdge[]) => {
    // Handle multiple selections if needed
    if (selectedNodes.length > 0) {
      setSelectedNode(selectedNodes[0]);
      setSelectedEdge(undefined);
      setIsDetailPanelOpen(true);
    } else if (selectedEdges.length > 0) {
      setSelectedEdge(selectedEdges[0]);
      setSelectedNode(undefined);
      setIsDetailPanelOpen(true);
    }
  }, []);

  const handleCloseDetailPanel = () => {
    setIsDetailPanelOpen(false);
    setSelectedNode(undefined);
    setSelectedEdge(undefined);
  };

  const getAvailableNodeTypes = (): string[] => {
    return Array.from(new Set(graphData.nodes.map(node => node.type)));
  };

  const getAvailableEdgeTypes = (): string[] => {
    return Array.from(new Set(graphData.edges.map(edge => edge.type)));
  };

  const handleRefreshData = () => {
    if (selectedConnectionId) {
      loadGraphData(selectedConnectionId);
    }
  };

  return (
    <div className={`graph-dashboard ${className}`}>
      {/* Header */}
      <div className="dashboard-header">
        <div className="header-content">
          <h2>Interactive Graph Visualization</h2>
          <p>Explore metadata relationships through an interactive graph interface</p>
        </div>
        
        <div className="header-controls">
          <div className="connection-selector">
            <label htmlFor="connection-select">Connection:</label>
            <select 
              id="connection-select"
              value={selectedConnectionId}
              onChange={(e) => setSelectedConnectionId(e.target.value)}
              disabled={isLoading}
            >
              <option value="">Select a connection...</option>
              {availableConnections.map(connection => (
                <option key={connection.id} value={connection.id}>
                  {connection.connectionName} ({connection.host}:{connection.port})
                </option>
              ))}
            </select>
          </div>
          
          <button 
            onClick={handleRefreshData}
            disabled={isLoading || !selectedConnectionId}
            className="refresh-btn"
            title="Refresh graph data"
          >
            🔄 Refresh
          </button>
        </div>
      </div>

      {/* Stats Bar */}
      <div className="stats-bar">
        <div className="stat-item">
          <span className="stat-icon">🔸</span>
          <span className="stat-label">Nodes:</span>
          <span className="stat-value">{graphStats.totalNodes}</span>
        </div>
        <div className="stat-item">
          <span className="stat-icon">🔗</span>
          <span className="stat-label">Edges:</span>
          <span className="stat-value">{graphStats.totalEdges}</span>
        </div>
        {Object.entries(graphStats.nodeTypeBreakdown).map(([type, count]) => (
          <div key={type} className="stat-item">
            <span className="stat-label">{type}:</span>
            <span className="stat-value">{count}</span>
          </div>
        ))}
      </div>

      {/* Search and Filter */}
      <div className="search-filter-container">
        <GraphSearchAndFilter
          onFilterChange={setSearchFilter}
          availableNodeTypes={getAvailableNodeTypes()}
          availableEdgeTypes={getAvailableEdgeTypes()}
        />
      </div>

      {/* Error Display */}
      {error && (
        <div className="error-banner">
          <span className="error-icon">⚠️</span>
          <span className="error-message">{error}</span>
          <button onClick={() => setError(null)} className="error-close">✕</button>
        </div>
      )}

      {/* Graph Visualization */}
      <div className="graph-container">
        {isLoading ? (
          <div className="loading-container">
            <div className="loading-spinner"></div>
            <div className="loading-text">Loading graph data...</div>
          </div>
        ) : graphData.nodes.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">📊</div>
            <h3>No Data Available</h3>
            <p>Select a connection and ensure it has discoverable metadata.</p>
            {selectedConnectionId && (
              <button onClick={handleRefreshData} className="retry-btn">
                Try Again
              </button>
            )}
          </div>
        ) : (
          <GraphVisualization
            data={graphData}
            searchFilter={searchFilter}
            onNodeClick={handleNodeClick}
            onEdgeClick={handleEdgeClick}
            onSelectionChange={handleSelectionChange}
            className="main-graph"
          />
        )}
      </div>

      {/* Detail Panel */}
      <MetadataDetailPanel
        selectedNode={selectedNode}
        selectedEdge={selectedEdge}
        isVisible={isDetailPanelOpen}
        onClose={handleCloseDetailPanel}
      />
    </div>
  );
};