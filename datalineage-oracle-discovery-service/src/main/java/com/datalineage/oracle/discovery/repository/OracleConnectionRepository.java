package com.datalineage.oracle.discovery.repository;

import com.datalineage.oracle.discovery.entity.OracleConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Oracle connection operations
 */
@Repository
public interface OracleConnectionRepository extends JpaRepository<OracleConnectionEntity, String> {
    
    /**
     * Find connection by name
     */
    Optional<OracleConnectionEntity> findByConnectionName(String connectionName);
    
    /**
     * Check if connection name exists
     */
    boolean existsByConnectionName(String connectionName);
    
    /**
     * Find connections by status
     */
    List<OracleConnectionEntity> findByStatus(String status);
    
    /**
     * Find connections by host and port
     */
    List<OracleConnectionEntity> findByHostAndPort(String host, Integer port);
    
    /**
     * Find connections that need testing (older than specified time)
     */
    @Query("SELECT c FROM OracleConnectionEntity c WHERE c.lastTestedAt IS NULL OR c.lastTestedAt < :threshold")
    List<OracleConnectionEntity> findConnectionsNeedingTest(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Find active connections
     */
    List<OracleConnectionEntity> findByStatusOrderByCreatedAtDesc(String status);
    
    /**
     * Count connections by status
     */
    long countByStatus(String status);
}