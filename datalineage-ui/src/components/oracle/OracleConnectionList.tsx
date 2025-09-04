import React, { useState, useEffect, useCallback } from 'react';
import { OracleConnection } from '../../types/oracle';
import { OracleApiService } from '../../services/oracleApi';
import './OracleConnectionList.css';

interface OracleConnectionListProps {
  onEdit?: (connection: OracleConnection) => void;
  onTest?: (connection: OracleConnection) => void;
  refresh?: boolean;
  onRefreshComplete?: () => void;
}

export const OracleConnectionList: React.FC<OracleConnectionListProps> = ({
  onEdit,
  onTest,
  refresh,
  onRefreshComplete
}) => {
  const [connections, setConnections] = useState<OracleConnection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [testingConnectionId, setTestingConnectionId] = useState<string>('');

  const loadConnections = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const response = await OracleApiService.getConnections();
      setConnections(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load connections');
    } finally {
      setLoading(false);
      if (onRefreshComplete) {
        onRefreshComplete();
      }
    }
  }, [onRefreshComplete]);

  useEffect(() => {
    loadConnections();
  }, [loadConnections]);

  useEffect(() => {
    if (refresh) {
      loadConnections();
    }
  }, [refresh, loadConnections]);

  const handleTestConnection = async (connection: OracleConnection) => {
    setTestingConnectionId(connection.id);
    try {
      await OracleApiService.testConnection(connection.id);
      // Refresh connections to get updated test status
      await loadConnections();
      if (onTest) {
        onTest(connection);
      }
    } catch (err: any) {
      setError(`Connection test failed: ${err.message}`);
    } finally {
      setTestingConnectionId('');
    }
  };

  const handleDelete = async (connection: OracleConnection) => {
    if (window.confirm(`Are you sure you want to delete the connection "${connection.connectionName}"?`)) {
      try {
        await OracleApiService.deleteConnection(connection.id);
        await loadConnections();
      } catch (err: any) {
        setError(`Failed to delete connection: ${err.message}`);
      }
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  const getStatusBadge = (status: string) => {
    const statusClass = status.toLowerCase();
    return <span className={`status-badge status-${statusClass}`}>{status}</span>;
  };

  const getTestResultBadge = (result?: string) => {
    if (!result) return null;
    const resultClass = result.toLowerCase();
    return <span className={`test-result-badge test-${resultClass}`}>{result}</span>;
  };

  if (loading) {
    return (
      <div className="oracle-connection-list">
        <div className="loading-spinner">Loading connections...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="oracle-connection-list">
        <div className="error-message">
          {error}
          <button onClick={loadConnections} className="btn btn-secondary btn-sm">
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="oracle-connection-list">
      <div className="list-header">
        <h3>Oracle Database Connections</h3>
        <button onClick={loadConnections} className="btn btn-secondary btn-sm">
          Refresh
        </button>
      </div>

      {connections.length === 0 ? (
        <div className="empty-state">
          <p>No Oracle database connections found.</p>
          <p>Use the onboarding form to add your first connection.</p>
        </div>
      ) : (
        <div className="connections-grid">
          {connections.map((connection) => (
            <div key={connection.id} className="connection-card">
              <div className="card-header">
                <h4>{connection.connectionName}</h4>
                {getStatusBadge(connection.status)}
              </div>
              
              <div className="card-body">
                {connection.description && (
                  <p className="connection-description">{connection.description}</p>
                )}
                
                <div className="connection-details">
                  <div className="detail-item">
                    <span className="label">Host:</span>
                    <span className="value">{connection.host}:{connection.port}</span>
                  </div>
                  
                  <div className="detail-item">
                    <span className="label">Service:</span>
                    <span className="value">{connection.serviceName}</span>
                  </div>
                  
                  <div className="detail-item">
                    <span className="label">Auth Type:</span>
                    <span className="value">{connection.authenticationType}</span>
                  </div>
                  
                  {connection.username && (
                    <div className="detail-item">
                      <span className="label">Username:</span>
                      <span className="value">{connection.username}</span>
                    </div>
                  )}
                  
                  <div className="detail-item">
                    <span className="label">Created:</span>
                    <span className="value">{formatDate(connection.createdAt)}</span>
                  </div>
                  
                  {connection.lastTestedAt && (
                    <div className="detail-item">
                      <span className="label">Last Tested:</span>
                      <span className="value">
                        {formatDate(connection.lastTestedAt)}
                        {getTestResultBadge(connection.lastTestResult)}
                      </span>
                    </div>
                  )}
                </div>
              </div>
              
              <div className="card-actions">
                <button
                  onClick={() => handleTestConnection(connection)}
                  disabled={testingConnectionId === connection.id}
                  className="btn btn-primary btn-sm"
                >
                  {testingConnectionId === connection.id ? 'Testing...' : 'Test'}
                </button>
                
                {onEdit && (
                  <button
                    onClick={() => onEdit(connection)}
                    className="btn btn-secondary btn-sm"
                  >
                    Edit
                  </button>
                )}
                
                <button
                  onClick={() => handleDelete(connection)}
                  className="btn btn-danger btn-sm"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};