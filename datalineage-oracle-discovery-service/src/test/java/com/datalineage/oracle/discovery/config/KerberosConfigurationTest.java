package com.datalineage.oracle.discovery.config;

import org.junit.jupiter.api.Test;

import javax.security.auth.login.AppConfigurationEntry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KerberosConfiguration
 */
class KerberosConfigurationTest {

    @Test
    void testKerberosConfigurationCreation() {
        String testRealm = "TEST.REALM.COM";
        String testKdc = "test-kdc.company.com";
        
        KerberosConfiguration config = new KerberosConfiguration(testRealm, testKdc);
        
        AppConfigurationEntry[] entries = config.getAppConfigurationEntry("OracleKerberos");
        
        assertNotNull(entries);
        assertEquals(1, entries.length);
        
        AppConfigurationEntry entry = entries[0];
        assertEquals("com.sun.security.auth.module.Krb5LoginModule", entry.getLoginModuleName());
        assertEquals(AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, entry.getControlFlag());
        
        // Verify the options contain the expected realm and KDC
        assertTrue(entry.getOptions().containsKey("realm"));
        assertTrue(entry.getOptions().containsKey("kdc"));
        assertEquals(testRealm, entry.getOptions().get("realm"));
        assertEquals(testKdc, entry.getOptions().get("kdc"));
        
        // Verify other expected options
        assertEquals("true", entry.getOptions().get("useTicketCache"));
        assertEquals("true", entry.getOptions().get("renewTGT"));
        assertEquals("true", entry.getOptions().get("doNotPrompt"));
    }
    
    @Test
    void testKerberosConfigurationWithNullValues() {
        KerberosConfiguration config = new KerberosConfiguration(null, null);
        
        AppConfigurationEntry[] entries = config.getAppConfigurationEntry("OracleKerberos");
        
        assertNotNull(entries);
        assertEquals(1, entries.length);
        
        AppConfigurationEntry entry = entries[0];
        assertNull(entry.getOptions().get("realm"));
        assertNull(entry.getOptions().get("kdc"));
    }
}