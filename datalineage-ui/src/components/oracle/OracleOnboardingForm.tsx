import React, { useState } from 'react';
import { 
  OracleOnboardingRequest, 
  AuthenticationType, 
  FormErrors 
} from '../../types/oracle';
import { 
  validateOracleOnboardingForm, 
  hasValidationErrors, 
  createDefaultFormData,
  sanitizeInput 
} from '../../utils/validation';
import { OracleApiService } from '../../services/oracleApi';
import './OracleOnboardingForm.css';

interface OracleOnboardingFormProps {
  onSuccess?: (connection: any) => void;
  onCancel?: () => void;
}

export const OracleOnboardingForm: React.FC<OracleOnboardingFormProps> = ({
  onSuccess,
  onCancel
}) => {
  const [formData, setFormData] = useState<OracleOnboardingRequest>(createDefaultFormData());
  const [errors, setErrors] = useState<FormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitMessage, setSubmitMessage] = useState<string>('');
  const [submitMessageType, setSubmitMessageType] = useState<'success' | 'error'>('success');

  const handleInputChange = (field: keyof OracleOnboardingRequest, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: typeof value === 'string' ? sanitizeInput(value) : value
    }));
    
    // Clear error for this field when user starts typing
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: ''
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate form
    const validationErrors = validateOracleOnboardingForm(formData);
    setErrors(validationErrors);
    
    if (hasValidationErrors(validationErrors)) {
      setSubmitMessage('Please fix the validation errors before submitting.');
      setSubmitMessageType('error');
      return;
    }

    setIsSubmitting(true);
    setSubmitMessage('');

    try {
      const response = await OracleApiService.onboardDatabase(formData);
      setSubmitMessage('Oracle database onboarded successfully!');
      setSubmitMessageType('success');
      
      if (onSuccess) {
        onSuccess(response.data);
      }
    } catch (error: any) {
      setSubmitMessage(error.message || 'Failed to onboard Oracle database');
      setSubmitMessageType('error');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleReset = () => {
    setFormData(createDefaultFormData());
    setErrors({});
    setSubmitMessage('');
  };

  return (
    <div className="oracle-onboarding-form">
      <div className="form-header">
        <h2>Oracle Database Onboarding</h2>
        <p>Configure your Oracle database connection for data lineage discovery</p>
      </div>

      {submitMessage && (
        <div className={`alert alert-${submitMessageType}`}>
          {submitMessage}
        </div>
      )}

      <form onSubmit={handleSubmit} className="onboarding-form">
        {/* Basic Connection Information */}
        <div className="form-section">
          <h3>Basic Information</h3>
          
          <div className="form-group">
            <label htmlFor="connectionName">Connection Name *</label>
            <input
              type="text"
              id="connectionName"
              value={formData.connectionName}
              onChange={(e) => handleInputChange('connectionName', e.target.value)}
              className={errors.connectionName ? 'error' : ''}
              placeholder="Enter a unique name for this connection"
            />
            {errors.connectionName && <span className="error-message">{errors.connectionName}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              value={formData.description || ''}
              onChange={(e) => handleInputChange('description', e.target.value)}
              placeholder="Optional description of this Oracle database"
              rows={3}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="host">Host *</label>
              <input
                type="text"
                id="host"
                value={formData.host}
                onChange={(e) => handleInputChange('host', e.target.value)}
                className={errors.host ? 'error' : ''}
                placeholder="oracle.example.com"
              />
              {errors.host && <span className="error-message">{errors.host}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="port">Port *</label>
              <input
                type="number"
                id="port"
                value={formData.port}
                onChange={(e) => handleInputChange('port', parseInt(e.target.value) || 1521)}
                className={errors.port ? 'error' : ''}
                min="1"
                max="65535"
              />
              {errors.port && <span className="error-message">{errors.port}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="serviceName">Service Name *</label>
            <input
              type="text"
              id="serviceName"
              value={formData.serviceName}
              onChange={(e) => handleInputChange('serviceName', e.target.value)}
              className={errors.serviceName ? 'error' : ''}
              placeholder="ORCL"
            />
            {errors.serviceName && <span className="error-message">{errors.serviceName}</span>}
          </div>
        </div>

        {/* Authentication Configuration */}
        <div className="form-section">
          <h3>Authentication</h3>
          
          <div className="form-group">
            <label>Authentication Type *</label>
            <div className="radio-group">
              <label className="radio-label">
                <input
                  type="radio"
                  value={AuthenticationType.DIRECT}
                  checked={formData.authenticationType === AuthenticationType.DIRECT}
                  onChange={(e) => handleInputChange('authenticationType', e.target.value)}
                />
                Direct (Username/Password)
              </label>
              <label className="radio-label">
                <input
                  type="radio"
                  value={AuthenticationType.KERBEROS}
                  checked={formData.authenticationType === AuthenticationType.KERBEROS}
                  onChange={(e) => handleInputChange('authenticationType', e.target.value)}
                />
                Kerberos
              </label>
            </div>
            {errors.authenticationType && <span className="error-message">{errors.authenticationType}</span>}
          </div>

          {/* Direct Authentication Fields */}
          {formData.authenticationType === AuthenticationType.DIRECT && (
            <div className="auth-section">
              <div className="form-group">
                <label htmlFor="username">Username *</label>
                <input
                  type="text"
                  id="username"
                  value={formData.username || ''}
                  onChange={(e) => handleInputChange('username', e.target.value)}
                  className={errors.username ? 'error' : ''}
                  placeholder="Database username"
                />
                {errors.username && <span className="error-message">{errors.username}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="password">Password *</label>
                <input
                  type="password"
                  id="password"
                  value={formData.password || ''}
                  onChange={(e) => handleInputChange('password', e.target.value)}
                  className={errors.password ? 'error' : ''}
                  placeholder="Database password"
                />
                {errors.password && <span className="error-message">{errors.password}</span>}
              </div>
            </div>
          )}

          {/* Kerberos Authentication Fields */}
          {formData.authenticationType === AuthenticationType.KERBEROS && (
            <div className="auth-section">
              <div className="form-group">
                <label htmlFor="kerberosRealm">Kerberos Realm *</label>
                <input
                  type="text"
                  id="kerberosRealm"
                  value={formData.kerberosRealm || ''}
                  onChange={(e) => handleInputChange('kerberosRealm', e.target.value)}
                  className={errors.kerberosRealm ? 'error' : ''}
                  placeholder="COMPANY.COM"
                />
                {errors.kerberosRealm && <span className="error-message">{errors.kerberosRealm}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="kerberosKdc">Kerberos KDC *</label>
                <input
                  type="text"
                  id="kerberosKdc"
                  value={formData.kerberosKdc || ''}
                  onChange={(e) => handleInputChange('kerberosKdc', e.target.value)}
                  className={errors.kerberosKdc ? 'error' : ''}
                  placeholder="kdc.company.com"
                />
                {errors.kerberosKdc && <span className="error-message">{errors.kerberosKdc}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="kerberosPrincipal">Kerberos Principal</label>
                <input
                  type="text"
                  id="kerberosPrincipal"
                  value={formData.kerberosPrincipal || ''}
                  onChange={(e) => handleInputChange('kerberosPrincipal', e.target.value)}
                  placeholder="datalineage@COMPANY.COM"
                />
              </div>

              <div className="form-group">
                <label htmlFor="kerberosKeytabPath">Keytab File Path</label>
                <input
                  type="text"
                  id="kerberosKeytabPath"
                  value={formData.kerberosKeytabPath || ''}
                  onChange={(e) => handleInputChange('kerberosKeytabPath', e.target.value)}
                  placeholder="/path/to/keytab/file"
                />
              </div>
            </div>
          )}
        </div>

        {/* Advanced Options */}
        <div className="form-section">
          <h3>Advanced Options</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="connectionTimeout">Connection Timeout (ms)</label>
              <input
                type="number"
                id="connectionTimeout"
                value={formData.connectionTimeout || 30000}
                onChange={(e) => handleInputChange('connectionTimeout', parseInt(e.target.value) || 30000)}
                className={errors.connectionTimeout ? 'error' : ''}
                min="1000"
              />
              {errors.connectionTimeout && <span className="error-message">{errors.connectionTimeout}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="readTimeout">Read Timeout (ms)</label>
              <input
                type="number"
                id="readTimeout"
                value={formData.readTimeout || 60000}
                onChange={(e) => handleInputChange('readTimeout', parseInt(e.target.value) || 60000)}
                className={errors.readTimeout ? 'error' : ''}
                min="1000"
              />
              {errors.readTimeout && <span className="error-message">{errors.readTimeout}</span>}
            </div>
          </div>

          <div className="form-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={formData.useSSL || false}
                onChange={(e) => handleInputChange('useSSL', e.target.checked)}
              />
              Enable SSL Connection
            </label>
          </div>

          {formData.useSSL && (
            <div className="ssl-section">
              <div className="form-group">
                <label htmlFor="sslTruststore">SSL Truststore Path *</label>
                <input
                  type="text"
                  id="sslTruststore"
                  value={formData.sslTruststore || ''}
                  onChange={(e) => handleInputChange('sslTruststore', e.target.value)}
                  className={errors.sslTruststore ? 'error' : ''}
                  placeholder="/path/to/truststore.jks"
                />
                {errors.sslTruststore && <span className="error-message">{errors.sslTruststore}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="sslTruststorePassword">SSL Truststore Password *</label>
                <input
                  type="password"
                  id="sslTruststorePassword"
                  value={formData.sslTruststorePassword || ''}
                  onChange={(e) => handleInputChange('sslTruststorePassword', e.target.value)}
                  className={errors.sslTruststorePassword ? 'error' : ''}
                  placeholder="Truststore password"
                />
                {errors.sslTruststorePassword && <span className="error-message">{errors.sslTruststorePassword}</span>}
              </div>
            </div>
          )}

          <div className="form-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={formData.autoDiscoverMetadata !== false}
                onChange={(e) => handleInputChange('autoDiscoverMetadata', e.target.checked)}
              />
              Automatically discover metadata after onboarding
            </label>
          </div>
        </div>

        {/* Form Actions */}
        <div className="form-actions">
          <button
            type="button"
            onClick={handleReset}
            className="btn btn-secondary"
            disabled={isSubmitting}
          >
            Reset
          </button>
          {onCancel && (
            <button
              type="button"
              onClick={onCancel}
              className="btn btn-secondary"
              disabled={isSubmitting}
            >
              Cancel
            </button>
          )}
          <button
            type="submit"
            className="btn btn-primary"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Onboarding...' : 'Onboard Database'}
          </button>
        </div>
      </form>
    </div>
  );
};