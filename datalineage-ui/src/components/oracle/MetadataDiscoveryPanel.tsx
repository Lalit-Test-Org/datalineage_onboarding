import React, { useState, useCallback, useRef, useEffect } from 'react';
import { OracleConnection, DiscoveryStatus, MetadataDiscoveryResponse, DiscoveryProgress, AuthenticationType } from '../../types/oracle';
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
      connectionName: 'Sample Oracle DB (localhost:1521)',
      description: 'Demo Oracle Database connection',
      host: 'localhost',
      port: 1521,
      serviceName: 'XEPDB1',
      username: 'demo_user',
      authenticationType: AuthenticationType.DIRECT,
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      lastTestedAt: new Date().toISOString(),
      lastTestResult: 'SUCCESS'
    }
  ];

  // State for discovery form
  const [selectedConnectionId, setSelectedConnectionId] = useState<string>('');
  const [schemas, setSchemas] = useState<string>('');
  const [tablePatterns, setTablePatterns] = useState<string>('');
  const [includeTables, setIncludeTables] = useState<boolean>(true);
  const [includeColumns, setIncludeColumns] = useState<boolean>(true);
  const [includeProcedures, setIncludeProcedures] = useState<boolean>(false);
  const [includeConstraints, setIncludeConstraints] = useState<boolean>(false);
  const [limit, setLimit] = useState<number>(100);
  const [offset, setOffset] = useState<number>(0);

  // State for discovery process
  const [isDiscovering, setIsDiscovering] = useState<boolean>(false);
  const [discoveryStatus, setDiscoveryStatus] = useState<DiscoveryStatus>(DiscoveryStatus.IDLE);
  const [discoveryProgress, setDiscoveryProgress] = useState<DiscoveryProgress | null>(null);
  const [discoveryResults, setDiscoveryResults] = useState<MetadataDiscoveryResponse | null>(null);
  const [discoveryError, setDiscoveryError] = useState<string | null>(null);

  // Progress simulation
  const progressIntervalRef = useRef<NodeJS.Timeout | null>(null);

  const simulateProgress = useCallback(() => {
    const steps = [
      { status: DiscoveryStatus.STARTING, message: 'Initializing discovery process...', duration: 2000 },
      { status: DiscoveryStatus.CONNECTING, message: 'Connecting to Oracle database...', duration: 3000 },
      { status: DiscoveryStatus.DISCOVERING_TABLES, message: 'Discovering tables and views...', duration: 8000 },
      { status: DiscoveryStatus.DISCOVERING_COLUMNS, message: 'Analyzing column metadata...', duration: 6000 },
      { status: DiscoveryStatus.DISCOVERING_PROCEDURES, message: 'Scanning stored procedures...', duration: 4000 },
      { status: DiscoveryStatus.DISCOVERING_CONSTRAINTS, message: 'Mapping constraints and relationships...', duration: 5000 },
      { status: DiscoveryStatus.FINALIZING, message: 'Finalizing metadata collection...', duration: 2000 }
    ];

    let stepIndex = 0;
    const totalDuration = steps.reduce((sum, step) => sum + step.duration, 0);
    let elapsedTime = 0;

    const updateProgress = () => {
      if (stepIndex < steps.length) {
        const currentStep = steps[stepIndex];
        elapsedTime += 500; // Update every 500ms
        
        const stepProgress = Math.min(elapsedTime, currentStep.duration) / currentStep.duration;
        const overallProgress = ((stepIndex + stepProgress) / steps.length) * 100;
        
        setDiscoveryStatus(currentStep.status);
        setDiscoveryProgress({
          currentStep: currentStep.status,
          progress: Math.round(overallProgress),
          message: currentStep.message,
          estimatedTimeRemaining: totalDuration - elapsedTime,
          startedAt: new Date().toISOString()
        });

        if (elapsedTime >= currentStep.duration) {
          stepIndex++;
          elapsedTime = 0;
        }

        if (stepIndex >= steps.length) {
          // Discovery completed
          setDiscoveryStatus(DiscoveryStatus.COMPLETED);
          setIsDiscovering(false);
          
          // Generate mock results
          const mockResults: MetadataDiscoveryResponse = {
            connectionId: selectedConnectionId,
            tables: [
              {
                id: 'table-1',
                owner: 'DEMO_SCHEMA',
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
                owner: 'DEMO_SCHEMA',
                tableName: 'DEPARTMENTS',
                tableType: 'TABLE',
                tablespace: 'USERS',
                numRows: 27,
                status: 'VALID',
                comments: 'Department information table',
                oracleConnectionId: selectedConnectionId
              }
            ],
            columns: [
              {
                id: 'col-1',
                owner: 'DEMO_SCHEMA',
                tableName: 'EMPLOYEES',
                columnName: 'EMPLOYEE_ID',
                dataType: 'NUMBER',
                dataPrecision: 6,
                dataScale: 0,
                nullable: 'N',
                columnId: 1,
                comments: 'Primary key',
                oracleConnectionId: selectedConnectionId
              },
              {
                id: 'col-2',
                owner: 'DEMO_SCHEMA',
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
            statistics: {
              totalTables: 2,
              totalColumns: 15,
              totalProcedures: 0,
              totalConstraints: 0,
              discoveryTimeMs: totalDuration
            }
          };

          setDiscoveryResults(mockResults);
          
          if (progressIntervalRef.current) {
            clearInterval(progressIntervalRef.current);
            progressIntervalRef.current = null;
          }
        }
      }
    };

    progressIntervalRef.current = setInterval(updateProgress, 500);
  }, [selectedConnectionId]);

  const handleStartDiscovery = useCallback(async () => {
    if (!selectedConnectionId) {
      setDiscoveryError('Please select a connection');
      return;
    }

    setIsDiscovering(true);

    setDiscoveryError(null);
    setDiscoveryResults(null);
    setDiscoveryStatus(DiscoveryStatus.STARTING);

    // Build discovery request
    // const discoveryRequest: Partial<MetadataDiscoveryRequest> = {
    //   schemas: schemas ? schemas.split(',').map(s => s.trim()) : undefined,
    //   tablePatterns: tablePatterns ? tablePatterns.split(',').map(s => s.trim()) : undefined,
    //   includeTables,
    //   includeColumns,
    //   includeProcedures,
    //   includeConstraints,
    //   limit,
    //   offset
    // };

    try {
      // Start progress simulation
      simulateProgress();

      // For demo purposes, we're using the simulation
      // In a real implementation, you would call:
      // const response = await OracleApiService.discoverMetadata(selectedConnectionId, discoveryRequest);
      // setDiscoveryResults(response.data);
      
    } catch (error: any) {
      setIsDiscovering(false);
      setDiscoveryStatus(DiscoveryStatus.FAILED);
      setDiscoveryError(error.message || 'Discovery failed');
      
      if (progressIntervalRef.current) {
        clearInterval(progressIntervalRef.current);
        progressIntervalRef.current = null;
      }
    }
  }, [selectedConnectionId, simulateProgress]);

  const handleStopDiscovery = useCallback(() => {
    setIsDiscovering(false);
    setDiscoveryStatus(DiscoveryStatus.IDLE);
    setDiscoveryProgress(null);
    
    if (progressIntervalRef.current) {
      clearInterval(progressIntervalRef.current);
      progressIntervalRef.current = null;
    }
  }, []);

  const handleResetForm = useCallback(() => {
    setSelectedConnectionId('');
    setSchemas('');
    setTablePatterns('');
    setIncludeTables(true);
    setIncludeColumns(true);
    setIncludeProcedures(false);
    setIncludeConstraints(false);
    setLimit(100);
    setOffset(0);
    setDiscoveryResults(null);
    setDiscoveryError(null);
    setDiscoveryStatus(DiscoveryStatus.IDLE);
    setDiscoveryProgress(null);
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (progressIntervalRef.current) {
        clearInterval(progressIntervalRef.current);
      }
    };
  }, []);

  return (
    <div className="metadata-discovery-panel">
      <div className="panel-header">
        <h2>Metadata Discovery</h2>
        <p>Discover and analyze database metadata from your Oracle connections</p>
      </div>

      <div className="discovery-form">
        <div className="form-section">
          <h3>Connection Selection</h3>
          <div className="form-group">
            <label htmlFor="connection-select">Select Oracle Connection:</label>
            <select
              id="connection-select"
              className="connection-select"
              value={selectedConnectionId}
              onChange={(e) => setSelectedConnectionId(e.target.value)}
              disabled={isDiscovering}
            >
              <option value="">-- Select a connection --</option>
              {demoConnections.map((connection) => (
                <option key={connection.id} value={connection.id}>
                  {connection.connectionName}
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
                value={schemas}
                onChange={(e) => setSchemas(e.target.value)}
                placeholder="e.g., DEMO_SCHEMA, HR, SALES"
                disabled={isDiscovering}
              />
            </div>
            <div className="form-group">
              <label htmlFor="table-patterns">Table Patterns (comma-separated):</label>
              <input
                type="text"
                id="table-patterns"
                value={tablePatterns}
                onChange={(e) => setTablePatterns(e.target.value)}
                placeholder="e.g., EMP%, DEPT%, %_LOG"
                disabled={isDiscovering}
              />
            </div>
          </div>

          <div className="form-row quad">
            <div className="checkbox-group">
              <input
                type="checkbox"
                id="include-tables"
                checked={includeTables}
                onChange={(e) => setIncludeTables(e.target.checked)}
                disabled={isDiscovering}
              />
              <label htmlFor="include-tables">Include Tables</label>
            </div>
            <div className="checkbox-group">
              <input
                type="checkbox"
                id="include-columns"
                checked={includeColumns}
                onChange={(e) => setIncludeColumns(e.target.checked)}
                disabled={isDiscovering}
              />
              <label htmlFor="include-columns">Include Columns</label>
            </div>
            <div className="checkbox-group">
              <input
                type="checkbox"
                id="include-procedures"
                checked={includeProcedures}
                onChange={(e) => setIncludeProcedures(e.target.checked)}
                disabled={isDiscovering}
              />
              <label htmlFor="include-procedures">Include Procedures</label>
            </div>
            <div className="checkbox-group">
              <input
                type="checkbox"
                id="include-constraints"
                checked={includeConstraints}
                onChange={(e) => setIncludeConstraints(e.target.checked)}
                disabled={isDiscovering}
              />
              <label htmlFor="include-constraints">Include Constraints</label>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">

              <label htmlFor="limit">Limit (max records):</label>
              <input
                type="number"
                id="limit"
                value={limit}
                onChange={(e) => setLimit(parseInt(e.target.value))}
                min="1"
                max="10000"
                disabled={isDiscovering}
              />
            </div>
            <div className="form-group">
              <label htmlFor="offset">Offset (skip records):</label>
              <input
                type="number"
                id="offset"
                value={offset}
                onChange={(e) => setOffset(parseInt(e.target.value))}
                min="0"
                disabled={isDiscovering}
              />
            </div>
          </div>
        </div>

        <div className="form-actions">
          {!isDiscovering ? (
            <>
              <button
                className="btn btn-primary"
                onClick={handleStartDiscovery}
                disabled={!selectedConnectionId}
              >
                Start Discovery
              </button>
              <button
                className="btn btn-secondary"
                onClick={handleResetForm}
              >
                Reset Form
              </button>
            </>
          ) : (
            <button
              className="btn btn-danger"
              onClick={handleStopDiscovery}
            >
              Stop Discovery
            </button>
          )}
        </div>

        {discoveryError && (
          <div className="error-message">
            <strong>Error:</strong> {discoveryError}
          </div>
        )}
      </div>

      {(isDiscovering || discoveryProgress) && (
        <MetadataDiscoveryStatus
          status={discoveryStatus}
          progress={discoveryProgress}
        />
      )}

      {discoveryResults && (
        <MetadataDiscoveryResults
          results={discoveryResults}
        />
      )}
    </div>
  );
};