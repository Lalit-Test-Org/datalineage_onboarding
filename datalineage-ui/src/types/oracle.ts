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

// Metadata Discovery Types
export interface MetadataDiscoveryRequest {
  connectionId: string;
  schemas?: string[];
  tablePatterns?: string[];
  includeTables?: boolean;
  includeColumns?: boolean;
  includeProcedures?: boolean;
  includeConstraints?: boolean;
  limit?: number;
  offset?: number;
}

export interface OracleTable {
  id: string;
  owner: string;
  tableName: string;
  tableType: string;
  tablespace?: string;
  numRows?: number;
  status: string;
  comments?: string;
  oracleConnectionId: string;
}

export interface OracleColumn {
  id: string;
  owner: string;
  tableName: string;
  columnName: string;
  dataType: string;
  dataLength?: number;
  dataPrecision?: number;
  dataScale?: number;
  nullable: string;
  columnId: number;
  comments?: string;
  oracleConnectionId: string;
}

export interface OracleProcedure {
  id: string;
  owner: string;
  objectName: string;
  objectType: string;
  status: string;
  oracleConnectionId: string;
}

export interface OracleConstraint {
  id: string;
  owner: string;
  constraintName: string;
  constraintType: string;
  tableName: string;
  status: string;
  oracleConnectionId: string;
}

export interface DiscoveryProgress {
  currentStep: DiscoveryStatus;
  progress: number; // 0-100
  message: string;
  estimatedTimeRemaining?: number; // in milliseconds
  startedAt?: string;
}

export interface DiscoveryStatistics {
  totalTables: number;
  totalColumns: number;
  totalProcedures: number;
  totalConstraints: number;
  discoveryTimeMs: number;
}

export interface MetadataDiscoveryResponse {
  connectionId: string;
  tables?: OracleTable[];
  columns?: OracleColumn[];
  procedures?: OracleProcedure[];
  constraints?: OracleConstraint[];
  statistics: DiscoveryStatistics;
}

export enum DiscoveryStatus {
  IDLE = 'IDLE',
  STARTING = 'STARTING',
  CONNECTING = 'CONNECTING',
  DISCOVERING_TABLES = 'DISCOVERING_TABLES',
  DISCOVERING_COLUMNS = 'DISCOVERING_COLUMNS',
  DISCOVERING_PROCEDURES = 'DISCOVERING_PROCEDURES',
  DISCOVERING_CONSTRAINTS = 'DISCOVERING_CONSTRAINTS',
  FINALIZING = 'FINALIZING',
  RUNNING = 'RUNNING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}