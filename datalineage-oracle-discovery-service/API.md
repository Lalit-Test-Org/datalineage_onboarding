# Oracle Discovery Service API

The Oracle Discovery Service provides RESTful APIs for discovering and extracting metadata from Oracle databases with support for both direct credentials and Kerberos authentication.

## Base URL
```
http://localhost:8084/api/v1/oracle-discovery
```

## Authentication
The service supports two authentication types:
- **DIRECT**: Username/password authentication
- **KERBEROS**: Kerberos authentication with realm and KDC configuration

## API Endpoints

### 1. Health Check
**GET** `/health`

Returns the health status of the service.

**Response:**
```json
{
  "success": true,
  "message": "Service is healthy",
  "data": {
    "status": "UP",
    "service": "oracle-discovery-service",
    "version": "1.0.0"
  }
}
```

### 2. Test Connection
**POST** `/test-connection`

Tests the Oracle database connection with the provided configuration.

**Request Body:**
```json
{
  "connectionId": "oracle-prod-01",
  "host": "oracle.company.com",
  "port": 1521,
  "serviceName": "PROD",
  "username": "metadata_user",
  "password": "secure_password",
  "authenticationType": "DIRECT",
  "connectionTimeout": 30,
  "readTimeout": 60,
  "useSSL": false
}
```

**For Kerberos Authentication:**
```json
{
  "connectionId": "oracle-prod-krb",
  "host": "oracle.company.com",
  "port": 1521,
  "serviceName": "PROD",
  "username": "user@REALM.COM",
  "authenticationType": "KERBEROS",
  "kerberosRealm": "REALM.COM",
  "kerberosKdc": "kdc.company.com",
  "kerberosPrincipal": "user@REALM.COM"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Connection test successful",
  "data": {
    "connectionValid": true,
    "connectionId": "oracle-prod-01",
    "host": "oracle.company.com",
    "port": 1521,
    "serviceName": "PROD",
    "authenticationType": "DIRECT"
  }
}
```

### 3. Discover Metadata
**POST** `/discover`

Discovers Oracle metadata based on the connection configuration and discovery parameters.

**Request Body:**
```json
{
  "connectionConfig": {
    "connectionId": "oracle-prod-01",
    "host": "oracle.company.com",
    "port": 1521,
    "serviceName": "PROD",
    "username": "metadata_user",
    "password": "secure_password",
    "authenticationType": "DIRECT"
  },
  "discoveryRequest": {
    "connectionId": "oracle-prod-01",
    "schemas": ["HR", "SALES"],
    "tablePatterns": ["EMP%", "DEPT%"],
    "includeTables": true,
    "includeColumns": true,
    "includeProcedures": true,
    "includeConstraints": true,
    "limit": 1000,
    "offset": 0
  }
}
```

### 4. Discover Metadata by Connection ID
**POST** `/connections/{connectionId}/discover`

Alternative endpoint that accepts connection configuration as request body and discovery parameters as query parameters.

**Path Parameters:**
- `connectionId`: Unique identifier for the connection

**Query Parameters:**
- `schemas`: Comma-separated list of schema names to include
- `tablePatterns`: Comma-separated list of table name patterns (SQL LIKE patterns)
- `tableTypes`: Comma-separated list of table types
- `includeTables`: Include table metadata (default: true)
- `includeColumns`: Include column metadata (default: true)
- `includeProcedures`: Include procedure metadata (default: true)
- `includeConstraints`: Include constraint metadata (default: true)
- `limit`: Maximum number of results (default: 1000)
- `offset`: Number of results to skip (default: 0)

**Example:**
```
POST /connections/oracle-prod-01/discover?schemas=HR,SALES&tablePatterns=EMP%,DEPT%&limit=500
```

**Response:**
```json
{
  "success": true,
  "message": "Metadata discovery completed successfully",
  "data": {
    "connectionId": "oracle-prod-01",
    "tables": [
      {
        "id": "uuid-generated",
        "owner": "HR",
        "tableName": "EMPLOYEES",
        "tableType": "TABLE",
        "tablespace": "USERS",
        "numRows": 107,
        "status": "VALID",
        "comments": "Employee information table",
        "oracleConnectionId": "oracle-prod-01"
      }
    ],
    "columns": [
      {
        "id": "uuid-generated",
        "owner": "HR",
        "tableName": "EMPLOYEES",
        "columnName": "EMPLOYEE_ID",
        "dataType": "NUMBER",
        "dataLength": 22,
        "dataPrecision": 6,
        "nullable": "N",
        "columnId": 1,
        "oracleConnectionId": "oracle-prod-01"
      }
    ],
    "procedures": [
      {
        "id": "uuid-generated",
        "owner": "HR",
        "objectName": "GET_EMPLOYEE",
        "objectType": "PROCEDURE",
        "status": "VALID",
        "oracleConnectionId": "oracle-prod-01"
      }
    ],
    "constraints": [
      {
        "id": "uuid-generated",
        "owner": "HR",
        "constraintName": "EMP_EMP_ID_PK",
        "constraintType": "P",
        "tableName": "EMPLOYEES",
        "status": "ENABLED",
        "oracleConnectionId": "oracle-prod-01"
      }
    ],
    "statistics": {
      "totalTables": 1,
      "totalColumns": 11,
      "totalProcedures": 1,
      "totalConstraints": 1,
      "discoveryTimeMs": 1234
    }
  }
}
```

## Entity Structure

### OracleTable
Represents Oracle table metadata with properties like owner, table name, type, tablespace, row count, and relationships to columns and constraints.

### OracleColumn
Contains detailed column information including data type, precision, scale, nullability, default values, and statistical information.

### OracleProcedure
Stores procedure metadata including name, type, status, and various Oracle-specific properties.

### OracleConstraint
Represents constraints (Primary Key, Foreign Key, Check, Unique) with relationships to the columns they affect.

### OracleConstraintColumn
Maps constraints to their constituent columns with position information.

## Error Handling

All endpoints return standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "errorCode": "ERROR_CODE",
  "data": null
}
```

Common error codes:
- `CONNECTION_FAILED`: Database connection failed
- `CONNECTION_ERROR`: Connection configuration error
- `SQL_ERROR`: SQL execution error
- `DISCOVERY_ERROR`: General discovery error
- `GRAPH_ERROR`: Graph transformation error
- `ID_MISMATCH`: Connection ID mismatch
- `VALIDATION_ERROR`: Request validation failed

## Graph API Endpoints

### 5. Get Schema Graph
**POST** `/graph/schema/{connectionId}`

Returns metadata transformed into graph format for visualization, including all schema objects.

**Path Parameters:**
- `connectionId`: Unique identifier for the connection

**Query Parameters:**
- `schemas`: Comma-separated list of schema names to include
- `tablePatterns`: Comma-separated list of table name patterns
- `tableTypes`: Comma-separated list of table types
- `includeTables`: Include table metadata (default: true)
- `includeColumns`: Include column metadata (default: true)
- `includeProcedures`: Include procedure metadata (default: true)
- `includeConstraints`: Include constraint metadata (default: true)
- `limit`: Maximum number of results (default: 1000)
- `offset`: Number of results to skip (default: 0)

**Request Body:**
```json
{
  "connectionId": "oracle-prod-01",
  "host": "oracle.company.com",
  "port": 1521,
  "serviceName": "PROD",
  "username": "metadata_user",
  "password": "secure_password",
  "authenticationType": "DIRECT"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Schema graph generated successfully",
  "data": {
    "nodes": [
      {
        "id": "schema-oracle-prod-01",
        "label": "Schema (oracle-prod-01)",
        "type": "schema",
        "metadata": {
          "connectionId": "oracle-prod-01",
          "type": "Oracle Schema"
        }
      },
      {
        "id": "table-123",
        "label": "HR.EMPLOYEES",
        "type": "table",
        "metadata": {
          "owner": "HR",
          "tableName": "EMPLOYEES",
          "tableType": "TABLE",
          "fullName": "HR.EMPLOYEES",
          "type": "Oracle Table"
        }
      }
    ],
    "edges": [
      {
        "id": "schema-table-123",
        "source": "schema-oracle-prod-01",
        "target": "table-123",
        "type": "contains",
        "metadata": {
          "relationship": "schema contains table"
        }
      }
    ],
    "statistics": {
      "totalNodes": 15,
      "totalEdges": 18,
      "nodeTypeBreakdown": {
        "schema": 1,
        "table": 5,
        "column": 8,
        "constraint": 1
      },
      "edgeTypeBreakdown": {
        "contains": 13,
        "relationship": 4,
        "foreign_key": 1
      }
    }
  }
}
```

### 6. Get Table Graph
**POST** `/graph/table/{connectionId}/{tableName}`

Returns graph data focused on a specific table and its related objects.

**Path Parameters:**
- `connectionId`: Unique identifier for the connection
- `tableName`: Name of the table to focus on

**Query Parameters:**
- `owner`: Schema owner of the table (optional)
- `includeColumns`: Include column metadata (default: true)
- `includeConstraints`: Include constraint metadata (default: true)

**Request Body:**
```json
{
  "connectionId": "oracle-prod-01",
  "host": "oracle.company.com",
  "port": 1521,
  "serviceName": "PROD",
  "username": "metadata_user",
  "password": "secure_password",
  "authenticationType": "DIRECT"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Table graph generated successfully",
  "data": {
    "nodes": [...],
    "edges": [...],
    "statistics": {...}
  }
}
```

### 7. Discover Graph Data
**POST** `/graph/connections/{connectionId}/discover`

Discovers and transforms metadata to graph format in a single call.

**Path Parameters:**
- `connectionId`: Unique identifier for the connection

**Request Body:**
```json
{
  "connectionConfig": {
    "connectionId": "oracle-prod-01",
    "host": "oracle.company.com",
    "port": 1521,
    "serviceName": "PROD",
    "username": "metadata_user",
    "password": "secure_password",
    "authenticationType": "DIRECT"
  },
  "discoveryRequest": {
    "connectionId": "oracle-prod-01",
    "schemas": ["HR"],
    "includeTables": true,
    "includeColumns": true,
    "includeProcedures": true,
    "includeConstraints": true,
    "limit": 1000,
    "offset": 0
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Graph discovery completed successfully",
  "data": {
    "nodes": [...],
    "edges": [...],
    "statistics": {...}
  }
}
```

### 8. Get Graph Metadata
**POST** `/graph/metadata/{connectionId}`

Returns graph statistics and metadata breakdown without the full graph data.

**Path Parameters:**
- `connectionId`: Unique identifier for the connection

**Query Parameters:**
- `schemas`: Comma-separated list of schema names to include
- `tablePatterns`: Comma-separated list of table name patterns
- `tableTypes`: Comma-separated list of table types

**Request Body:**
```json
{
  "connectionId": "oracle-prod-01",
  "host": "oracle.company.com",
  "port": 1521,
  "serviceName": "PROD",
  "username": "metadata_user",
  "password": "secure_password",
  "authenticationType": "DIRECT"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Graph metadata retrieved successfully",
  "data": {
    "totalNodes": 25,
    "totalEdges": 30,
    "nodeTypeBreakdown": {
      "schema": 1,
      "table": 8,
      "column": 15,
      "constraint": 1
    },
    "edgeTypeBreakdown": {
      "contains": 24,
      "relationship": 5,
      "foreign_key": 1
    }
  }
}
```

## Graph Data Structure

### Node Types
- **schema**: Database schema/connection
- **table**: Database table
- **column**: Table column
- **procedure**: Stored procedure
- **constraint**: Database constraint

### Edge Types
- **contains**: Parent-child containment relationships
- **relationship**: General relationships between entities
- **foreign_key**: Foreign key constraints between tables
- **references**: Reference relationships
- **derived_from**: Data derivation relationships