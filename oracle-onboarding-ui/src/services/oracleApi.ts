import axios from 'axios';
import { 
  OracleOnboardingRequest, 
  OracleConnection, 
  ApiResponse, 
  ConnectionTestResult 
} from '../types/oracle';

// Base URL for the Oracle onboarding API
const BASE_URL = 'http://localhost:8083/api/v1/oracle';

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
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
}