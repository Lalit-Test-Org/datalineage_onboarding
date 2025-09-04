import React, { useState } from 'react';
import { GraphNode, GraphEdge } from '../../types/graph';
import './MetadataDetailPanel.css';

interface MetadataDetailPanelProps {
  selectedNode?: GraphNode;
  selectedEdge?: GraphEdge;
  isVisible: boolean;
  onClose: () => void;
  className?: string;
}

export const MetadataDetailPanel: React.FC<MetadataDetailPanelProps> = ({
  selectedNode,
  selectedEdge,
  isVisible,
  onClose,
  className = ''
}) => {
  const [activeTab, setActiveTab] = useState<'overview' | 'properties' | 'relationships' | 'raw'>('overview');

  if (!isVisible || (!selectedNode && !selectedEdge)) {
    return null;
  }

  const item = selectedNode || selectedEdge;
  const isNode = !!selectedNode;

  if (!item) {
    return null;
  }

  const getTypeIcon = (type: string) => {
    const iconMap: Record<string, string> = {
      'database': '🗄️',
      'schema': '📋',
      'table': '📊',
      'column': '📝',
      'procedure': '⚙️',
      'constraint': '🔗',
      'csv': '📄',
      'xsd': '📰',
      'avro': '🔧',
      'contains': '⊃',
      'relationship': '↔️',
      'foreign_key': '🔑',
      'references': '👉',
      'derived_from': '🔄'
    };
    return iconMap[type] || (isNode ? '⚪' : '↔️');
  };

  const getTypeColor = (type: string) => {
    const colorMap: Record<string, string> = {
      'database': '#4CAF50',
      'schema': '#2196F3',
      'table': '#FF9800',
      'column': '#9C27B0',
      'procedure': '#F44336',
      'constraint': '#795548',
      'csv': '#00BCD4',
      'xsd': '#CDDC39',
      'avro': '#FF5722',
      'contains': '#666',
      'relationship': '#333',
      'foreign_key': '#e91e63',
      'references': '#3f51b5',
      'derived_from': '#9c27b0'
    };
    return colorMap[type] || '#9E9E9E';
  };

  const formatMetadataValue = (key: string, value: any): string => {
    if (value === null || value === undefined) {
      return 'N/A';
    }
    
    if (typeof value === 'boolean') {
      return value ? 'Yes' : 'No';
    }
    
    if (typeof value === 'object') {
      return JSON.stringify(value, null, 2);
    }
    
    if (typeof value === 'number') {
      // Format large numbers with commas
      return value.toLocaleString();
    }
    
    return String(value);
  };

  const getMetadataCategories = (metadata: Record<string, any>) => {
    const categories: Record<string, Record<string, any>> = {
      'Basic Info': {},
      'Database Info': {},
      'Schema Info': {},
      'Performance': {},
      'Security': {},
      'Other': {}
    };

    Object.entries(metadata).forEach(([key, value]) => {
      const lowerKey = key.toLowerCase();
      
      if (['id', 'name', 'label', 'type', 'fullname'].some(k => lowerKey.includes(k))) {
        categories['Basic Info'][key] = value;
      } else if (['owner', 'schema', 'database', 'tablespace'].some(k => lowerKey.includes(k))) {
        categories['Database Info'][key] = value;
      } else if (['datatype', 'length', 'precision', 'scale', 'nullable'].some(k => lowerKey.includes(k))) {
        categories['Schema Info'][key] = value;
      } else if (['rows', 'blocks', 'size', 'avgrowlen'].some(k => lowerKey.includes(k))) {
        categories['Performance'][key] = value;
      } else if (['privilege', 'grant', 'security'].some(k => lowerKey.includes(k))) {
        categories['Security'][key] = value;
      } else {
        categories['Other'][key] = value;
      }
    });

    // Remove empty categories
    return Object.entries(categories).filter(([_, values]) => Object.keys(values).length > 0);
  };

  const renderOverviewTab = () => (
    <div className="metadata-overview">
      <div className="metadata-summary">
        <div className="summary-header">
          <div 
            className="type-badge"
            style={{ backgroundColor: getTypeColor(item.type) }}
          >
            <span className="type-icon">{getTypeIcon(item.type)}</span>
            <span className="type-label">{item.type.replace('_', ' ').toUpperCase()}</span>
          </div>
        </div>
        
        <h3 className="item-label">{item.label}</h3>
        
        {item.metadata.description && (
          <p className="item-description">{item.metadata.description}</p>
        )}
        
        {item.metadata.fullName && item.metadata.fullName !== item.label && (
          <div className="full-name">
            <strong>Full Name:</strong> {item.metadata.fullName}
          </div>
        )}
      </div>

      <div className="key-properties">
        <h4>Key Properties</h4>
        <div className="properties-grid">
          {isNode ? (
            <>
              <div className="property-item">
                <span className="property-label">Type:</span>
                <span className="property-value">{selectedNode?.type}</span>
              </div>
              {selectedNode?.metadata.owner && (
                <div className="property-item">
                  <span className="property-label">Owner:</span>
                  <span className="property-value">{selectedNode.metadata.owner}</span>
                </div>
              )}
              {selectedNode?.metadata.tableName && (
                <div className="property-item">
                  <span className="property-label">Table:</span>
                  <span className="property-value">{selectedNode.metadata.tableName}</span>
                </div>
              )}
              {selectedNode?.metadata.dataType && (
                <div className="property-item">
                  <span className="property-label">Data Type:</span>
                  <span className="property-value">{selectedNode.metadata.dataType}</span>
                </div>
              )}
            </>
          ) : (
            <>
              <div className="property-item">
                <span className="property-label">Edge Type:</span>
                <span className="property-value">{selectedEdge?.type}</span>
              </div>
              <div className="property-item">
                <span className="property-label">Source:</span>
                <span className="property-value">{selectedEdge?.source}</span>
              </div>
              <div className="property-item">
                <span className="property-label">Target:</span>
                <span className="property-value">{selectedEdge?.target}</span>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );

  const renderPropertiesTab = () => {
    const categories = getMetadataCategories(item.metadata);
    
    return (
      <div className="metadata-properties">
        {categories.map(([categoryName, properties]) => (
          <div key={categoryName} className="property-category">
            <h4 className="category-title">{categoryName}</h4>
            <div className="properties-list">
              {Object.entries(properties).map(([key, value]) => (
                <div key={key} className="property-row">
                  <span className="property-key">{key}:</span>
                  <span className="property-value">
                    {formatMetadataValue(key, value)}
                  </span>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    );
  };

  const renderRelationshipsTab = () => (
    <div className="metadata-relationships">
      <div className="relationships-info">
        <p>Relationship information will be available in future versions.</p>
        <p>This will show connected nodes and relationship details.</p>
      </div>
    </div>
  );

  const renderRawTab = () => (
    <div className="metadata-raw">
      <pre className="raw-json">
        {JSON.stringify({
          id: item.id,
          label: item.label,
          type: item.type,
          metadata: item.metadata,
          ...(selectedEdge && { source: selectedEdge.source, target: selectedEdge.target })
        }, null, 2)}
      </pre>
    </div>
  );

  return (
    <div className={`metadata-detail-panel ${isVisible ? 'visible' : 'hidden'} ${className}`}>
      <div className="panel-header">
        <div className="panel-title">
          <span className="title-icon">{getTypeIcon(item.type)}</span>
          <span className="title-text">
            {isNode ? 'Node Details' : 'Edge Details'}
          </span>
        </div>
        <button onClick={onClose} className="close-btn" title="Close panel">
          ✕
        </button>
      </div>

      <div className="panel-tabs">
        <button 
          className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Overview
        </button>
        <button 
          className={`tab-btn ${activeTab === 'properties' ? 'active' : ''}`}
          onClick={() => setActiveTab('properties')}
        >
          Properties
        </button>
        <button 
          className={`tab-btn ${activeTab === 'relationships' ? 'active' : ''}`}
          onClick={() => setActiveTab('relationships')}
        >
          Relationships
        </button>
        <button 
          className={`tab-btn ${activeTab === 'raw' ? 'active' : ''}`}
          onClick={() => setActiveTab('raw')}
        >
          Raw Data
        </button>
      </div>

      <div className="panel-content">
        {activeTab === 'overview' && renderOverviewTab()}
        {activeTab === 'properties' && renderPropertiesTab()}
        {activeTab === 'relationships' && renderRelationshipsTab()}
        {activeTab === 'raw' && renderRawTab()}
      </div>
    </div>
  );
};