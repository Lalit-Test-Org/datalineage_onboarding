import { OracleOnboardingRequest, AuthenticationType, FormErrors } from '../types/oracle';

/**
 * Validates Oracle onboarding form data
 */
export const validateOracleOnboardingForm = (formData: OracleOnboardingRequest): FormErrors => {
  const errors: FormErrors = {};

  // Validate required fields
  if (!formData.connectionName?.trim()) {
    errors.connectionName = 'Connection name is required';
  }

  if (!formData.host?.trim()) {
    errors.host = 'Host is required';
  }

  if (!formData.port || formData.port < 1 || formData.port > 65535) {
    errors.port = 'Port must be between 1 and 65535';
  }

  if (!formData.serviceName?.trim()) {
    errors.serviceName = 'Service name is required';
  }

  if (!formData.authenticationType) {
    errors.authenticationType = 'Authentication type is required';
  }

  // Validate authentication-specific fields
  if (formData.authenticationType === AuthenticationType.DIRECT) {
    if (!formData.username?.trim()) {
      errors.username = 'Username is required for direct authentication';
    }
    if (!formData.password?.trim()) {
      errors.password = 'Password is required for direct authentication';
    }
  } else if (formData.authenticationType === AuthenticationType.KERBEROS) {
    if (!formData.kerberosRealm?.trim()) {
      errors.kerberosRealm = 'Kerberos realm is required for Kerberos authentication';
    }
    if (!formData.kerberosKdc?.trim()) {
      errors.kerberosKdc = 'Kerberos KDC is required for Kerberos authentication';
    }
  }

  // Validate optional timeout values
  if (formData.connectionTimeout && formData.connectionTimeout < 1000) {
    errors.connectionTimeout = 'Connection timeout must be at least 1000ms';
  }

  if (formData.readTimeout && formData.readTimeout < 1000) {
    errors.readTimeout = 'Read timeout must be at least 1000ms';
  }

  // Validate SSL settings
  if (formData.useSSL) {
    if (!formData.sslTruststore?.trim()) {
      errors.sslTruststore = 'SSL truststore path is required when SSL is enabled';
    }
    if (!formData.sslTruststorePassword?.trim()) {
      errors.sslTruststorePassword = 'SSL truststore password is required when SSL is enabled';
    }
  }

  return errors;
};

/**
 * Checks if the form has any validation errors
 */
export const hasValidationErrors = (errors: FormErrors): boolean => {
  return Object.keys(errors).length > 0;
};

/**
 * Formats validation error messages for display
 */
export const formatErrorMessage = (fieldName: string, error: string): string => {
  return `${fieldName}: ${error}`;
};

/**
 * Creates default form data
 */
export const createDefaultFormData = (): OracleOnboardingRequest => {
  return {
    connectionName: '',
    description: '',
    host: '',
    port: 1521,
    serviceName: '',
    authenticationType: AuthenticationType.DIRECT,
    username: '',
    password: '',
    kerberosRealm: '',
    kerberosKdc: '',
    kerberosPrincipal: '',
    kerberosKeytabPath: '',
    connectionTimeout: 30000,
    readTimeout: 60000,
    useSSL: false,
    sslTruststore: '',
    sslTruststorePassword: '',
    autoDiscoverMetadata: true,
  };
};

/**
 * Checks if a hostname is valid
 */
export const isValidHostname = (hostname: string): boolean => {
  const hostnameRegex = /^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
  return hostnameRegex.test(hostname);
};

/**
 * Sanitizes form input to prevent XSS
 */
export const sanitizeInput = (input: string): string => {
  return input.trim().replace(/[<>]/g, '');
};