import React, { useState, useCallback, useRef, useEffect } from 'react';
import { OracleConnection, DiscoveryStatus, MetadataDiscoveryResponse, MetadataDiscoveryRequest, DiscoveryProgress, AuthenticationType } from '../../types/oracle';
import { OracleApiService } from '../../services/oracleApi';
import { MetadataDiscoveryStatus } from './MetadataDiscoveryStatus';
import { MetadataDiscoveryResults } from './MetadataDiscoveryResults';
import './MetadataDiscoveryPanel.css';

interface MetadataDiscoveryPanelProps {
  connections: OracleConnection[];
  onConnectionSelect?: (connection: OracleConnection) => void;
}

export const MetadataDiscoveryPanel: React.FC<MetadataDiscoveryPanelProps> = ({
  connections,
  onConnectionSelect
}) => {
  // Add demo connections if none are provided (for demonstration purposes)
  const demoConnections: OracleConnection[] = connections.length > 0 ? connections : [
    {
      id: 'demo-oracle-01',
      connectionName: 'Demo Oracle Production',
      description: 'Demo Oracle database for testing',
      host: 'oracle-prod.company.com',
      port: 1521,
      serviceName: 'PROD',
      authenticationType: AuthenticationType.DIRECT,
      username: 'demo_user',
      status: 'ACTIVE',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    {
      id: 'demo-oracle-02',
      connectionName: 'Demo Oracle Development',
      description: 'Demo Oracle development database',
      host: 'oracle-dev.company.com',
      port: 1521,
      serviceName: 'DEV',
      authenticationType: AuthenticationType.KERBEROS,
      status: 'ACTIVE',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    }
  ];

  const [selectedConnectionId, setSelectedConnectionId] = useState<string>('');
  const [discoveryStatus, setDiscoveryStatus] = useState<DiscoveryStatus>(DiscoveryStatus.IDLE);
  const [discoveryProgress, setDiscoveryProgress] = useState<DiscoveryProgress | null>(null);
  const [discoveryResults, setDiscoveryResults] = useState<MetadataDiscoveryResponse | null>(null);
  const [error, setError] = useState<string>('');
  const [isDiscovering, setIsDiscovering] = useState<boolean>(false);
  const progressIntervalRef = useRef<NodeJS.Timeout | null>(null);
  const [discoveryOptions, setDiscoveryOptions] = useState<Partial<MetadataDiscoveryRequest>>({
    schemas: [],
    tablePatterns: [],
    includeTables: true,
    includeColumns: true,
    includeProcedures: true,
    includeConstraints: true,
    limit: 1000,
    offset: 0
  });

  const selectedConnection = demoConnections.find(conn => conn.id === selectedConnectionId);

  // Cleanup progress interval on unmount
  useEffect(() => {
    const intervalRef = progressIntervalRef.current;
    return () => {
      if (intervalRef) {
        clearInterval(intervalRef);
      }
    };
  }, []);

  // Simulate progress steps for better user feedback
  const simulateDiscoveryProgress = useCallback((startTime: number): Promise<void> => {
    return new Promise((resolve) => {
      const progressSteps = [
        { status: DiscoveryStatus.STARTING, progress: 0, message: 'Initializing discovery process...', duration: 500 },
        { status: DiscoveryStatus.CONNECTING, progress: 10, message: 'Establishing database connection...', duration: 1000 },
        { status: DiscoveryStatus.DISCOVERING_TABLES, progress: 25, message: 'Discovering tables and views...', duration: 2000 },
        { status: DiscoveryStatus.DISCOVERING_COLUMNS, progress: 50, message: 'Analyzing column metadata...', duration: 2500 },
        { status: DiscoveryStatus.DISCOVERING_PROCEDURES, progress: 75, message: 'Scanning procedures and functions...', duration: 1500 },
        { status: DiscoveryStatus.DISCOVERING_CONSTRAINTS, progress: 90, message: 'Extracting constraint information...', duration: 1000 },
        { status: DiscoveryStatus.FINALIZING, progress: 95, message: 'Finalizing metadata compilation...', duration: 500 }
      ];

      let currentStepIndex = 0;

      const updateProgress = () => {
        if (currentStepIndex >= progressSteps.length) {
          resolve();
          return;
        }

        const step = progressSteps[currentStepIndex];
        const elapsed = Date.now() - startTime;
        const estimatedTotal = 8000; // 8 seconds estimated total
        const estimatedRemaining = Math.max(0, estimatedTotal - elapsed);

        setDiscoveryStatus(step.status);
        setDiscoveryProgress({
          currentStep: step.status,
          progress: step.progress,
          message: step.message,
          estimatedTimeRemaining: estimatedRemaining,
          startedAt: new Date(startTime).toISOString()
        });

        currentStepIndex++;
        setTimeout(updateProgress, step.duration);
      };

      updateProgress();
    });
  }, []);

  const handleDiscoveryStart = useCallback(async () => {
    if (!selectedConnectionId) {
      setError('Please select a connection first');
      return;
    }

    setIsDiscovering(true);
    setDiscoveryStatus(DiscoveryStatus.STARTING);
    setError('');
    setDiscoveryResults(null);
    setDiscoveryProgress(null);

    const startTime = Date.now();

    try {
      // Start the progress simulation
      const progressPromise = simulateDiscoveryProgress(startTime);
      
      // For demo purposes, create mock discovery results
      const demoResults: MetadataDiscoveryResponse = {
        connectionId: selectedConnectionId,
        tables: [
          {
            id: 'table-1',
            owner: 'HR',
            tableName: 'EMPLOYEES',
            tableType: 'TABLE',
            tablespace: 'USERS',
            numRows: 107,
            status: 'VALID',
            comments: 'Employee information table',
            oracleConnectionId: selectedConnectionId
          },
          {
            id: 'table-2', 
            owner: 'HR',
            tableName: 'DEPARTMENTS',
            tableType: 'TABLE',
            tablespace: 'USERS',
            numRows: 27,
            status: 'VALID',
            comments: 'Department information',
            oracleConnectionId: selectedConnectionId
          }
        ],
        columns: [
          {
            id: 'col-1',
            owner: 'HR',
            tableName: 'EMPLOYEES',
            columnName: 'EMPLOYEE_ID',
            dataType: 'NUMBER',
            dataLength: 22,
            dataPrecision: 6,
            nullable: 'N',
            columnId: 1,
            comments: 'Unique employee identifier',
            oracleConnectionId: selectedConnectionId
          },
          {
            id: 'col-2',
            owner: 'HR', 
            tableName: 'EMPLOYEES',
            columnName: 'FIRST_NAME',
            dataType: 'VARCHAR2',
            dataLength: 20,
            nullable: 'Y',
            columnId: 2,
            comments: 'Employee first name',
            oracleConnectionId: selectedConnectionId
          }
        ],
        procedures: [
          {
            id: 'proc-1',
            owner: 'HR',
            objectName: 'GET_EMPLOYEE',
            objectType: 'PROCEDURE',
            status: 'VALID',
            oracleConnectionId: selectedConnectionId
          }
        ],
        constraints: [
          {
            id: 'cons-1',
            owner: 'HR',
            constraintName: 'EMP_EMP_ID_PK',
            constraintType: 'P',
            tableName: 'EMPLOYEES',
            status: 'ENABLED',
            oracleConnectionId: selectedConnectionId
          }
        ],
        statistics: {
          totalTables: 2,
          totalColumns: 2,
          totalProcedures: 1,
          totalConstraints: 1,
          discoveryTimeMs: 8250
        }
      };
      
      // Start the actual discovery (or use demo results)
      let discoveryPromise: Promise<void>;
      
      if (selectedConnectionId.startsWith('demo-')) {
        // Use demo results for demo connections
        discoveryPromise = new Promise(resolve => {
          setTimeout(() => {
            setDiscoveryResults(demoResults);
            resolve();
          }, 8000); // Match the progress simulation time
        });
      } else {
        // Use real API for actual connections
        discoveryPromise = OracleApiService.discoverMetadata(selectedConnectionId, discoveryOptions)
          .then(response => {
            setDiscoveryResults(response.data);
          });
      }
      
      // Wait for both to complete
      await Promise.all([progressPromise, discoveryPromise]);
      
      setDiscoveryStatus(DiscoveryStatus.COMPLETED);
      setDiscoveryProgress({
        currentStep: DiscoveryStatus.COMPLETED,
        progress: 100,
        message: 'Discovery completed successfully!',
        startedAt: new Date(startTime).toISOString()
      });
    } catch (err: any) {
      setError(err.message || 'Failed to discover metadata');
      setDiscoveryStatus(DiscoveryStatus.FAILED);
      setDiscoveryProgress({
        currentStep: DiscoveryStatus.FAILED,
        progress: 0,
        message: 'Discovery failed: ' + (err.message || 'Unknown error'),
        startedAt: new Date(startTime).toISOString()
      });
    } finally {
      setIsDiscovering(false);
    }
  }, [selectedConnectionId, discoveryOptions, simulateDiscoveryProgress]);

  const handleRetry = () => {
    setDiscoveryStatus(DiscoveryStatus.IDLE);
    setError('');
    setDiscoveryResults(null);
    setDiscoveryProgress(null);
    setIsDiscovering(false);
  };

  const handleOptionsChange = (field: keyof MetadataDiscoveryRequest, value: any) => {
    setDiscoveryOptions(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleArrayInputChange = (field: 'schemas' | 'tablePatterns', value: string) => {
    const arrayValue = value.split(',').map(s => s.trim()).filter(s => s.length > 0);
    handleOptionsChange(field, arrayValue);
  };

  return (
    <div className="metadata-discovery-panel">
      <div className="panel-header">
        <h2>Oracle Metadata Discovery</h2>
        <p>Discover and explore metadata from your Oracle database connections</p>
      </div>

      <div className="discovery-form">
        <div className="form-section">
          <h3>Connection Selection</h3>
          <div className="form-group">
            <label htmlFor="connectionSelect">Select Oracle Connection:</label>
            <select
              id="connectionSelect"
              value={selectedConnectionId}
              onChange={(e) => setSelectedConnectionId(e.target.value)}
              className="connection-select"
              disabled={isDiscovering}
            >
              <option value="">-- Select a connection --</option>
              {demoConnections.map((connection) => (
                <option key={connection.id} value={connection.id}>
                  {connection.connectionName} ({connection.host}:{connection.port}/{connection.serviceName})
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="form-section">
          <h3>Discovery Options</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="schemas">Schemas (comma-separated):</label>
              <input
                type="text"
                id="schemas"
                placeholder="e.g., HR, SALES, FINANCE"
                value={discoveryOptions.schemas?.join(', ') || ''}
                onChange={(e) => handleArrayInputChange('schemas', e.target.value)}
                disabled={isDiscovering}
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="tablePatterns">Table Patterns (comma-separated):</label>
              <input
                type="text"
                id="tablePatterns"
                placeholder="e.g., EMP%, DEPT%, USER_*"
                value={discoveryOptions.tablePatterns?.join(', ') || ''}
                onChange={(e) => handleArrayInputChange('tablePatterns', e.target.value)}
                disabled={isDiscovering}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group checkbox-group">
              <label>
                <input
                  type="checkbox"
                  checked={discoveryOptions.includeTables || false}
                  onChange={(e) => handleOptionsChange('includeTables', e.target.checked)}
                  disabled={isDiscovering}
                />
                Include Tables
              </label>
            </div>
            
            <div className="form-group checkbox-group">
              <label>
                <input
                  type="checkbox"
                  checked={discoveryOptions.includeColumns || false}
                  onChange={(e) => handleOptionsChange('includeColumns', e.target.checked)}
                  disabled={isDiscovering}
                />
                Include Columns
              </label>
            </div>
            
            <div className="form-group checkbox-group">
              <label>
                <input
                  type="checkbox"
                  checked={discoveryOptions.includeProcedures || false}
                  onChange={(e) => handleOptionsChange('includeProcedures', e.target.checked)}
                  disabled={isDiscovering}
                />
                Include Procedures
              </label>
            </div>
            
            <div className="form-group checkbox-group">
              <label>
                <input
                  type="checkbox"
                  checked={discoveryOptions.includeConstraints || false}
                  onChange={(e) => handleOptionsChange('includeConstraints', e.target.checked)}
                  disabled={isDiscovering}
                />
                Include Constraints
              </label>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="limit">Limit:</label>
              <input
                type="number"
                id="limit"
                min="1"
                max="10000"
                value={discoveryOptions.limit || 1000}
                onChange={(e) => handleOptionsChange('limit', parseInt(e.target.value) || 1000)}
                disabled={isDiscovering}
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="offset">Offset:</label>
              <input
                type="number"
                id="offset"
                min="0"
                value={discoveryOptions.offset || 0}
                onChange={(e) => handleOptionsChange('offset', parseInt(e.target.value) || 0)}
                disabled={isDiscovering}
              />
            </div>
          </div>
        </div>

        <div className="action-section">
          {discoveryStatus === DiscoveryStatus.IDLE && (
            <button
              onClick={handleDiscoveryStart}
              disabled={!selectedConnectionId}
              className="discover-button"
            >
              Start Discovery
            </button>
          )}
          
          {isDiscovering && (
            <button className="discover-button running" disabled>
              {discoveryProgress?.message || 'Discovering...'}
            </button>
          )}
          
          {discoveryStatus === DiscoveryStatus.FAILED && (
            <div className="retry-section">
              <button onClick={handleRetry} className="retry-button">
                Reset
              </button>
              <button onClick={handleDiscoveryStart} className="discover-button">
                Retry Discovery
              </button>
            </div>
          )}
          
          {discoveryStatus === DiscoveryStatus.COMPLETED && (
            <div className="success-section">
              <button onClick={handleRetry} className="new-discovery-button">
                New Discovery
              </button>
            </div>
          )}
        </div>
      </div>

      <MetadataDiscoveryStatus
        status={discoveryStatus}
        progress={discoveryProgress}
        statistics={discoveryResults?.statistics}
        error={error}
        connectionName={selectedConnection?.connectionName}
      />

      {discoveryResults && discoveryStatus === DiscoveryStatus.COMPLETED && (
        <MetadataDiscoveryResults
          results={discoveryResults}
          onClose={() => setDiscoveryResults(null)}
        />
      )}
    </div>
  );
};