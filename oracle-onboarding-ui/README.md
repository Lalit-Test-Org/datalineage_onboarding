# Oracle Database Onboarding UI

A React.js frontend application for onboarding Oracle databases to the Data Lineage Discovery Service. This application provides a user-friendly interface for configuring Oracle database connections with support for both Direct and Kerberos authentication.

## Features

### 🚀 Comprehensive Oracle Database Onboarding
- **Direct Authentication**: Username/password-based connections
- **Kerberos Authentication**: Enterprise-grade security with realm, KDC, principal, and keytab configuration
- **Connection Testing**: Real-time validation of database connectivity
- **Advanced Configuration**: SSL, timeouts, and metadata discovery options

### 🎨 Modern User Interface
- **Responsive Design**: Mobile-friendly interface that works on all devices
- **Tab-based Navigation**: Separate views for onboarding and connection management
- **Form Validation**: Client-side validation with user-friendly error messages
- **Visual Feedback**: Loading states, success/error notifications

### 🔧 Modular Architecture
- **TypeScript Support**: Type-safe development with comprehensive interfaces
- **Component-based**: Reusable components for future database types
- **Service Layer**: Clean API integration with error handling
- **Utility Functions**: Validation helpers and form utilities

### 🔒 Security Features
- **Input Sanitization**: XSS protection for all form inputs
- **Secure Transmission**: Encrypted transmission of credentials to backend
- **Password Masking**: Secure password fields
- **Validation**: Comprehensive client-side and server-side validation

## Getting Started

### Prerequisites
- Node.js 16+ and npm
- Data Lineage Discovery Service running on localhost:8083

### Installation

1. **Navigate to the UI directory:**
   ```bash
   cd oracle-onboarding-ui
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start the development server:**
   ```bash
   npm start
   ```

4. **Open your browser:**
   Navigate to [http://localhost:3000](http://localhost:3000)

### Building for Production

```bash
npm run build
```

## Usage

### Onboarding a New Oracle Database

1. **Navigate to the "Onboard Database" tab**
2. **Fill in Basic Information** (Connection Name, Host, Port, Service Name)
3. **Choose Authentication Type** (Direct or Kerberos)
4. **Configure Advanced Options** (Optional: SSL, timeouts, metadata discovery)
5. **Click "Onboard Database"** to submit

### Managing Existing Connections

1. **Navigate to the "Manage Connections" tab**
2. **View all configured Oracle connections**
3. **Test, Edit, or Delete** connections as needed

## API Integration

Communicates with Data Lineage Discovery Service API at `http://localhost:8083/api/v1/oracle`

## Development

### Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run test suite

### Architecture

The application follows a modular architecture to support future database types:
- Components are organized by database type (`src/components/oracle/`)
- TypeScript interfaces ensure type safety (`src/types/`)
- Service layer handles API communication (`src/services/`)
- Utility functions provide validation and helpers (`src/utils/`)

## Screenshots

The UI provides intuitive forms for both Direct and Kerberos authentication, with comprehensive validation and error handling.
