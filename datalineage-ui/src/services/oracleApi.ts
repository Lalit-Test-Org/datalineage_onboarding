import axios from 'axios';
import { 
  OracleOnboardingRequest, 
  OracleConnection, 
  ApiResponse, 
  ConnectionTestResult,
  MetadataDiscoveryRequest,
  MetadataDiscoveryResponse
} from '../types/oracle';

// Base URL for the Oracle onboarding API
const BASE_URL = 'http://localhost:8083/api/v1/oracle';
// Base URL for the Oracle discovery API
const DISCOVERY_BASE_URL = 'http://localhost:8084/api/v1/oracle-discovery';

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create discovery service axios instance
const discoveryClient = axios.create({
  baseURL: DISCOVERY_BASE_URL,
  timeout: 120000, // Longer timeout for metadata discovery
  headers: {
    'Content-Type': 'application/json',
  },
});

// API response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Discovery API response interceptor
discoveryClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Discovery API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export class OracleApiService {
  
  /**
   * Onboard a new Oracle database connection
   */
  static async onboardDatabase(request: OracleOnboardingRequest): Promise<ApiResponse<OracleConnection>> {
    try {
      const response = await apiClient.post<ApiResponse<OracleConnection>>('/onboard', request);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to onboard Oracle database');
    }
  }

  /**
   * Get all Oracle database connections
   */
  static async getConnections(): Promise<ApiResponse<OracleConnection[]>> {
    try {
      const response = await apiClient.get<ApiResponse<OracleConnection[]>>('/connections');
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch Oracle connections');
    }
  }

  /**
   * Get a specific Oracle database connection
   */
  static async getConnection(id: string): Promise<ApiResponse<OracleConnection>> {
    try {
      const response = await apiClient.get<ApiResponse<OracleConnection>>(`/connections/${id}`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch Oracle connection');
    }
  }

  /**
   * Test an Oracle database connection
   */
  static async testConnection(id: string): Promise<ApiResponse<ConnectionTestResult>> {
    try {
      const response = await apiClient.post<ApiResponse<ConnectionTestResult>>(`/connections/${id}/test`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to test Oracle connection');
    }
  }

  /**
   * Update an Oracle database connection
   */
  static async updateConnection(id: string, request: OracleOnboardingRequest): Promise<ApiResponse<OracleConnection>> {
    try {
      const response = await apiClient.put<ApiResponse<OracleConnection>>(`/connections/${id}`, request);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to update Oracle connection');
    }
  }

  /**
   * Delete an Oracle database connection
   */
  static async deleteConnection(id: string): Promise<ApiResponse<null>> {
    try {
      const response = await apiClient.delete<ApiResponse<null>>(`/connections/${id}`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to delete Oracle connection');
    }
  }

  /**
   * Trigger metadata discovery for a connection
   */
  static async triggerMetadataDiscovery(id: string): Promise<ApiResponse<any>> {
    try {
      const response = await apiClient.post<ApiResponse<any>>(`/connections/${id}/discover`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to trigger metadata discovery');
    }
  }

  /**
   * Check service health
   */
  static async healthCheck(): Promise<ApiResponse<any>> {
    try {
      const response = await apiClient.get<ApiResponse<any>>('/health');
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Health check failed');
    }
  }

  /**
   * Discover metadata for a connection using the discovery service
   */
  static async discoverMetadata(
    connectionId: string, 
    discoveryRequest?: Partial<MetadataDiscoveryRequest>
  ): Promise<ApiResponse<MetadataDiscoveryResponse>> {
    try {
      const params = new URLSearchParams();
      
      if (discoveryRequest?.schemas) {
        params.append('schemas', discoveryRequest.schemas.join(','));
      }
      if (discoveryRequest?.tablePatterns) {
        params.append('tablePatterns', discoveryRequest.tablePatterns.join(','));
      }
      if (discoveryRequest?.includeTables !== undefined) {
        params.append('includeTables', discoveryRequest.includeTables.toString());
      }
      if (discoveryRequest?.includeColumns !== undefined) {
        params.append('includeColumns', discoveryRequest.includeColumns.toString());
      }
      if (discoveryRequest?.includeProcedures !== undefined) {
        params.append('includeProcedures', discoveryRequest.includeProcedures.toString());
      }
      if (discoveryRequest?.includeConstraints !== undefined) {
        params.append('includeConstraints', discoveryRequest.includeConstraints.toString());
      }
      if (discoveryRequest?.limit) {
        params.append('limit', discoveryRequest.limit.toString());
      }
      if (discoveryRequest?.offset) {
        params.append('offset', discoveryRequest.offset.toString());
      }

      const url = `/connections/${connectionId}/discover${params.toString() ? '?' + params.toString() : ''}`;
      const response = await discoveryClient.post<ApiResponse<MetadataDiscoveryResponse>>(url);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to discover metadata');
    }
  }

  /**
   * Check discovery service health
   */
  static async discoveryHealthCheck(): Promise<ApiResponse<any>> {
    try {
      const response = await discoveryClient.get<ApiResponse<any>>('/health');
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Discovery service health check failed');
    }
  }
}