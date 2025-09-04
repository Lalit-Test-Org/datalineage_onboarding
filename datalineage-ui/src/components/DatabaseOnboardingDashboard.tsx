import React, { useState, useEffect } from 'react';
import { OracleOnboardingForm } from './oracle/OracleOnboardingForm';
import { OracleConnectionList } from './oracle/OracleConnectionList';
import { MetadataDiscoveryPanel } from './oracle/MetadataDiscoveryPanel';
import { GraphDashboard } from './graph/GraphDashboard';
import { OracleConnection } from '../types/oracle';
import { OracleApiService } from '../services/oracleApi';
import './DatabaseOnboardingDashboard.css';

interface DashboardTab {
  id: string;
  label: string;
  component: React.ReactNode;
}

export const DatabaseOnboardingDashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('onboard');
  const [refreshConnections, setRefreshConnections] = useState<boolean>(false);
  const [connections, setConnections] = useState<OracleConnection[]>([]);

  // Load connections for the discovery panel
  const loadConnections = async () => {
    try {
      const response = await OracleApiService.getConnections();
      setConnections(response.data);
    } catch (error) {
      console.error('Failed to load connections:', error);
      setConnections([]);
    }
  };

  useEffect(() => {
    loadConnections();
  }, []);

  const handleOnboardingSuccess = (connection: OracleConnection) => {
    console.log('Successfully onboarded connection:', connection);
    // Switch to connections tab and refresh the list
    setActiveTab('connections');
    setRefreshConnections(true);
    // Also refresh the connections for discovery panel
    loadConnections();
  };

  const handleConnectionEdit = (connection: OracleConnection) => {
    console.log('Edit connection:', connection);
    // Future enhancement: populate form with connection data for editing
    setActiveTab('onboard');
  };

  const handleConnectionTest = (connection: OracleConnection) => {
    console.log('Test connection:', connection);
    // The test is handled in the component, this is just for logging
  };

  const handleConnectionSelect = (connection: OracleConnection) => {
    console.log('Selected connection for discovery:', connection);
    // Switch to discovery tab
    setActiveTab('discovery');
  };

  const handleRefreshComplete = () => {
    setRefreshConnections(false);
    // Also refresh the connections for discovery panel
    loadConnections();
  };

  const tabs: DashboardTab[] = [
    {
      id: 'onboard',
      label: 'Onboard Database',
      component: (
        <OracleOnboardingForm 
          onSuccess={handleOnboardingSuccess}
        />
      )
    },
    {
      id: 'connections',
      label: 'Manage Connections',
      component: (
        <OracleConnectionList 
          onEdit={handleConnectionEdit}
          onTest={handleConnectionTest}
          refresh={refreshConnections}
          onRefreshComplete={handleRefreshComplete}
        />
      )
    },
    {
      id: 'discovery',
      label: 'Metadata Discovery',
      component: (
        <MetadataDiscoveryPanel
          connections={connections}
          onConnectionSelect={handleConnectionSelect}
        />
      )
    },
    {
      id: 'graph',
      label: 'Graph Visualization',
      component: (
        <GraphDashboard />
      )
    }
  ];

  return (
    <div className="database-onboarding-dashboard">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Data Lineage Onboarding</h1>
          <p>Configure and manage your database connections for data lineage discovery</p>
        </div>
      </header>

      <nav className="dashboard-nav">
        <div className="nav-tabs">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              className={`nav-tab ${activeTab === tab.id ? 'active' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </nav>

      <main className="dashboard-content">
        {tabs.find(tab => tab.id === activeTab)?.component}
      </main>

      <footer className="dashboard-footer">
        <div className="footer-content">
          <p>Data Lineage Discovery Service - Oracle Database Onboarding</p>
          <div className="footer-links">
            <a href="/api/v1/oracle/health" target="_blank" rel="noopener noreferrer">
              API Health
            </a>
            <span>|</span>
            <a href="http://localhost:8083/h2-console" target="_blank" rel="noopener noreferrer">
              Database Console
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
};