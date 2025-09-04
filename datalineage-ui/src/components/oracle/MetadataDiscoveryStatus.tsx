import React from 'react';
import { DiscoveryStatus, DiscoveryStatistics, DiscoveryProgress } from '../../types/oracle';
import './MetadataDiscoveryStatus.css';

interface MetadataDiscoveryStatusProps {
  status: DiscoveryStatus;
  progress?: DiscoveryProgress | null;
  statistics?: DiscoveryStatistics;
  error?: string;
  connectionName?: string;
}

export const MetadataDiscoveryStatus: React.FC<MetadataDiscoveryStatusProps> = ({
  status,
  progress,
  statistics,
  error,
  connectionName
}) => {
  const getStatusIcon = () => {
    switch (status) {
      case DiscoveryStatus.STARTING:
      case DiscoveryStatus.CONNECTING:
      case DiscoveryStatus.DISCOVERING_TABLES:
      case DiscoveryStatus.DISCOVERING_COLUMNS:
      case DiscoveryStatus.DISCOVERING_PROCEDURES:
      case DiscoveryStatus.DISCOVERING_CONSTRAINTS:
      case DiscoveryStatus.FINALIZING:
      case DiscoveryStatus.RUNNING:
        return <div className="status-icon running">⏳</div>;
      case DiscoveryStatus.COMPLETED:
        return <div className="status-icon completed">✅</div>;
      case DiscoveryStatus.FAILED:
        return <div className="status-icon failed">❌</div>;
      default:
        return <div className="status-icon idle">⭕</div>;
    }
  };

  const getStatusText = () => {
    if (progress?.message) {
      return progress.message;
    }
    
    switch (status) {
      case DiscoveryStatus.STARTING:
        return 'Starting discovery process...';
      case DiscoveryStatus.CONNECTING:
        return 'Connecting to database...';
      case DiscoveryStatus.DISCOVERING_TABLES:
        return 'Discovering tables and views...';
      case DiscoveryStatus.DISCOVERING_COLUMNS:
        return 'Analyzing column metadata...';
      case DiscoveryStatus.DISCOVERING_PROCEDURES:
        return 'Scanning procedures and functions...';
      case DiscoveryStatus.DISCOVERING_CONSTRAINTS:
        return 'Extracting constraint information...';
      case DiscoveryStatus.FINALIZING:
        return 'Finalizing metadata compilation...';
      case DiscoveryStatus.RUNNING:
        return 'Discovering metadata...';
      case DiscoveryStatus.COMPLETED:
        return 'Discovery completed successfully';
      case DiscoveryStatus.FAILED:
        return 'Discovery failed';
      default:
        return 'Ready for discovery';
    }
  };

  const formatTime = (timeMs: number) => {
    if (timeMs < 1000) return `${timeMs}ms`;
    if (timeMs < 60000) return `${(timeMs / 1000).toFixed(1)}s`;
    return `${(timeMs / 60000).toFixed(1)}m`;
  };

  const isDiscovering = [
    DiscoveryStatus.STARTING,
    DiscoveryStatus.CONNECTING,
    DiscoveryStatus.DISCOVERING_TABLES,
    DiscoveryStatus.DISCOVERING_COLUMNS,
    DiscoveryStatus.DISCOVERING_PROCEDURES,
    DiscoveryStatus.DISCOVERING_CONSTRAINTS,
    DiscoveryStatus.FINALIZING,
    DiscoveryStatus.RUNNING
  ].includes(status);

  return (
    <div className={`metadata-discovery-status ${status.toLowerCase()}`}>
      <div className="status-header">
        {getStatusIcon()}
        <div className="status-info">
          <h3>Metadata Discovery Status</h3>
          {connectionName && <p className="connection-name">Connection: {connectionName}</p>}
          <p className="status-text">{getStatusText()}</p>
        </div>
      </div>

      {isDiscovering && progress && (
        <div className="progress-indicator">
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{ width: `${progress.progress}%` }}
            ></div>
          </div>
          <div className="progress-details">
            <p className="progress-message">{progress.message}</p>
            <div className="progress-info">
              <span className="progress-percentage">{progress.progress}%</span>
              {progress.estimatedTimeRemaining && progress.estimatedTimeRemaining > 0 && (
                <span className="time-remaining">
                  ~{formatTime(progress.estimatedTimeRemaining)} remaining
                </span>
              )}
            </div>
          </div>
        </div>
      )}

      {status === DiscoveryStatus.RUNNING && !progress && (
        <div className="progress-indicator">
          <div className="progress-bar">
            <div className="progress-fill"></div>
          </div>
          <p>Processing Oracle database metadata...</p>
        </div>
      )}

      {error && status === DiscoveryStatus.FAILED && (
        <div className="error-details">
          <h4>Error Details:</h4>
          <p className="error-message">{error}</p>
        </div>
      )}

      {statistics && status === DiscoveryStatus.COMPLETED && (
        <div className="discovery-summary">
          <h4>Discovery Summary</h4>
          <div className="summary-grid">
            <div className="summary-item">
              <span className="summary-count">{statistics.totalTables}</span>
              <span className="summary-label">Tables</span>
            </div>
            <div className="summary-item">
              <span className="summary-count">{statistics.totalColumns}</span>
              <span className="summary-label">Columns</span>
            </div>
            <div className="summary-item">
              <span className="summary-count">{statistics.totalProcedures}</span>
              <span className="summary-label">Procedures</span>
            </div>
            <div className="summary-item">
              <span className="summary-count">{statistics.totalConstraints}</span>
              <span className="summary-label">Constraints</span>
            </div>
          </div>
          <div className="discovery-time">
            <span>Discovery completed in {formatTime(statistics.discoveryTimeMs)}</span>
          </div>
        </div>
      )}
    </div>
  );
};