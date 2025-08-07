# Placement Portal - React Frontend

A modern React TypeScript application for college placement management, providing interfaces for both students and administrators.

## Features

### For Students
- **Authentication**: Secure login/register with JWT tokens
- **Dashboard**: Overview of applications, placements, and deadlines
- **Company Listings**: Browse available job opportunities with filters and search
- **Application Management**: Apply to companies and track application status
- **Profile Management**: Update personal and academic information

### For Administrators
- **Admin Dashboard**: Overview of system statistics
- **Student Management**: View and manage student records
- **Company Management**: Add and manage company visits
- **Application Oversight**: Review and update application statuses

## Technology Stack

- **Frontend**: React 18 with TypeScript
- **Styling**: Tailwind CSS with Headless UI components
- **Icons**: Heroicons
- **Routing**: React Router v6
- **Forms**: React Hook Form with Yup validation
- **HTTP Client**: Axios
- **State Management**: React Context API

## Project Structure

```
src/
├── components/
│   ├── auth/           # Authentication components
│   │   ├── Login.tsx
│   │   └── Register.tsx
│   ├── student/        # Student interface components
│   │   ├── Dashboard.tsx
│   │   ├── CompanyList.tsx
│   │   ├── ApplicationList.tsx
│   │   └── Profile.tsx
│   ├── admin/          # Admin interface components
│   │   ├── Dashboard.tsx
│   │   ├── Students.tsx
│   │   ├── Companies.tsx
│   │   └── Applications.tsx
│   ├── layout/         # Layout components
│   │   └── Navbar.tsx
│   └── common/         # Shared components
│       └── ProtectedRoute.tsx
├── contexts/           # React contexts
│   └── AuthContext.tsx
├── services/           # API service layer
│   └── api.ts
├── types/             # TypeScript type definitions
│   └── index.ts
└── App.tsx            # Main application component
```

## API Integration

The application integrates with a Spring Boot backend with the following endpoints:

### Authentication Endpoints
- `POST /api/auth/public/login` - User login
- `POST /api/auth/public/register` - User registration
- `POST /api/auth/public/forgot-password` - Password reset request
- `POST /api/auth/public/reset-password` - Password reset

### Student Endpoints
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/student-details` - Update student details
- `GET /api/users/companies` - Get available companies
- `GET /api/users/company-visits` - Get company visits
- `POST /api/users/apply/{visitId}` - Apply to company
- `GET /api/users/my-applications` - Get user's applications
- `GET /api/users/departments` - Get departments list

### Admin Endpoints
- `GET /api/admin/students` - Get all students
- `POST /api/admin/companies` - Create company
- `PUT /api/admin/companies/{id}` - Update company
- `DELETE /api/admin/companies/{id}` - Delete company
- `GET /api/admin/applications` - Get all applications
- `PUT /api/admin/applications/{id}/status` - Update application status

## Getting Started

### Prerequisites
- Node.js (v16 or higher)
- npm or yarn
- Running Spring Boot backend on port 8080

### Installation

1. Navigate to the frontend directory:
```bash
cd placement-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The application will be available at `http://localhost:3000`

### Environment Configuration

The frontend is configured to connect to the backend at `http://localhost:8080/api`. You can modify the API base URL in `src/services/api.ts` if needed.

## Available Scripts

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm test` - Run tests
- `npm run eject` - Eject from Create React App

## Features Overview

### Authentication System
- JWT token-based authentication
- Automatic token refresh handling
- Role-based access control (Student/Admin)
- Protected routes with authentication checks

### Modern UI/UX
- Responsive design with Tailwind CSS
- Clean, professional interface
- Loading states and error handling
- Mobile-friendly navigation

### Student Features
- Comprehensive dashboard with statistics
- Advanced company search and filtering
- Real-time application status tracking
- Complete profile management

### Admin Features
- System overview dashboard
- Student and company management interfaces
- Application review and status updates
- Placeholder sections for future enhancements

## State Management

The application uses React Context API for:
- **AuthContext**: User authentication state and methods
- JWT token management
- User role detection
- Login/logout functionality

## Type Safety

Full TypeScript integration with:
- Comprehensive type definitions for all API responses
- Type-safe API service methods
- Form validation with typed schemas
- Component prop types

## API Error Handling

- Automatic token expiration handling
- Network error recovery
- User-friendly error messages
- Loading states for all async operations

## Security Features

- JWT token storage in localStorage
- Automatic token inclusion in API requests
- Route protection based on authentication status
- Role-based component rendering

## Future Enhancements

The application is designed to be easily extensible with:
- Additional admin management features
- Advanced reporting and analytics
- Real-time notifications
- File upload capabilities
- Email integration

## Contributing

1. Follow the existing code structure and naming conventions
2. Add TypeScript types for all new features
3. Include error handling for all API calls
4. Test on both mobile and desktop viewports
5. Update this README for any new features

## Backend Integration

This frontend is designed to work with the Spring Boot backend located in the parent directory. Ensure the backend is running on port 8080 before starting the frontend development server.

Key integration points:
- Authentication flows
- API endpoint compatibility
- Data type consistency
- Error response handling
