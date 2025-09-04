package com.datalineage.oracle.discovery.service;

import com.datalineage.oracle.discovery.dto.OracleConnectionConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

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
}