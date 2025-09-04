package com.datalineage.oracle.discovery.config;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom JAAS Configuration for Kerberos authentication that provides
 * realm and KDC settings programmatically without setting global system properties
 */
public class KerberosConfiguration extends Configuration {
    
    private final String realm;
    private final String kdc;
    
    public KerberosConfiguration(String realm, String kdc) {
        this.realm = realm;
        this.kdc = kdc;
    }
    
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        Map<String, String> options = new HashMap<>();
        options.put("useTicketCache", "true");
        options.put("renewTGT", "true");
        options.put("doNotPrompt", "true");
        if (realm != null) {
            options.put("realm", realm);
        }
        if (kdc != null) {
            options.put("kdc", kdc);
        }
        
        AppConfigurationEntry entry = new AppConfigurationEntry(
            "com.sun.security.auth.module.Krb5LoginModule",
            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
            options
        );
        
        return new AppConfigurationEntry[]{entry};
    }
}