import axios from 'axios';
import { GraphData, GraphNode, GraphEdge } from '../types/graph';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

export interface MetadataDiscoveryResponse {
  connectionId: string;
  tables: any[];
  columns: any[];
  procedures: any[];
  constraints: any[];
  statistics: {
    totalTables: number;
    totalColumns: number;
    totalProcedures: number;
    totalConstraints: number;
    discoveryTimeMs: number;
  };
}

export interface OracleConnectionConfig {
  connectionId: string;
  host: string;
  port: number;
  serviceName: string;
  username: string;
  password: string;
  authenticationType: 'DIRECT' | 'KERBEROS';
}

/**
 * Service for fetching and transforming graph data from backend APIs
 */
export class GraphApiService {
  private static readonly ORACLE_DISCOVERY_BASE = `${API_BASE_URL}/api/v1/oracle-discovery`;

  /**
   * Fetch Oracle metadata and transform it to graph data
   */
  static async fetchOracleGraphData(
    connectionId: string,
    connectionConfig: OracleConnectionConfig,
    options?: {
      schemas?: string[];
      tablePatterns?: string[];
      tableTypes?: string[];
      includeTables?: boolean;
      includeColumns?: boolean;
      includeProcedures?: boolean;
      includeConstraints?: boolean;
      limit?: number;
      offset?: number;
    }
  ): Promise<GraphData> {
    try {
      const params = new URLSearchParams();
      
      if (options?.schemas) {
        options.schemas.forEach(schema => params.append('schemas', schema));
      }
      if (options?.tablePatterns) {
        options.tablePatterns.forEach(pattern => params.append('tablePatterns', pattern));
      }
      if (options?.tableTypes) {
        options.tableTypes.forEach(type => params.append('tableTypes', type));
      }
      
      params.append('includeTables', String(options?.includeTables ?? true));
      params.append('includeColumns', String(options?.includeColumns ?? true));
      params.append('includeProcedures', String(options?.includeProcedures ?? true));
      params.append('includeConstraints', String(options?.includeConstraints ?? true));
      params.append('limit', String(options?.limit ?? 1000));
      params.append('offset', String(options?.offset ?? 0));

      const url = `${this.ORACLE_DISCOVERY_BASE}/graph/schema/${connectionId}?${params.toString()}`;
      
      const response = await axios.post<{
        success: boolean;
        message: string;
        data: GraphData;
      }>(url, connectionConfig);
      
      if (response.data.success) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch Oracle graph data');
      }
    } catch (error) {
      console.error('Error fetching Oracle graph data:', error);
      throw error;
    }
  }

  // Legacy method - now deprecated as graph transformation is handled by backend
  // Kept for backward compatibility
  private static transformOracleMetadataToGraph(metadata: MetadataDiscoveryResponse): GraphData {
    console.warn('transformOracleMetadataToGraph is deprecated. Use the new graph API endpoints.');
    
    const nodes: GraphNode[] = [];
    const edges: GraphEdge[] = [];

    // Create database schema node
    const schemaNode: GraphNode = {
      id: `schema-${metadata.connectionId}`,
      label: `Schema (${metadata.connectionId})`,
      type: 'schema',
      metadata: {
        connectionId: metadata.connectionId,
        statistics: metadata.statistics
      }
    };
    nodes.push(schemaNode);

    // Create table nodes
    metadata.tables?.forEach(table => {
      const tableNode: GraphNode = {
        id: `table-${table.id}`,
        label: `${table.owner}.${table.tableName}`,
        type: 'table',
        metadata: {
          ...table,
          fullName: `${table.owner}.${table.tableName}`,
          type: 'Oracle Table'
        }
      };
      nodes.push(tableNode);

      // Connect table to schema
      edges.push({
        id: `schema-table-${table.id}`,
        source: schemaNode.id,
        target: tableNode.id,
        type: 'contains',
        metadata: {
          relationship: 'schema contains table'
        }
      });
    });

    // Create column nodes and connect to tables
    metadata.columns?.forEach(column => {
      const columnNode: GraphNode = {
        id: `column-${column.id}`,
        label: column.columnName,
        type: 'column',
        metadata: {
          ...column,
          fullName: `${column.owner}.${column.tableName}.${column.columnName}`,
          type: 'Oracle Column'
        }
      };
      nodes.push(columnNode);

      // Find parent table and connect
      const parentTableId = `table-${metadata.tables?.find(t => 
        t.owner === column.owner && t.tableName === column.tableName
      )?.id}`;
      
      if (parentTableId !== 'table-undefined') {
        edges.push({
          id: `table-column-${column.id}`,
          source: parentTableId,
          target: columnNode.id,
          type: 'contains',
          metadata: {
            relationship: 'table contains column'
          }
        });
      }
    });

    // Create procedure nodes
    metadata.procedures?.forEach(procedure => {
      const procedureNode: GraphNode = {
        id: `procedure-${procedure.id}`,
        label: `${procedure.owner}.${procedure.procedureName}`,
        type: 'procedure',
        metadata: {
          ...procedure,
          fullName: `${procedure.owner}.${procedure.procedureName}`,
          type: 'Oracle Procedure'
        }
      };
      nodes.push(procedureNode);

      // Connect procedure to schema
      edges.push({
        id: `schema-procedure-${procedure.id}`,
        source: schemaNode.id,
        target: procedureNode.id,
        type: 'contains',
        metadata: {
          relationship: 'schema contains procedure'
        }
      });
    });

    // Create constraint nodes and relationships
    metadata.constraints?.forEach(constraint => {
      const constraintNode: GraphNode = {
        id: `constraint-${constraint.id}`,
        label: constraint.constraintName,
        type: 'constraint',
        metadata: {
          ...constraint,
          fullName: `${constraint.owner}.${constraint.constraintName}`,
          type: 'Oracle Constraint'
        }
      };
      nodes.push(constraintNode);

      // Find related table and connect
      const relatedTableId = `table-${metadata.tables?.find(t => 
        t.owner === constraint.owner && t.tableName === constraint.tableName
      )?.id}`;
      
      if (relatedTableId !== 'table-undefined') {
        edges.push({
          id: `table-constraint-${constraint.id}`,
          source: relatedTableId,
          target: constraintNode.id,
          type: 'relationship',
          metadata: {
            relationship: 'table has constraint'
          }
        });
      }

      // If it's a foreign key constraint, create relationships to referenced table
      if (constraint.constraintType === 'R' && constraint.rOwner && constraint.rConstraintName) {
        const referencedTableId = `table-${metadata.tables?.find(t => 
          t.owner === constraint.rOwner
        )?.id}`;
        
        if (referencedTableId !== 'table-undefined') {
          edges.push({
            id: `fk-${constraint.id}`,
            source: constraintNode.id,
            target: referencedTableId,
            type: 'foreign_key',
            metadata: {
              relationship: 'foreign key references',
              referencedConstraint: constraint.rConstraintName
            }
          });
        }
      }
    });

    return { nodes, edges };
  }

  /**
   * Test connection to Oracle database
   */
  static async testOracleConnection(config: OracleConnectionConfig): Promise<boolean> {
    try {
      const response = await axios.post<{
        success: boolean;
        data: { connectionValid: boolean };
      }>(`${this.ORACLE_DISCOVERY_BASE}/test-connection`, config);
      return response.data.success && response.data.data.connectionValid;
    } catch (error) {
      console.error('Error testing Oracle connection:', error);
      return false;
    }
  }

  /**
   * Get health status of the oracle discovery service
   */
  static async getOracleDiscoveryHealth(): Promise<boolean> {
    try {
      const response = await axios.get<{
        success: boolean;
      }>(`${this.ORACLE_DISCOVERY_BASE}/health`);
      return response.data.success;
    } catch (error) {
      console.error('Error checking Oracle discovery service health:', error);
      return false;
    }
  }

  /**
   * Fetch graph data for a specific table
   */
  static async fetchTableGraphData(
    connectionId: string,
    tableName: string,
    connectionConfig: OracleConnectionConfig,
    options?: {
      owner?: string;
      includeColumns?: boolean;
      includeConstraints?: boolean;
    }
  ): Promise<GraphData> {
    try {
      const params = new URLSearchParams();
      
      if (options?.owner) {
        params.append('owner', options.owner);
      }
      params.append('includeColumns', String(options?.includeColumns ?? true));
      params.append('includeConstraints', String(options?.includeConstraints ?? true));

      const url = `${this.ORACLE_DISCOVERY_BASE}/graph/table/${connectionId}/${tableName}?${params.toString()}`;
      
      const response = await axios.post<{
        success: boolean;
        message: string;
        data: GraphData;
      }>(url, connectionConfig);
      
      if (response.data.success) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch table graph data');
      }
    } catch (error) {
      console.error('Error fetching table graph data:', error);
      throw error;
    }
  }

  /**
   * Discover and transform metadata to graph in a single call
   */
  static async discoverGraphData(
    connectionId: string,
    connectionConfig: OracleConnectionConfig,
    discoveryRequest: {
      schemas?: string[];
      tablePatterns?: string[];
      tableTypes?: string[];
      includeTables?: boolean;
      includeColumns?: boolean;
      includeProcedures?: boolean;
      includeConstraints?: boolean;
      limit?: number;
      offset?: number;
    }
  ): Promise<GraphData> {
    try {
      const requestBody = {
        connectionConfig,
        discoveryRequest: {
          connectionId,
          ...discoveryRequest
        }
      };

      const response = await axios.post<{
        success: boolean;
        message: string;
        data: GraphData;
      }>(`${this.ORACLE_DISCOVERY_BASE}/graph/connections/${connectionId}/discover`, requestBody);
      
      if (response.data.success) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to discover graph data');
      }
    } catch (error) {
      console.error('Error discovering graph data:', error);
      throw error;
    }
  }

  /**
   * Get graph metadata/statistics without full graph data
   */
  static async getGraphMetadata(
    connectionId: string,
    connectionConfig: OracleConnectionConfig,
    options?: {
      schemas?: string[];
      tablePatterns?: string[];
      tableTypes?: string[];
    }
  ): Promise<{
    totalNodes: number;
    totalEdges: number;
    nodeTypeBreakdown: Record<string, number>;
    edgeTypeBreakdown: Record<string, number>;
  }> {
    try {
      const params = new URLSearchParams();
      
      if (options?.schemas) {
        options.schemas.forEach(schema => params.append('schemas', schema));
      }
      if (options?.tablePatterns) {
        options.tablePatterns.forEach(pattern => params.append('tablePatterns', pattern));
      }
      if (options?.tableTypes) {
        options.tableTypes.forEach(type => params.append('tableTypes', type));
      }

      const url = `${this.ORACLE_DISCOVERY_BASE}/graph/metadata/${connectionId}?${params.toString()}`;
      
      const response = await axios.post<{
        success: boolean;
        message: string;
        data: {
          totalNodes: number;
          totalEdges: number;
          nodeTypeBreakdown: Record<string, number>;
          edgeTypeBreakdown: Record<string, number>;
        };
      }>(url, connectionConfig);
      
      if (response.data.success) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch graph metadata');
      }
    } catch (error) {
      console.error('Error fetching graph metadata:', error);
      throw error;
    }
  }
}