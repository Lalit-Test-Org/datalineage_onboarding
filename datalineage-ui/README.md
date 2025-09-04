# Data Lineage UI

A React.js frontend application for managing database connections and onboarding to the Data Lineage Discovery Service. This application provides a user-friendly interface for configuring database connections with comprehensive authentication support, starting with Oracle databases and designed to be extended for additional database types.

## Features

### 🚀 Comprehensive Database Onboarding
- **Oracle Database Support**: Full Oracle database onboarding with Direct and Kerberos authentication
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
- **Component-based**: Reusable components designed for multiple database types
- **Service Layer**: Clean API integration with error handling
- **Utility Functions**: Validation helpers and form utilities
- **Extensible Design**: Easy to add support for additional database types

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
   cd datalineage-ui
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

### Onboarding a New Database

1. **Navigate to the "Onboard Database" tab**
2. **Fill in Basic Information** (Connection Name, Host, Port, Service Name)
3. **Choose Authentication Type** (Direct or Kerberos)
4. **Configure Advanced Options** (Optional: SSL, timeouts, metadata discovery)
5. **Click "Onboard Database"** to submit

### Managing Existing Connections

1. **Navigate to the "Manage Connections" tab**
2. **View all configured database connections**
3. **Test, Edit, or Delete** connections as needed

## API Integration

Communicates with Data Lineage Discovery Service API:
- Oracle databases: `http://localhost:8083/api/v1/oracle`

## Development

### Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run test suite

### Architecture

The application follows a modular architecture to support multiple database types:
- Components are organized by database type (`src/components/oracle/`, future: `src/components/postgresql/`, etc.)
- TypeScript interfaces ensure type safety (`src/types/`)
- Service layer handles API communication (`src/services/`)
- Utility functions provide validation and helpers (`src/utils/`)

### Adding Support for New Database Types

The modular architecture makes it easy to add support for additional databases:

1. **Create database-specific components** in `src/components/{database-type}/`
2. **Add TypeScript interfaces** in `src/types/{database-type}.ts`
3. **Implement API service** in `src/services/{database-type}Api.ts`
4. **Update main dashboard** to include new database option

## Screenshots

The UI provides intuitive forms for database onboarding with comprehensive validation and error handling, currently supporting Oracle databases with plans for additional database types.
