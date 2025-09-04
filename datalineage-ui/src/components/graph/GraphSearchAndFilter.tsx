import React, { useState, useEffect } from 'react';
import { SearchFilter, MetadataType } from '../../types/graph';
import './GraphSearchAndFilter.css';

interface GraphSearchAndFilterProps {
  onFilterChange: (filter: SearchFilter) => void;
  availableNodeTypes: string[];
  availableEdgeTypes: string[];
  className?: string;
}

export const GraphSearchAndFilter: React.FC<GraphSearchAndFilterProps> = ({
  onFilterChange,
  availableNodeTypes,
  availableEdgeTypes,
  className = ''
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedNodeTypes, setSelectedNodeTypes] = useState<string[]>([]);
  const [selectedEdgeTypes, setSelectedEdgeTypes] = useState<string[]>([]);
  const [isExpanded, setIsExpanded] = useState(false);

  // Metadata type configurations with icons and colors
  const metadataTypes: Record<string, MetadataType> = {
    'database': { id: 'database', name: 'Database', description: 'Database instances', color: '#4CAF50', icon: '🗄️', enabled: true },
    'schema': { id: 'schema', name: 'Schema', description: 'Database schemas', color: '#2196F3', icon: '📋', enabled: true },
    'table': { id: 'table', name: 'Table', description: 'Database tables', color: '#FF9800', icon: '📊', enabled: true },
    'column': { id: 'column', name: 'Column', description: 'Table columns', color: '#9C27B0', icon: '📝', enabled: true },
    'procedure': { id: 'procedure', name: 'Procedure', description: 'Stored procedures', color: '#F44336', icon: '⚙️', enabled: true },
    'constraint': { id: 'constraint', name: 'Constraint', description: 'Database constraints', color: '#795548', icon: '🔗', enabled: true },
    'csv': { id: 'csv', name: 'CSV', description: 'CSV file schemas', color: '#00BCD4', icon: '📄', enabled: true },
    'xsd': { id: 'xsd', name: 'XSD', description: 'XML schema definitions', color: '#CDDC39', icon: '📰', enabled: true },
    'avro': { id: 'avro', name: 'Avro', description: 'Avro schema definitions', color: '#FF5722', icon: '🔧', enabled: true }
  };

  const edgeTypeConfigs: Record<string, { name: string; description: string; icon: string }> = {
    'contains': { name: 'Contains', description: 'Containment relationship', icon: '⊃' },
    'relationship': { name: 'Relationship', description: 'General relationship', icon: '↔️' },
    'foreign_key': { name: 'Foreign Key', description: 'Foreign key constraint', icon: '🔑' },
    'references': { name: 'References', description: 'Reference relationship', icon: '👉' },
    'derived_from': { name: 'Derived From', description: 'Derivation relationship', icon: '🔄' }
  };

  // Update filter when any parameter changes
  useEffect(() => {
    const filter: SearchFilter = {
      query: searchQuery.trim(),
      nodeTypes: selectedNodeTypes,
      edgeTypes: selectedEdgeTypes,
      properties: [] // Future enhancement
    };
    onFilterChange(filter);
  }, [searchQuery, selectedNodeTypes, selectedEdgeTypes, onFilterChange]);

  const handleNodeTypeToggle = (nodeType: string) => {
    setSelectedNodeTypes(prev => 
      prev.includes(nodeType) 
        ? prev.filter(t => t !== nodeType)
        : [...prev, nodeType]
    );
  };

  const handleEdgeTypeToggle = (edgeType: string) => {
    setSelectedEdgeTypes(prev => 
      prev.includes(edgeType) 
        ? prev.filter(t => t !== edgeType)
        : [...prev, edgeType]
    );
  };

  const clearAllFilters = () => {
    setSearchQuery('');
    setSelectedNodeTypes([]);
    setSelectedEdgeTypes([]);
  };

  const selectAllNodeTypes = () => {
    setSelectedNodeTypes([...availableNodeTypes]);
  };

  const selectAllEdgeTypes = () => {
    setSelectedEdgeTypes([...availableEdgeTypes]);
  };

  const hasActiveFilters = searchQuery.trim() !== '' || 
                          selectedNodeTypes.length > 0 || 
                          selectedEdgeTypes.length > 0;

  return (
    <div className={`graph-search-filter ${className}`}>
      {/* Search Input */}
      <div className="search-input-container">
        <div className="search-input-wrapper">
          <span className="search-icon">🔍</span>
          <input
            type="text"
            placeholder="Search metadata entities..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />
          {searchQuery && (
            <button 
              onClick={() => setSearchQuery('')}
              className="clear-search-btn"
              title="Clear search"
            >
              ✕
            </button>
          )}
        </div>
        
        <button 
          onClick={() => setIsExpanded(!isExpanded)}
          className={`filter-toggle-btn ${isExpanded ? 'expanded' : ''}`}
          title={isExpanded ? 'Hide filters' : 'Show filters'}
        >
          <span className="filter-icon">🎛️</span>
          <span className="filter-text">Filters</span>
          {hasActiveFilters && <span className="filter-badge">{
            (selectedNodeTypes.length + selectedEdgeTypes.length) + (searchQuery ? 1 : 0)
          }</span>}
        </button>
      </div>

      {/* Expandable Filter Panel */}
      {isExpanded && (
        <div className="filter-panel">
          {/* Quick Actions */}
          <div className="filter-actions">
            <button onClick={clearAllFilters} className="filter-action-btn clear">
              Clear All
            </button>
            <button onClick={selectAllNodeTypes} className="filter-action-btn select">
              Select All Nodes
            </button>
            <button onClick={selectAllEdgeTypes} className="filter-action-btn select">
              Select All Edges
            </button>
          </div>

          {/* Node Type Filters */}
          {availableNodeTypes.length > 0 && (
            <div className="filter-section">
              <h4 className="filter-section-title">
                <span className="section-icon">🔸</span>
                Node Types ({selectedNodeTypes.length}/{availableNodeTypes.length})
              </h4>
              <div className="filter-options">
                {availableNodeTypes.map(nodeType => {
                  const config = metadataTypes[nodeType];
                  const isSelected = selectedNodeTypes.includes(nodeType);
                  
                  return (
                    <label 
                      key={nodeType}
                      className={`filter-option ${isSelected ? 'selected' : ''}`}
                      style={{ '--accent-color': config?.color || '#9E9E9E' } as React.CSSProperties}
                    >
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => handleNodeTypeToggle(nodeType)}
                        className="filter-checkbox"
                      />
                      <span className="option-icon">{config?.icon || '⚪'}</span>
                      <span className="option-label">{config?.name || nodeType}</span>
                      <span className="option-description">{config?.description || ''}</span>
                    </label>
                  );
                })}
              </div>
            </div>
          )}

          {/* Edge Type Filters */}
          {availableEdgeTypes.length > 0 && (
            <div className="filter-section">
              <h4 className="filter-section-title">
                <span className="section-icon">🔗</span>
                Edge Types ({selectedEdgeTypes.length}/{availableEdgeTypes.length})
              </h4>
              <div className="filter-options">
                {availableEdgeTypes.map(edgeType => {
                  const config = edgeTypeConfigs[edgeType];
                  const isSelected = selectedEdgeTypes.includes(edgeType);
                  
                  return (
                    <label 
                      key={edgeType}
                      className={`filter-option ${isSelected ? 'selected' : ''}`}
                    >
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => handleEdgeTypeToggle(edgeType)}
                        className="filter-checkbox"
                      />
                      <span className="option-icon">{config?.icon || '↔️'}</span>
                      <span className="option-label">{config?.name || edgeType}</span>
                      <span className="option-description">{config?.description || ''}</span>
                    </label>
                  );
                })}
              </div>
            </div>
          )}

          {/* Active Filters Summary */}
          {hasActiveFilters && (
            <div className="active-filters-summary">
              <h5>Active Filters:</h5>
              <div className="active-filters-list">
                {searchQuery && (
                  <span className="active-filter">
                    Search: "{searchQuery}"
                    <button onClick={() => setSearchQuery('')}>✕</button>
                  </span>
                )}
                {selectedNodeTypes.map(type => (
                  <span key={`node-${type}`} className="active-filter">
                    {metadataTypes[type]?.icon || '⚪'} {metadataTypes[type]?.name || type}
                    <button onClick={() => handleNodeTypeToggle(type)}>✕</button>
                  </span>
                ))}
                {selectedEdgeTypes.map(type => (
                  <span key={`edge-${type}`} className="active-filter">
                    {edgeTypeConfigs[type]?.icon || '↔️'} {edgeTypeConfigs[type]?.name || type}
                    <button onClick={() => handleEdgeTypeToggle(type)}>✕</button>
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};