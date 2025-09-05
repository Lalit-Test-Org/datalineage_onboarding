# Kafka Integration Service API

This document provides an overview of the Kafka Integration Service REST API endpoints.

## Base URL
```
http://localhost:8084/api/v1/kafka
```

## Endpoints

### Health Check
**GET** `/health`

Returns the health status of the service.

**Response:**
```json
{
  "success": true,
  "message": "Success", 
  "data": "Kafka Integration Service is running",
  "errorCode": null
}
```

### Connection Management

#### Test Kafka Connection
**POST** `/test-connection`

Tests connectivity to a Kafka cluster.

**Request Body:**
```json
{
  "connectionId": "my-kafka-cluster",
  "bootstrapServers": "localhost:9092",
  "securityProtocol": "PLAINTEXT",
  "saslMechanism": "PLAIN",
  "saslUsername": "user",
  "saslPassword": "password",
  "schemaRegistryUrl": "http://localhost:8081",
  "connectionTimeout": 30,
  "sessionTimeout": 10,
  "requestTimeout": 120
}
```

**Response:**
```json
{
  "success": true,
  "message": "Connection successful",
  "data": true,
  "errorCode": null
}
```

#### Save Kafka Connection
**POST** `/connections`

Saves a new Kafka connection configuration.

**Request Body:** Same as test connection

**Response:**
```json
{
  "success": true,
  "message": "Connection saved successfully",
  "data": {
    "id": "uuid-string",
    "connectionId": "my-kafka-cluster",
    "bootstrapServers": "localhost:9092",
    "securityProtocol": "PLAINTEXT",
    "connectionStatus": "CONNECTED",
    "lastTested": "2025-09-05T19:44:17",
    "isActive": true
  },
  "errorCode": null
}
```

#### Get All Connections
**GET** `/connections`

Retrieves all active Kafka connections.

**Response:**
```json
{
  "success": true,
  "message": "Connections retrieved successfully",
  "data": [
    {
      "id": "uuid-string",
      "connectionId": "my-kafka-cluster",
      "bootstrapServers": "localhost:9092",
      "securityProtocol": "PLAINTEXT",
      "connectionStatus": "CONNECTED",
      "lastTested": "2025-09-05T19:44:17",
      "isActive": true
    }
  ],
  "errorCode": null
}
```

#### Get Connection by ID
**GET** `/connections/{connectionId}`

Retrieves a specific connection by its connection ID.

#### Delete Connection
**DELETE** `/connections/{connectionId}`

Deletes a connection and all associated topics.

### Topic Discovery

#### Discover Topics
**POST** `/connections/{connectionId}/discover-topics`

Discovers and saves all topics for the specified connection.

**Response:**
```json
{
  "success": true,
  "message": "Topics discovered successfully",
  "data": [
    {
      "id": "uuid-string",
      "topicName": "user-events",
      "partitions": 3,
      "replicationFactor": 2,
      "cleanupPolicy": "delete",
      "retentionMs": 604800000,
      "kafkaConnectionId": "connection-uuid"
    }
  ],
  "errorCode": null
}
```

### Schema Registry

#### Test Schema Registry Connection
**POST** `/test-schema-registry`

Tests connectivity to a Schema Registry.

**Request Body:**
```json
{
  "schemaRegistryUrl": "http://localhost:8081",
  "username": "user",
  "password": "password"
}
```

#### Get Schema Subjects
**POST** `/schema-registry/subjects`

Retrieves all subjects from Schema Registry.

**Request Body:** Same as test Schema Registry

**Response:**
```json
{
  "success": true,
  "message": "Subjects retrieved successfully",
  "data": [
    "user-events-value",
    "user-events-key",
    "order-events-value"
  ],
  "errorCode": null
}
```

## Features

- **Connection Management**: Store and manage multiple Kafka cluster connections
- **Security**: Support for PLAINTEXT, SASL/SSL authentication
- **Topic Discovery**: Automatic discovery of Kafka topics and their metadata
- **Schema Registry Integration**: Connect to Confluent-compatible Schema Registry
- **Encrypted Storage**: Sensitive credentials are encrypted before storage
- **Health Monitoring**: Built-in health checks and connection testing

## Configuration

The service is configured via `application.yml`:

```yaml
server:
  port: 8084

kafka:
  integration:
    default-timeout: 30
    max-connections: 10
    connection-pool-size: 5

schema-registry:
  default-timeout: 30
  cache-size: 1000
```

## Database

The service uses H2 in-memory database for development. Tables created:
- `kafka_connections` - Kafka cluster connections
- `kafka_topics` - Discovered Kafka topics
- `avro_schemas` - Avro schema metadata
- `avro_fields` - Avro field definitions