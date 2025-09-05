# DEPRECATED - Data Lineage Discovery Service

⚠️ **This service has been deprecated and its functionality has been merged into `datalineage-oracle-discovery-service`.**

## Migration Information

The Oracle onboarding and connection management functionality that was previously handled by this service has been consolidated into the Oracle Discovery Service to reduce complexity and eliminate unnecessary network calls between services.

### What Changed

- **Oracle onboarding endpoints** are now available at: `http://localhost:8082/api/v1/oracle/`
- **Connection management** is handled directly within the Oracle Discovery Service
- **Metadata discovery** no longer requires inter-service calls
- **Port change**: The consolidated service runs on port `8082` instead of the previous `8083` and `8084`

### New Architecture

```
Before:
datalineage-discovery-service (8083) --> OracleDiscoveryClient --> datalineage-oracle-discovery-service (8084)

After:
datalineage-oracle-discovery-service (8082) - All functionality consolidated
```

### API Mapping

All previous endpoints from `/api/v1/oracle/*` are now available in the Oracle Discovery Service:

- `POST /api/v1/oracle/onboard` - Onboard new Oracle database
- `GET /api/v1/oracle/connections` - List all connections  
- `GET /api/v1/oracle/connections/{id}` - Get connection by ID
- `PUT /api/v1/oracle/connections/{id}` - Update connection
- `DELETE /api/v1/oracle/connections/{id}` - Delete connection
- `POST /api/v1/oracle/connections/{id}/test` - Test connection
- `POST /api/v1/oracle/connections/{id}/discover` - Trigger metadata discovery

### Benefits of Consolidation

- ✅ Reduced maintenance overhead
- ✅ Eliminated unnecessary network calls
- ✅ Simplified architecture
- ✅ Better performance (direct service calls)
- ✅ Easier debugging and monitoring

## For Developers

If you need to work with Oracle database connections, please use the `datalineage-oracle-discovery-service` module instead.

---

*This service will be completely removed in a future release. Please migrate to the consolidated Oracle Discovery Service.*