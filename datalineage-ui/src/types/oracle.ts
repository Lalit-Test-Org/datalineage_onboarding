// Types for Oracle database onboarding

export enum AuthenticationType {
  DIRECT = 'DIRECT',
  KERBEROS = 'KERBEROS'
}

export interface OracleOnboardingRequest {
  connectionName: string;
  description?: string;
  host: string;
  port: number;
  serviceName: string;
  authenticationType: AuthenticationType;
  
  // Direct authentication fields
  username?: string;
  password?: string;
  
  // Kerberos authentication fields
  kerberosRealm?: string;
  kerberosKdc?: string;
  kerberosPrincipal?: string;
  kerberosKeytabPath?: string;
  
  // Optional connection settings
  connectionTimeout?: number;
  readTimeout?: number;
  useSSL?: boolean;
  sslTruststore?: string;
  sslTruststorePassword?: string;
  autoDiscoverMetadata?: boolean;
}

export interface OracleConnection {
  id: string;
  connectionName: string;
  description?: string;
  host: string;
  port: number;
  serviceName: string;
  authenticationType: AuthenticationType;
  username?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  lastTestedAt?: string;
  lastTestResult?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
}

export interface ConnectionTestResult {
  connectionValid: boolean;
  connectionId: string;
  testedAt: string;
}

export interface FormErrors {
  [key: string]: string;
}