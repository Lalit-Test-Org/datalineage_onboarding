# Oracle Database Onboarding API Documentation

This document describes the REST API endpoints for Oracle database onboarding in the Data Lineage Discovery Service.

## Base URL
```
http://localhost:8083/api/v1/oracle
```

## Endpoints

### 1. Onboard Oracle Database
**POST** `/onboard`

Onboards a new Oracle database connection with support for both direct credential and Kerberos authentication.

#### Request Body (Direct Authentication)
```json
{
  "connectionName": "Production Oracle DB",
  "description": "Main production Oracle database",
  "host": "oracle-prod.company.com",
  "port": 1521,
  "serviceName": "ORCL",
  "authenticationType": "DIRECT",
  "username": "dbuser",
  "password": "securePassword123",
  "connectionTimeout": 30000,
  "readTimeout": 60000,
  "useSSL": false,
  "autoDiscoverMetadata": true
}
```

#### Request Body (Kerberos Authentication)
```json
{
  "connectionName": "Secure Oracle DB",
  "description": "Kerberos-secured Oracle database",
  "host": "oracle-secure.company.com",
  "port": 1521,
  "serviceName": "SECURE",
  "authenticationType": "KERBEROS",
  "kerberosRealm": "COMPANY.COM",
  "kerberosKdc": "kdc.company.com",
  "kerberosPrincipal": "datalineage@COMPANY.COM",
  "kerberosKeytabPath": "/path/to/keytab/file",
  "connectionTimeout": 30000,
  "readTimeout": 60000,
  "useSSL": true,
  "sslTruststore": "/path/to/truststore.jks",
  "sslTruststorePassword": "truststorePassword",
  "autoDiscoverMetadata": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Oracle database onboarded successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "connectionName": "Production Oracle DB",
    "description": "Main production Oracle database",
    "host": "oracle-prod.company.com",
    "port": 1521,
    "serviceName": "ORCL",
    "authenticationType": "DIRECT",
    "username": "dbuser",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "lastTestedAt": "2024-01-15T10:30:00",
    "lastTestResult": "SUCCESS"
  }
}
```

### 2. List Oracle Connections
**GET** `/connections`

Returns a list of all active Oracle database connections.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Oracle connections retrieved successfully",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "connectionName": "Production Oracle DB",
      "description": "Main production Oracle database",
      "host": "oracle-prod.company.com",
      "port": 1521,
      "serviceName": "ORCL",
      "authenticationType": "DIRECT",
      "username": "dbuser",
      "status": "ACTIVE",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00",
      "lastTestedAt": "2024-01-15T10:30:00",
      "lastTestResult": "SUCCESS"
    }
  ]
}
```

### 3. Get Oracle Connection
**GET** `/connections/{id}`

Returns details of a specific Oracle database connection.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Oracle connection retrieved successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "connectionName": "Production Oracle DB",
    "description": "Main production Oracle database",
    "host": "oracle-prod.company.com",
    "port": 1521,
    "serviceName": "ORCL",
    "authenticationType": "DIRECT",
    "username": "dbuser",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "lastTestedAt": "2024-01-15T10:30:00",
    "lastTestResult": "SUCCESS"
  }
}
```

### 4. Update Oracle Connection
**PUT** `/connections/{id}`

Updates an existing Oracle database connection.

#### Request Body
Same format as onboarding request.

#### Response (200 OK)
Same format as get connection response.

### 5. Delete Oracle Connection
**DELETE** `/connections/{id}`

Deletes an Oracle database connection.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Oracle connection deleted successfully",
  "data": null
}
```

### 6. Test Connection
**POST** `/connections/{id}/test`

Tests the connectivity of an Oracle database connection.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Connection test successful",
  "data": {
    "connectionValid": true,
    "connectionId": "550e8400-e29b-41d4-a716-446655440000",
    "testedAt": "2024-01-15T10:30:00"
  }
}
```

### 7. Trigger Metadata Discovery
**POST** `/connections/{id}/discover`

Triggers metadata discovery for an Oracle database connection.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Metadata discovery triggered successfully",
  "data": {
    "success": true,
    "connectionId": "550e8400-e29b-41d4-a716-446655440000",
    "discoveredAt": "2024-01-15T10:30:00",
    "metadata": {
      "tables": [...],
      "columns": [...],
      "procedures": [...],
      "constraints": [...]
    }
  }
}
```

### 8. Health Check
**GET** `/health`

Returns the health status of the Oracle onboarding service.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Service is healthy",
  "data": {
    "status": "UP",
    "service": "oracle-onboarding-service",
    "version": "1.0.0"
  }
}
```

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid request: Connection name is required",
  "errorCode": "INVALID_REQUEST"
}
```

### Connection Not Found (404 Not Found)
```json
{
  "success": false,
  "message": "Oracle connection not found",
  "errorCode": "CONNECTION_NOT_FOUND"
}
```

### Connection Test Failed (400 Bad Request)
```json
{
  "success": false,
  "message": "Connection test failed. Please verify your connection parameters.",
  "errorCode": "ONBOARDING_FAILED"
}
```

### Internal Server Error (500 Internal Server Error)
```json
{
  "success": false,
  "message": "Internal server error during onboarding",
  "errorCode": "INTERNAL_ERROR"
}
```

## Security Features

- **Encryption**: All sensitive data (passwords, Kerberos keytabs, SSL passwords) are encrypted using AES encryption before storage
- **No Sensitive Data in Responses**: Passwords and other sensitive information are never returned in API responses
- **Input Validation**: All requests are validated using Jakarta Bean Validation
- **Audit Logging**: All connection operations are logged for audit purposes
- **Connection Testing**: Connections are tested before storage to ensure validity

## Authentication Types

### Direct Authentication
Required fields:
- `username`: Database username
- `password`: Database password

### Kerberos Authentication
Required fields:
- `kerberosRealm`: Kerberos realm (e.g., COMPANY.COM)
- `kerberosKdc`: Kerberos Key Distribution Center address
- `kerberosPrincipal`: Service principal name (optional)
- `kerberosKeytabPath`: Path to keytab file (optional)

## Optional Configuration

- `connectionTimeout`: Connection timeout in milliseconds (default: 30000)
- `readTimeout`: Read timeout in milliseconds (default: 60000)
- `useSSL`: Enable SSL connection (default: false)
- `sslTruststore`: Path to SSL truststore (when SSL is enabled)
- `sslTruststorePassword`: SSL truststore password (when SSL is enabled)
- `autoDiscoverMetadata`: Automatically trigger metadata discovery after onboarding (default: true)