package com.datalineage.oracle.discovery.service;

import com.datalineage.oracle.discovery.dto.OracleConnectionConfig;
import oracle.jdbc.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Service for managing Oracle database connections with direct and Kerberos authentication
 */
@Service
public class OracleConnectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(OracleConnectionService.class);
    
    /**
     * Creates Oracle connection based on configuration
     */
    public Connection createConnection(OracleConnectionConfig config) throws SQLException {
        switch (config.getAuthenticationType()) {
            case DIRECT:
                return createDirectConnection(config);
            case KERBEROS:
                return createKerberosConnection(config);
            default:
                throw new IllegalArgumentException("Unsupported authentication type: " + config.getAuthenticationType());
        }
    }
    
    /**
     * Creates direct Oracle connection with username/password
     */
    private Connection createDirectConnection(OracleConnectionConfig config) throws SQLException {
        String jdbcUrl = buildJdbcUrl(config);
        Properties props = new Properties();
        props.setProperty("user", config.getUsername());
        props.setProperty("password", config.getPassword());
        
        // Set connection properties
        if (config.getConnectionTimeout() != null) {
            props.setProperty("oracle.net.CONNECT_TIMEOUT", config.getConnectionTimeout().toString());
        }
        if (config.getReadTimeout() != null) {
            props.setProperty("oracle.net.READ_TIMEOUT", config.getReadTimeout().toString());
        }
        
        // SSL configuration
        if (Boolean.TRUE.equals(config.getUseSSL())) {
            props.setProperty("oracle.net.ssl_server_dn_match", "true");
            props.setProperty("oracle.net.ssl_version", "1.2");
            if (config.getSslTruststore() != null) {
                System.setProperty("javax.net.ssl.trustStore", config.getSslTruststore());
                if (config.getSslTruststorePassword() != null) {
                    System.setProperty("javax.net.ssl.trustStorePassword", config.getSslTruststorePassword());
                }
            }
        }
        
        return DriverManager.getConnection(jdbcUrl, props);
    }
    
    /**
     * Creates Kerberos Oracle connection
     */
    private Connection createKerberosConnection(OracleConnectionConfig config) throws SQLException {
        try {
            // Set Kerberos properties
            System.setProperty("java.security.krb5.realm", config.getKerberosRealm());
            System.setProperty("java.security.krb5.kdc", config.getKerberosKdc());
            
            // Create login context for Kerberos authentication
            LoginContext loginContext = new LoginContext("OracleKerberos");
            loginContext.login();
            
            Subject subject = loginContext.getSubject();
            
            // Create connection with Kerberos authentication
            String jdbcUrl = buildJdbcUrl(config);
            Properties props = new Properties();
            props.setProperty("oracle.net.authentication_services", "KERBEROS5");
            props.setProperty("oracle.net.kerberos5_mutual_authentication", "true");
            
            if (config.getKerberosPrincipal() != null) {
                props.setProperty("oracle.net.kerberos5_principal", config.getKerberosPrincipal());
            }
            
            // Set connection properties
            if (config.getConnectionTimeout() != null) {
                props.setProperty("oracle.net.CONNECT_TIMEOUT", config.getConnectionTimeout().toString());
            }
            if (config.getReadTimeout() != null) {
                props.setProperty("oracle.net.READ_TIMEOUT", config.getReadTimeout().toString());
            }
            
            return DriverManager.getConnection(jdbcUrl, props);
            
        } catch (LoginException e) {
            throw new SQLException("Kerberos authentication failed", e);
        }
    }
    
    /**
     * Builds JDBC URL from configuration
     */
    private String buildJdbcUrl(OracleConnectionConfig config) {
        StringBuilder url = new StringBuilder("jdbc:oracle:thin:@");
        
        if (Boolean.TRUE.equals(config.getUseSSL())) {
            url.append("(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=");
        } else {
            url.append("(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=");
        }
        
        url.append(config.getHost())
           .append(")(PORT=")
           .append(config.getPort())
           .append("))(CONNECT_DATA=(SERVICE_NAME=")
           .append(config.getServiceName())
           .append(")))");
        
        return url.toString();
    }
    
    /**
     * Tests connection with given configuration
     */
    public boolean testConnection(OracleConnectionConfig config) {
        try (Connection connection = createConnection(config)) {
            return connection.isValid(config.getConnectionTimeout() != null ? config.getConnectionTimeout() : 30);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Closes connection safely
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Log error but don't throw
                logger.error("Error closing Oracle connection: {}", e.getMessage());
            }
        }
    }
}