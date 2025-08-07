import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/public/login', credentials),
  register: (userData) => api.post('/auth/public/register', userData),
  forgotPassword: (email) => api.post('/auth/public/forgot-password', { email }),
  resetPassword: (data) => api.post('/auth/public/reset-password', data),
};

// Admin API
export const adminAPI = {
  // Students
  getAllStudents: () => api.get('/admin/students'),
  getStudentById: (id) => api.get(`/admin/students/${id}`),
  getStudentsByBatch: (batchYear) => api.get(`/admin/students/batch/${batchYear}`),
  getStudentsByDepartment: (departmentId) => api.get(`/admin/students/department/${departmentId}`),
  getStudentsByBatchAndDepartment: (batchYear, departmentId) => 
    api.get(`/admin/students/batch/${batchYear}/department/${departmentId}`),

  // Companies
  createCompany: (companyData) => api.post('/admin/companies', companyData),
  getAllCompanies: () => api.get('/admin/companies'),
  getCompanyById: (id) => api.get(`/admin/companies/${id}`),
  updateCompany: (id, companyData) => api.put(`/admin/companies/${id}`, companyData),
  deleteCompany: (id) => api.delete(`/admin/companies/${id}`),

  // Company Visits
  scheduleVisit: (visitData) => api.post('/admin/visits', visitData),
  updateVisit: (id, visitData) => api.put(`/admin/visits/${id}`, visitData),
  updateVisitStatus: (id, isActive) => api.put(`/admin/visits/${id}/status?isActive=${isActive}`),

  // Applications
  getAllApplications: (status) => api.get(`/admin/applications${status ? `?status=${status}` : ''}`),
  getApplicationsByCompany: (companyName) => api.get(`/admin/applications/by-company?companyName=${companyName}`),
  updateApplicationStatus: (id, status, feedback) => 
    api.put(`/admin/applications/${id}?status=${status}${feedback ? `&feedback=${feedback}` : ''}`),

  // Reports
  getPlacementSummary: () => api.get('/admin/reports/placement-summary'),
  getCompanyStats: () => api.get('/admin/reports/company-stats'),
  exportCompanyStats: () => api.get('/admin/reports/company-stats/export', { responseType: 'blob' }),

  // Departments
  createDepartment: (departmentData) => api.post('/admin/departments', departmentData),
  updateDepartment: (id, departmentData) => api.put(`/admin/departments/${id}`, departmentData),
};

// User API
export const userAPI = {
  // Profile
  saveStudentDetails: (studentData) => api.post('/users/me', studentData),
  getStudentDetails: (studentId) => api.get(`/users/me/${studentId}`),
  updateStudentDetails: (studentId, studentData) => api.put(`/users/me/${studentId}`, studentData),

  // Companies and Visits
  getActiveVisits: () => api.get('/users/companies/visits'),
  getCompanyVisitById: (id) => api.get(`/users/companies/visits/${id}`),

  // Applications
  applyForCompany: (applicationData) => api.post('/users/applications', applicationData),
  getAllApplications: () => api.get('/users/applications'),

  // Placement Records
  getPlacementRecords: (batchYear, companyName) => {
    const params = new URLSearchParams();
    if (batchYear) params.append('batchYear', batchYear);
    if (companyName) params.append('companyName', companyName);
    return api.get(`/users/records?${params.toString()}`);
  },
  getAllPlacementRecords: () => api.get('/users/placementsRecords'),

  // Departments
  getAllDepartments: () => api.get('/users/departments'),
};

export default api;