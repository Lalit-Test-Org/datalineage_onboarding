package com.datalineage.oracle.discovery.service;

import com.datalineage.oracle.discovery.dto.MetadataDiscoveryRequest;
import com.datalineage.oracle.discovery.dto.MetadataDiscoveryResponse;
import com.datalineage.oracle.discovery.dto.OracleConnectionConfig;
import com.datalineage.oracle.discovery.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for extracting metadata from Oracle system tables
 */
@Service
public class OracleMetadataExtractionService {
    
    @Autowired
    private OracleConnectionService connectionService;
    
    /**
     * Discovers Oracle metadata based on request parameters
     */
    public MetadataDiscoveryResponse discoverMetadata(OracleConnectionConfig config, 
                                                     MetadataDiscoveryRequest request) throws SQLException {
        long startTime = System.currentTimeMillis();
        
        MetadataDiscoveryResponse response = new MetadataDiscoveryResponse(request.getConnectionId());
        
        try (Connection connection = connectionService.createConnection(config)) {
            if (request.isIncludeTables()) {
                response.setTables(extractTables(connection, request));
            }
            
            if (request.isIncludeColumns()) {
                response.setColumns(extractColumns(connection, request));
            }
            
            if (request.isIncludeProcedures()) {
                response.setProcedures(extractProcedures(connection, request));
            }
            
            if (request.isIncludeConstraints()) {
                response.setConstraints(extractConstraints(connection, request));
            }
            
            long endTime = System.currentTimeMillis();
            
            // Build statistics
            MetadataDiscoveryResponse.DiscoveryStatistics stats = new MetadataDiscoveryResponse.DiscoveryStatistics(
                response.getTables() != null ? response.getTables().size() : 0,
                response.getColumns() != null ? response.getColumns().size() : 0,
                response.getProcedures() != null ? response.getProcedures().size() : 0,
                response.getConstraints() != null ? response.getConstraints().size() : 0,
                endTime - startTime
            );
            response.setStatistics(stats);
        }
        
        return response;
    }
    
    /**
     * Extracts table metadata from Oracle system tables
     */
    private List<OracleTable> extractTables(Connection connection, MetadataDiscoveryRequest request) throws SQLException {
        StringBuilder query = new StringBuilder(
            "SELECT owner, table_name, table_type, tablespace_name, num_rows, blocks, " +
            "avg_row_len, sample_size, compression, status, temporary " +
            "FROM all_tables " +
            "WHERE 1=1 "
        );
        
        List<Object> params = new ArrayList<>();
        
        // Add schema filter
        if (request.getSchemas() != null && !request.getSchemas().isEmpty()) {
            query.append("AND owner IN (");
            for (int i = 0; i < request.getSchemas().size(); i++) {
                if (i > 0) query.append(",");
                query.append("?");
                params.add(request.getSchemas().get(i));
            }
            query.append(") ");
        }
        
        // Add table name patterns
        if (request.getTablePatterns() != null && !request.getTablePatterns().isEmpty()) {
            query.append("AND (");
            for (int i = 0; i < request.getTablePatterns().size(); i++) {
                if (i > 0) query.append(" OR ");
                query.append("table_name LIKE ?");
                params.add(request.getTablePatterns().get(i));
            }
            query.append(") ");
        }
        
        query.append("ORDER BY owner, table_name ");
        
        if (request.getLimit() != null) {
            query.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(request.getOffset() != null ? request.getOffset() : 0);
            params.add(request.getLimit());
        }
        
        List<OracleTable> tables = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OracleTable table = new OracleTable(
                        rs.getString("owner"),
                        rs.getString("table_name"),
                        request.getConnectionId()
                    );
                    
                    table.setTableType(rs.getString("table_type"));
                    table.setTablespaceName(rs.getString("tablespace_name"));
                    table.setNumRows(rs.getLong("num_rows"));
                    table.setBlocks(rs.getLong("blocks"));
                    table.setAvgRowLen(rs.getLong("avg_row_len"));
                    table.setSampleSize(rs.getLong("sample_size"));
                    table.setCompression(rs.getString("compression"));
                    table.setStatus(rs.getString("status"));
                    table.setTemporary(rs.getString("temporary"));
                    
                    tables.add(table);
                }
            }
        }
        
        // Get table comments
        addTableComments(connection, tables);
        
        return tables;
    }
    
    /**
     * Extracts column metadata from Oracle system tables
     */
    private List<OracleColumn> extractColumns(Connection connection, MetadataDiscoveryRequest request) throws SQLException {
        StringBuilder query = new StringBuilder(
            "SELECT owner, table_name, column_name, data_type, data_type_mod, data_type_owner, " +
            "data_length, data_precision, data_scale, nullable, column_id, default_length, " +
            "data_default, num_distinct, low_value, high_value, density, num_nulls, num_buckets, " +
            "character_set_name, char_col_decl_length, global_stats, user_stats, avg_col_len, " +
            "char_length, char_used " +
            "FROM all_tab_columns " +
            "WHERE 1=1 "
        );
        
        List<Object> params = new ArrayList<>();
        
        // Add schema filter
        if (request.getSchemas() != null && !request.getSchemas().isEmpty()) {
            query.append("AND owner IN (");
            for (int i = 0; i < request.getSchemas().size(); i++) {
                if (i > 0) query.append(",");
                query.append("?");
                params.add(request.getSchemas().get(i));
            }
            query.append(") ");
        }
        
        // Add table name patterns
        if (request.getTablePatterns() != null && !request.getTablePatterns().isEmpty()) {
            query.append("AND (");
            for (int i = 0; i < request.getTablePatterns().size(); i++) {
                if (i > 0) query.append(" OR ");
                query.append("table_name LIKE ?");
                params.add(request.getTablePatterns().get(i));
            }
            query.append(") ");
        }
        
        query.append("ORDER BY owner, table_name, column_id ");
        
        if (request.getLimit() != null) {
            query.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(request.getOffset() != null ? request.getOffset() : 0);
            params.add(request.getLimit());
        }
        
        List<OracleColumn> columns = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OracleColumn column = new OracleColumn(
                        rs.getString("owner"),
                        rs.getString("table_name"),
                        rs.getString("column_name"),
                        request.getConnectionId()
                    );
                    
                    column.setDataType(rs.getString("data_type"));
                    column.setDataTypeMod(rs.getString("data_type_mod"));
                    column.setDataTypeOwner(rs.getString("data_type_owner"));
                    column.setDataLength(rs.getLong("data_length"));
                    column.setDataPrecision(getIntegerOrNull(rs, "data_precision"));
                    column.setDataScale(getIntegerOrNull(rs, "data_scale"));
                    column.setNullable(rs.getString("nullable"));
                    column.setColumnId(getIntegerOrNull(rs, "column_id"));
                    column.setDefaultLength(rs.getLong("default_length"));
                    column.setDataDefault(rs.getString("data_default"));
                    column.setNumDistinct(rs.getLong("num_distinct"));
                    column.setLowValue(rs.getString("low_value"));
                    column.setHighValue(rs.getString("high_value"));
                    column.setDensity(getDoubleOrNull(rs, "density"));
                    column.setNumNulls(rs.getLong("num_nulls"));
                    column.setNumBuckets(getIntegerOrNull(rs, "num_buckets"));
                    column.setCharacterSetName(rs.getString("character_set_name"));
                    column.setCharColDeclLength(getIntegerOrNull(rs, "char_col_decl_length"));
                    column.setGlobalStats(rs.getString("global_stats"));
                    column.setUserStats(rs.getString("user_stats"));
                    column.setAvgColLen(getDoubleOrNull(rs, "avg_col_len"));
                    column.setCharLength(getIntegerOrNull(rs, "char_length"));
                    column.setCharUsed(rs.getString("char_used"));
                    
                    columns.add(column);
                }
            }
        }
        
        // Get column comments
        addColumnComments(connection, columns);
        
        return columns;
    }
    
    /**
     * Extracts procedure metadata from Oracle system tables
     */
    private List<OracleProcedure> extractProcedures(Connection connection, MetadataDiscoveryRequest request) throws SQLException {
        StringBuilder query = new StringBuilder(
            "SELECT owner, object_name, procedure_name, object_type, status, aggregate, " +
            "pipelined, impltypeowner, impltypename, parallel, interface, deterministic, " +
            "authid, result_cache, origin_con_id, polymorphic " +
            "FROM all_procedures " +
            "WHERE 1=1 "
        );
        
        List<Object> params = new ArrayList<>();
        
        // Add schema filter
        if (request.getSchemas() != null && !request.getSchemas().isEmpty()) {
            query.append("AND owner IN (");
            for (int i = 0; i < request.getSchemas().size(); i++) {
                if (i > 0) query.append(",");
                query.append("?");
                params.add(request.getSchemas().get(i));
            }
            query.append(") ");
        }
        
        query.append("ORDER BY owner, object_name, procedure_name ");
        
        if (request.getLimit() != null) {
            query.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(request.getOffset() != null ? request.getOffset() : 0);
            params.add(request.getLimit());
        }
        
        List<OracleProcedure> procedures = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OracleProcedure procedure = new OracleProcedure(
                        rs.getString("owner"),
                        rs.getString("object_name"),
                        request.getConnectionId()
                    );
                    
                    procedure.setProcedureName(rs.getString("procedure_name"));
                    procedure.setObjectType(rs.getString("object_type"));
                    procedure.setStatus(rs.getString("status"));
                    procedure.setAggregate(rs.getString("aggregate"));
                    procedure.setPipelined(rs.getString("pipelined"));
                    procedure.setImplTypeOwner(rs.getString("impltypeowner"));
                    procedure.setImplTypeName(rs.getString("impltypename"));
                    procedure.setParallel(rs.getString("parallel"));
                    procedure.setInterfaceType(rs.getString("interface"));
                    procedure.setDeterministic(rs.getString("deterministic"));
                    procedure.setAuthId(rs.getString("authid"));
                    procedure.setResultCache(rs.getString("result_cache"));
                    procedure.setOriginConId(getIntegerOrNull(rs, "origin_con_id"));
                    procedure.setPolymorphic(rs.getString("polymorphic"));
                    
                    procedures.add(procedure);
                }
            }
        }
        
        return procedures;
    }
    
    /**
     * Extracts constraint metadata from Oracle system tables
     */
    private List<OracleConstraint> extractConstraints(Connection connection, MetadataDiscoveryRequest request) throws SQLException {
        StringBuilder query = new StringBuilder(
            "SELECT owner, constraint_name, constraint_type, table_name, search_condition, " +
            "search_condition_vc, r_owner, r_constraint_name, delete_rule, status, deferrable, " +
            "deferred, validated, generated, bad, rely, last_change, index_owner, index_name, " +
            "invalid, view_related, origin_con_id " +
            "FROM all_constraints " +
            "WHERE constraint_type IN ('P', 'R', 'U', 'C') "
        );
        
        List<Object> params = new ArrayList<>();
        
        // Add schema filter
        if (request.getSchemas() != null && !request.getSchemas().isEmpty()) {
            query.append("AND owner IN (");
            for (int i = 0; i < request.getSchemas().size(); i++) {
                if (i > 0) query.append(",");
                query.append("?");
                params.add(request.getSchemas().get(i));
            }
            query.append(") ");
        }
        
        // Add table name patterns
        if (request.getTablePatterns() != null && !request.getTablePatterns().isEmpty()) {
            query.append("AND (");
            for (int i = 0; i < request.getTablePatterns().size(); i++) {
                if (i > 0) query.append(" OR ");
                query.append("table_name LIKE ?");
                params.add(request.getTablePatterns().get(i));
            }
            query.append(") ");
        }
        
        query.append("ORDER BY owner, table_name, constraint_name ");
        
        if (request.getLimit() != null) {
            query.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(request.getOffset() != null ? request.getOffset() : 0);
            params.add(request.getLimit());
        }
        
        List<OracleConstraint> constraints = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OracleConstraint constraint = new OracleConstraint(
                        rs.getString("owner"),
                        rs.getString("constraint_name"),
                        rs.getString("table_name"),
                        request.getConnectionId()
                    );
                    
                    constraint.setConstraintType(rs.getString("constraint_type"));
                    constraint.setSearchCondition(rs.getString("search_condition"));
                    constraint.setSearchConditionVc(rs.getString("search_condition_vc"));
                    constraint.setrOwner(rs.getString("r_owner"));
                    constraint.setrConstraintName(rs.getString("r_constraint_name"));
                    constraint.setDeleteRule(rs.getString("delete_rule"));
                    constraint.setStatus(rs.getString("status"));
                    constraint.setDeferrable(rs.getString("deferrable"));
                    constraint.setDeferred(rs.getString("deferred"));
                    constraint.setValidated(rs.getString("validated"));
                    constraint.setGenerated(rs.getString("generated"));
                    constraint.setBad(rs.getString("bad"));
                    constraint.setRely(rs.getString("rely"));
                    constraint.setIndexOwner(rs.getString("index_owner"));
                    constraint.setIndexName(rs.getString("index_name"));
                    constraint.setInvalid(rs.getString("invalid"));
                    constraint.setViewRelated(rs.getString("view_related"));
                    constraint.setOriginConId(getIntegerOrNull(rs, "origin_con_id"));
                    
                    constraints.add(constraint);
                }
            }
        }
        
        return constraints;
    }
    
    /**
     * Adds table comments to the tables
     */
    private void addTableComments(Connection connection, List<OracleTable> tables) throws SQLException {
        if (tables.isEmpty()) return;
        
        String query = "SELECT owner, table_name, comments FROM all_tab_comments WHERE comments IS NOT NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String owner = rs.getString("owner");
                String tableName = rs.getString("table_name");
                String comments = rs.getString("comments");
                
                tables.stream()
                    .filter(t -> t.getOwner().equals(owner) && t.getTableName().equals(tableName))
                    .forEach(t -> t.setComments(comments));
            }
        }
    }
    
    /**
     * Adds column comments to the columns
     */
    private void addColumnComments(Connection connection, List<OracleColumn> columns) throws SQLException {
        if (columns.isEmpty()) return;
        
        String query = "SELECT owner, table_name, column_name, comments FROM all_col_comments WHERE comments IS NOT NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String owner = rs.getString("owner");
                String tableName = rs.getString("table_name");
                String columnName = rs.getString("column_name");
                String comments = rs.getString("comments");
                
                columns.stream()
                    .filter(c -> c.getOwner().equals(owner) && 
                               c.getTableName().equals(tableName) && 
                               c.getColumnName().equals(columnName))
                    .forEach(c -> c.setComments(comments));
            }
        }
    }
    
    /**
     * Helper method to get Integer or null from ResultSet
     */
    private Integer getIntegerOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Helper method to get Double or null from ResultSet
     */
    private Double getDoubleOrNull(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : value;
    }
}