# Interactive Graph Visualization for Metadata

This document describes the new interactive graph visualization feature for exploring metadata relationships.

## Overview

The Interactive Graph Visualization component provides a powerful way to explore and understand complex data lineage relationships through an interactive graph interface. It supports multiple metadata types and provides intuitive navigation and exploration capabilities.

## Features

### 🎯 Core Functionality
- **Interactive Graph Visualization**: Powered by Cytoscape.js for high-performance rendering
- **Multiple Metadata Support**: Database tables, columns, procedures, constraints, CSV, XSD, and Avro schemas
- **Real-time Data Integration**: Connects to backend APIs for live metadata discovery
- **Search and Filtering**: Advanced filtering by node types, edge types, and text search
- **Detailed Metadata View**: Comprehensive detail panel with tabbed information

### 🔍 Graph Exploration
- **Zoom and Pan**: Smooth navigation with mouse wheel and drag controls
- **Node Selection**: Click nodes and edges to view detailed information
- **Visual Styling**: Color-coded nodes and edges by type with distinct shapes
- **Layout Algorithms**: Multiple layout options including force-directed, hierarchical, and grid

### 🎛️ User Controls
- **Graph Controls**: Fit to content, center, reset zoom, and image export
- **Search Bar**: Real-time text search across all metadata properties
- **Type Filters**: Toggle visibility of specific node and edge types
- **Connection Selector**: Choose from available database connections

### 📱 Responsive Design
- **Desktop Optimized**: Full-screen experience with side panels
- **Mobile Support**: Responsive layout that works on tablets and phones
- **Accessibility**: Keyboard navigation and screen reader support

## Architecture

### Components Structure
```
src/components/graph/
├── GraphDashboard.tsx          # Main dashboard container
├── GraphVisualization.tsx      # Core graph rendering component
├── GraphSearchAndFilter.tsx    # Search and filtering controls
├── MetadataDetailPanel.tsx     # Detailed metadata information panel
└── index.ts                    # Export file
```

### Type Definitions
```
src/types/graph.ts              # Graph-specific TypeScript interfaces
```

### Services
```
src/services/graphApiService.ts # API integration for fetching graph data
```

## Usage

### Basic Usage
```tsx
import { GraphDashboard } from './components/graph';

function App() {
  return (
    <div className="app">
      <GraphDashboard />
    </div>
  );
}
```

### Custom Graph Component
```tsx
import { GraphVisualization } from './components/graph';
import { GraphData } from './types/graph';

function CustomGraph({ data }: { data: GraphData }) {
  return (
    <GraphVisualization
      data={data}
      onNodeClick={(node) => console.log('Node clicked:', node)}
      onEdgeClick={(edge) => console.log('Edge clicked:', edge)}
    />
  );
}
```

## Supported Metadata Types

### Node Types
- **Database**: 🗄️ Database instances
- **Schema**: 📋 Database schemas
- **Table**: 📊 Database tables
- **Column**: 📝 Table columns
- **Procedure**: ⚙️ Stored procedures
- **Constraint**: 🔗 Database constraints
- **CSV**: 📄 CSV file schemas
- **XSD**: 📰 XML schema definitions
- **Avro**: 🔧 Avro schema definitions

### Edge Types
- **Contains**: Parent-child containment relationships
- **Relationship**: General relationships between entities
- **Foreign Key**: Foreign key constraints between tables
- **References**: Reference relationships
- **Derived From**: Data derivation relationships

## API Integration

The graph visualization integrates with the Oracle Discovery Service to fetch metadata:

### Endpoints Used
- `POST /api/v1/oracle-discovery/connections/{id}/discover` - Fetch metadata
- `POST /api/v1/oracle-discovery/test-connection` - Test connections
- `GET /api/v1/oracle-discovery/health` - Health check

### Data Transformation
Raw metadata from the backend is automatically transformed into graph-compatible format with nodes and edges representing the relationships between different metadata entities.

## Configuration

### Graph Layout Options
- **Force-directed (COSE)**: Default layout for organic relationship visualization
- **Hierarchical**: Tree-like layout for clear parent-child relationships
- **Grid**: Organized grid layout for systematic exploration
- **Circle**: Circular arrangement for overview visualization

### Styling Customization
The component supports custom styling through CSS variables and theme configuration. Colors, shapes, and sizes are automatically assigned based on metadata types but can be customized.

## Performance Considerations

- **Efficient Rendering**: Cytoscape.js provides hardware-accelerated rendering
- **Data Pagination**: Large datasets are handled through backend pagination
- **Lazy Loading**: Detail panels load information on-demand
- **Responsive Updates**: Real-time filtering without full re-renders

## Future Enhancements

### Planned Features
- **Multi-database Support**: Extend beyond Oracle to PostgreSQL, MySQL, etc.
- **Advanced Analytics**: Graph metrics and relationship analysis
- **Collaborative Features**: Annotations and shared views
- **Export Options**: Export to various formats (GraphML, JSON, etc.)
- **Custom Layouts**: User-defined layout algorithms
- **Time-series Views**: Historical metadata evolution

### Integration Possibilities
- **Data Catalog Integration**: Connect with enterprise data catalogs
- **Impact Analysis**: Understand downstream effects of changes
- **Data Quality Metrics**: Overlay quality scores on the graph
- **Lineage Tracking**: Full end-to-end data lineage visualization

## Troubleshooting

### Common Issues
1. **Graph not loading**: Check backend service connectivity
2. **Performance issues**: Reduce data set size or use filtering
3. **Layout problems**: Try different layout algorithms
4. **Mobile issues**: Ensure responsive mode is enabled

### Debug Mode
Enable debug logging by setting the environment variable:
```bash
REACT_APP_DEBUG_GRAPH=true
```

## Browser Support

- **Chrome**: 90+ (recommended)
- **Firefox**: 88+
- **Safari**: 14+
- **Edge**: 90+

---

For more information, see the [main README](../../../README.md) or contact the development team.