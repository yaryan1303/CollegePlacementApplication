import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import Navbar from './components/layout/Navbar';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import StudentDashboard from './components/student/Dashboard';
import AdminDashboard from './components/admin/Dashboard';
import CompanyList from './components/student/CompanyList';
import ApplicationList from './components/student/ApplicationList';
import Profile from './components/student/Profile';
import AdminStudents from './components/admin/Students';
import AdminCompanies from './components/admin/Companies';
import AdminApplications from './components/admin/Applications';

const AppRoutes: React.FC = () => {
  const { user } = useAuth();

  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        {user && <Navbar />}
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* Protected routes */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <StudentDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/companies"
            element={
              <ProtectedRoute>
                <CompanyList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/applications"
            element={
              <ProtectedRoute>
                <ApplicationList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          
          {/* Admin routes */}
          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute requireAdmin>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/students"
            element={
              <ProtectedRoute requireAdmin>
                <AdminStudents />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/companies"
            element={
              <ProtectedRoute requireAdmin>
                <AdminCompanies />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/applications"
            element={
              <ProtectedRoute requireAdmin>
                <AdminApplications />
              </ProtectedRoute>
            }
          />
          
          {/* Default redirects */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </div>
    </Router>
  );
};

function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  );
}

export default App;
