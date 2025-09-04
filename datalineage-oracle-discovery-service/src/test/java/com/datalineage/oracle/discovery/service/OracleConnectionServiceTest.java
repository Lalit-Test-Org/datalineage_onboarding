package com.datalineage.oracle.discovery.service;

import com.datalineage.oracle.discovery.dto.OracleConnectionConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OracleConnectionService
 */
@ExtendWith(MockitoExtension.class)
class OracleConnectionServiceTest {
    
    @InjectMocks
    private OracleConnectionService connectionService;
    
    @Test
    void testDirectConnectionConfiguration() {
        OracleConnectionConfig config = new OracleConnectionConfig(
            "test-conn-1",
            "localhost",
            1521,
            "XE",
            "testuser",
            "testpass",
            OracleConnectionConfig.AuthenticationType.DIRECT
        );
        
        assertNotNull(config);
        assertEquals("test-conn-1", config.getConnectionId());
        assertEquals("localhost", config.getHost());
        assertEquals(1521, config.getPort());
        assertEquals("XE", config.getServiceName());
        assertEquals("testuser", config.getUsername());
        assertEquals("testpass", config.getPassword());
        assertEquals(OracleConnectionConfig.AuthenticationType.DIRECT, config.getAuthenticationType());
    }
    
    @Test
    void testKerberosConnectionConfiguration() {
        OracleConnectionConfig config = new OracleConnectionConfig();
        config.setConnectionId("test-conn-2");
        config.setHost("oracleserver.company.com");
        config.setPort(1521);
        config.setServiceName("PROD");
        config.setUsername("user@REALM.COM");
        config.setAuthenticationType(OracleConnectionConfig.AuthenticationType.KERBEROS);
        config.setKerberosRealm("REALM.COM");
        config.setKerberosKdc("kdc.company.com");
        config.setKerberosPrincipal("user@REALM.COM");
        
        assertNotNull(config);
        assertEquals("test-conn-2", config.getConnectionId());
        assertEquals(OracleConnectionConfig.AuthenticationType.KERBEROS, config.getAuthenticationType());
        assertEquals("REALM.COM", config.getKerberosRealm());
        assertEquals("kdc.company.com", config.getKerberosKdc());
        assertEquals("user@REALM.COM", config.getKerberosPrincipal());
    }
    
    @Test
    void testConnectionConfigDefaults() {
        OracleConnectionConfig config = new OracleConnectionConfig();
        
        // Test default values
        assertEquals(Integer.valueOf(30), config.getConnectionTimeout());
        assertEquals(Integer.valueOf(60), config.getReadTimeout());
        assertEquals(Boolean.FALSE, config.getUseSSL());
    }
    
    @Test
    void testCloseConnectionWithValidConnection() {
        // Test that closeConnection works correctly with a valid connection
        // This test confirms the logging mechanism doesn't break normal operation
        Connection mockConnection = mock(Connection.class);
        
        // This should not throw any exceptions
        assertDoesNotThrow(() -> connectionService.closeConnection(mockConnection));
    }
    
    @Test
    void testCloseConnectionWithNullConnection() {
        // Test that closeConnection handles null connections gracefully
        assertDoesNotThrow(() -> connectionService.closeConnection(null));
    }
    
    @Test
    void testCloseConnectionWithSQLException() throws SQLException {
        // Test that closeConnection properly logs errors when SQLException occurs
        Connection mockConnection = mock(Connection.class);
        doThrow(new SQLException("Test SQLException")).when(mockConnection).close();
        
        // This should not throw any exceptions, just log the error
        assertDoesNotThrow(() -> connectionService.closeConnection(mockConnection));
        
        // Verify that close was actually called
        verify(mockConnection, times(1)).close();
    }
    
    @Test
    void testKerberosConnectionDoesNotSetGlobalSystemProperties() {
        // Capture current system properties before the test
        String originalRealm = System.getProperty("java.security.krb5.realm");
        String originalKdc = System.getProperty("java.security.krb5.kdc");
        
        OracleConnectionConfig config = new OracleConnectionConfig();
        config.setConnectionId("test-conn-kerberos");
        config.setHost("oracleserver.company.com");
        config.setPort(1521);
        config.setServiceName("PROD");
        config.setUsername("user@REALM.COM");
        config.setAuthenticationType(OracleConnectionConfig.AuthenticationType.KERBEROS);
        config.setKerberosRealm("TEST.REALM.COM");
        config.setKerberosKdc("test-kdc.company.com");
        config.setKerberosPrincipal("user@REALM.COM");
        
        // Test that creating Kerberos connection doesn't affect global system properties
        // Note: This test will fail with LoginException due to invalid credentials,
        // but the important thing is to verify system properties are not set globally
        try {
            connectionService.createConnection(config);
        } catch (SQLException e) {
            // Expected to fail due to invalid Kerberos setup, but that's okay for this test
        }
        
        // Verify that system properties were not modified globally
        String finalRealm = System.getProperty("java.security.krb5.realm");
        String finalKdc = System.getProperty("java.security.krb5.kdc");
        
        assertEquals(originalRealm, finalRealm, "Global java.security.krb5.realm should not be modified");
        assertEquals(originalKdc, finalKdc, "Global java.security.krb5.kdc should not be modified");
    }
}