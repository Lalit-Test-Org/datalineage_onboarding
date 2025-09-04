import React, { useState } from 'react';
import { MetadataDiscoveryResponse } from '../../types/oracle';
import './MetadataDiscoveryResults.css';

interface MetadataDiscoveryResultsProps {
  results: MetadataDiscoveryResponse;
  onClose?: () => void;
}

type TabType = 'tables' | 'columns' | 'procedures' | 'constraints';

export const MetadataDiscoveryResults: React.FC<MetadataDiscoveryResultsProps> = ({
  results,
  onClose
}) => {
  const [activeTab, setActiveTab] = useState<TabType>('tables');
  const [searchTerm, setSearchTerm] = useState('');

  const filterItems = <T extends { owner: string; tableName?: string; columnName?: string; objectName?: string; constraintName?: string }>(
    items: T[] | undefined,
    searchTerm: string
  ): T[] => {
    if (!items) return [];
    if (!searchTerm) return items;
    
    return items.filter(item => {
      const searchLower = searchTerm.toLowerCase();
      return (
        item.owner.toLowerCase().includes(searchLower) ||
        (item.tableName && item.tableName.toLowerCase().includes(searchLower)) ||
        (item.columnName && item.columnName.toLowerCase().includes(searchLower)) ||
        (item.objectName && item.objectName.toLowerCase().includes(searchLower)) ||
        (item.constraintName && item.constraintName.toLowerCase().includes(searchLower))
      );
    });
  };

  const renderTables = () => {
    const filteredTables = filterItems(results.tables, searchTerm);
    
    return (
      <div className="results-table">
        <div className="table-header">
          <div>Owner</div>
          <div>Table Name</div>
          <div>Type</div>
          <div>Rows</div>
          <div>Status</div>
          <div>Comments</div>
        </div>
        {filteredTables.map((table) => (
          <div key={table.id} className="table-row">
            <div className="owner-cell">{table.owner}</div>
            <div className="table-name-cell">{table.tableName}</div>
            <div className="type-cell">{table.tableType}</div>
            <div className="rows-cell">{table.numRows?.toLocaleString() || 'N/A'}</div>
            <div className={`status-cell ${table.status.toLowerCase()}`}>{table.status}</div>
            <div className="comments-cell" title={table.comments || 'No comments'}>
              {table.comments || 'No comments'}
            </div>
          </div>
        ))}
        {filteredTables.length === 0 && (
          <div className="empty-results">No tables match your search criteria.</div>
        )}
      </div>
    );
  };

  const renderColumns = () => {
    const filteredColumns = filterItems(results.columns, searchTerm);
    
    return (
      <div className="results-table">
        <div className="table-header">
          <div>Owner</div>
          <div>Table</div>
          <div>Column</div>
          <div>Data Type</div>
          <div>Length</div>
          <div>Nullable</div>
          <div>Comments</div>
        </div>
        {filteredColumns.map((column) => (
          <div key={column.id} className="table-row">
            <div className="owner-cell">{column.owner}</div>
            <div className="table-name-cell">{column.tableName}</div>
            <div className="column-name-cell">{column.columnName}</div>
            <div className="datatype-cell">
              {column.dataType}
              {column.dataPrecision && `(${column.dataPrecision}${column.dataScale ? `,${column.dataScale}` : ''})`}
            </div>
            <div className="length-cell">{column.dataLength || 'N/A'}</div>
            <div className={`nullable-cell ${column.nullable === 'Y' ? 'nullable' : 'not-nullable'}`}>
              {column.nullable === 'Y' ? 'Yes' : 'No'}
            </div>
            <div className="comments-cell" title={column.comments || 'No comments'}>
              {column.comments || 'No comments'}
            </div>
          </div>
        ))}
        {filteredColumns.length === 0 && (
          <div className="empty-results">No columns match your search criteria.</div>
        )}
      </div>
    );
  };

  const renderProcedures = () => {
    const filteredProcedures = filterItems(results.procedures, searchTerm);
    
    return (
      <div className="results-table">
        <div className="table-header">
          <div>Owner</div>
          <div>Object Name</div>
          <div>Object Type</div>
          <div>Status</div>
        </div>
        {filteredProcedures.map((procedure) => (
          <div key={procedure.id} className="table-row">
            <div className="owner-cell">{procedure.owner}</div>
            <div className="object-name-cell">{procedure.objectName}</div>
            <div className="type-cell">{procedure.objectType}</div>
            <div className={`status-cell ${procedure.status.toLowerCase()}`}>{procedure.status}</div>
          </div>
        ))}
        {filteredProcedures.length === 0 && (
          <div className="empty-results">No procedures match your search criteria.</div>
        )}
      </div>
    );
  };

  const renderConstraints = () => {
    const filteredConstraints = filterItems(results.constraints, searchTerm);
    
    return (
      <div className="results-table">
        <div className="table-header">
          <div>Owner</div>
          <div>Constraint Name</div>
          <div>Type</div>
          <div>Table Name</div>
          <div>Status</div>
        </div>
        {filteredConstraints.map((constraint) => (
          <div key={constraint.id} className="table-row">
            <div className="owner-cell">{constraint.owner}</div>
            <div className="constraint-name-cell">{constraint.constraintName}</div>
            <div className="type-cell">{constraint.constraintType}</div>
            <div className="table-name-cell">{constraint.tableName}</div>
            <div className={`status-cell ${constraint.status.toLowerCase()}`}>{constraint.status}</div>
          </div>
        ))}
        {filteredConstraints.length === 0 && (
          <div className="empty-results">No constraints match your search criteria.</div>
        )}
      </div>
    );
  };

  const getTabCounts = () => ({
    tables: results.tables?.length || 0,
    columns: results.columns?.length || 0,
    procedures: results.procedures?.length || 0,
    constraints: results.constraints?.length || 0
  });

  const counts = getTabCounts();

  return (
    <div className="metadata-discovery-results">
      <div className="results-header">
        <div className="header-info">
          <h3>Discovery Results</h3>
          <p>Connection: {results.connectionId}</p>
        </div>
        {onClose && (
          <button className="close-button" onClick={onClose}>
            ✕
          </button>
        )}
      </div>

      <div className="search-section">
        <input
          type="text"
          placeholder="Search metadata..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>

      <div className="results-tabs">
        <button
          className={`tab-button ${activeTab === 'tables' ? 'active' : ''}`}
          onClick={() => setActiveTab('tables')}
        >
          Tables ({counts.tables})
        </button>
        <button
          className={`tab-button ${activeTab === 'columns' ? 'active' : ''}`}
          onClick={() => setActiveTab('columns')}
        >
          Columns ({counts.columns})
        </button>
        <button
          className={`tab-button ${activeTab === 'procedures' ? 'active' : ''}`}
          onClick={() => setActiveTab('procedures')}
        >
          Procedures ({counts.procedures})
        </button>
        <button
          className={`tab-button ${activeTab === 'constraints' ? 'active' : ''}`}
          onClick={() => setActiveTab('constraints')}
        >
          Constraints ({counts.constraints})
        </button>
      </div>

      <div className="results-content">
        {activeTab === 'tables' && renderTables()}
        {activeTab === 'columns' && renderColumns()}
        {activeTab === 'procedures' && renderProcedures()}
        {activeTab === 'constraints' && renderConstraints()}
      </div>
    </div>
  );
};