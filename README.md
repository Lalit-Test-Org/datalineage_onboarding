# Data Lineage Onboarding - Spring Boot Microservices

This repository contains a Maven parent/child project structure for a Spring Boot microservice architecture focused on data lineage management.

## Project Structure

```
datalineage_onboarding/
├── pom.xml                          # Parent POM
├── datalineage-common/              # Common shared components
│   ├── src/main/java/com/datalineage/common/
│   │   ├── model/                   # Shared entities and models
│   │   ├── dto/                     # Data Transfer Objects
│   │   └── util/                    # Utility classes
│   └── pom.xml
├── datalineage-api-gateway/         # API Gateway service
│   ├── src/main/java/com/datalineage/gateway/
│   ├── src/main/resources/application.yml
│   └── pom.xml
├── datalineage-metadata-service/    # Metadata management service
│   ├── src/main/java/com/datalineage/metadata/
│   ├── src/main/resources/application.yml
│   └── pom.xml
├── datalineage-lineage-service/     # Data lineage tracking service
│   ├── src/main/java/com/datalineage/lineage/
│   ├── src/main/resources/application.yml
│   └── pom.xml
└── datalineage-oracle-discovery-service/   # Oracle metadata discovery & onboarding service
    ├── src/main/java/com/datalineage/oracle/discovery/
    ├── src/main/resources/application.yml
    └── pom.xml
```

> **Note**: The `datalineage-discovery-service` has been **deprecated** and its functionality merged into `datalineage-oracle-discovery-service` for better maintainability and performance.

## Services Overview

### 1. Common Module (`datalineage-common`)
- **Purpose**: Shared components, models, DTOs, and utilities
- **Dependencies**: Spring Boot Starter, Validation, Jackson
- **Key Components**:
  - `BaseEntity`: Base class for all entities with common fields
  - `ApiResponse<T>`: Standard API response wrapper

### 2. API Gateway (`datalineage-api-gateway`)
- **Purpose**: Entry point for all external requests, routing to microservices
- **Port**: 8080
- **Dependencies**: Spring Cloud Gateway, Eureka Client
- **Key Features**:
  - Routes requests to appropriate microservices
  - Load balancing and service discovery
  - Cross-cutting concerns (security, logging, etc.)

### 3. Metadata Service (`datalineage-metadata-service`)
- **Purpose**: Manages data metadata, schemas, and data source information
- **Port**: 8081
- **Dependencies**: Spring Boot Web, JPA, H2 Database, Eureka Client
- **Key Features**:
  - CRUD operations for metadata
  - Schema management
  - Data source registration

### 4. Lineage Service (`datalineage-lineage-service`)
- **Purpose**: Tracks and manages data lineage relationships
- **Port**: 8082
- **Dependencies**: Spring Boot Web, JPA, H2 Database, Eureka Client
- **Key Features**:
  - Data lineage tracking
  - Relationship mapping
  - Impact analysis

### 5. Oracle Discovery Service (`datalineage-oracle-discovery-service`)
- **Purpose**: Comprehensive Oracle database metadata discovery and connection management
- **Port**: 8083
- **Dependencies**: Spring Boot Web, JPA, H2 Database, Oracle JDBC, Eureka Client
- **Key Features**:
  - Oracle database onboarding and connection management
  - Comprehensive metadata extraction (tables, columns, procedures, constraints)
  - Direct and Kerberos authentication support
  - Connection testing and validation
  - Automated metadata discovery
  - Graph-based relationship mapping
  - Encryption for sensitive connection data

> **⚠️ Deprecated Service**: `datalineage-discovery-service` has been merged into this service to eliminate redundancy and improve performance.
- **Dependencies**: Spring Boot Web, JPA, H2 Database, Oracle JDBC Driver, Eureka Client
- **Key Features**:
  - Direct and Kerberos authentication support
  - Extracts tables, columns, procedures, and constraints metadata
  - Queries Oracle system tables (ALL_TABLES, ALL_TAB_COLUMNS, ALL_PROCEDURES, ALL_CONSTRAINTS)
  - Structured entity models for Oracle metadata
  - RESTful API for metadata discovery operations
- **Entity Models**:
  - `OracleTable`: Table metadata with relationships to columns and constraints
  - `OracleColumn`: Column metadata with data types, constraints, and statistics
  - `OracleProcedure`: Stored procedure metadata
  - `OracleConstraint`: Primary key, foreign key, and check constraint metadata
  - `OracleConstraintColumn`: Constraint-column relationship mapping

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.4
- **Spring Cloud**: 2023.0.1
- **Maven**: 3.9.11
- **Database**: H2 (development), can be replaced with PostgreSQL/MySQL for production
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build All Modules
```bash
mvn clean compile
```

### Build and Package
```bash
mvn clean package
```

### Run Individual Services
Each service can be run independently:

```bash
# Start services in this order for proper dependency resolution:

# 1. Start Eureka Server (if using external Eureka)
# 2. Start individual services
cd datalineage-metadata-service && mvn spring-boot:run
cd datalineage-lineage-service && mvn spring-boot:run
cd datalineage-discovery-service && mvn spring-boot:run
cd datalineage-api-gateway && mvn spring-boot:run
```

## Configuration

Each service has its own `application.yml` with:
- Database configuration (H2 for development)
- Eureka client configuration
- Service-specific settings
- Management endpoints for monitoring

## Next Steps

1. **Service Registry**: Set up Eureka Server for service discovery
2. **Configuration Server**: Implement Spring Cloud Config for centralized configuration
3. **Database**: Replace H2 with production databases
4. **Security**: Add Spring Security and OAuth2
5. **Monitoring**: Integrate with monitoring tools (Micrometer, Zipkin, etc.)
6. **Documentation**: Add OpenAPI/Swagger documentation
7. **Testing**: Implement comprehensive unit and integration tests
8. **CI/CD**: Set up build and deployment pipelines