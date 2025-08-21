import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { LoginRequest, RegisterRequest, LoginResponse, GenericResponse, ForgotPasswordRequest, ResetPasswordRequest } from '../types';

class ApiService {
  private api: AxiosInstance;
  private baseURL = 'http://localhost:8080/api';

  constructor() {
    this.api = axios.create({
      baseURL: this.baseURL,
      timeout: 10000,
    });

    // Request interceptor to add auth token
    this.api.interceptors.request.use(
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

    // Response interceptor for error handling
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Token expired or invalid
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.api.post('/auth/public/login', credentials);
    return response.data;
  }

  async register(userData: RegisterRequest): Promise<GenericResponse> {
    const response: AxiosResponse<GenericResponse> = await this.api.post('/auth/public/register', userData);
    return response.data;
  }

  async forgotPassword(email: ForgotPasswordRequest): Promise<GenericResponse> {
    const response: AxiosResponse<GenericResponse> = await this.api.post('/auth/public/forgot-password', email);
    return response.data;
  }

  async resetPassword(resetData: ResetPasswordRequest): Promise<GenericResponse> {
    const response: AxiosResponse<GenericResponse> = await this.api.post('/auth/public/reset-password', resetData);
    return response.data;
  }

  // User endpoints
  async getCurrentUser(): Promise<any> {
    const response = await this.api.get('/users/profile');
    return response.data;
  }

  async updateProfile(profileData: any): Promise<any> {
    const response = await this.api.put('/users/profile', profileData);
    return response.data;
  }

  async updateStudentDetails(studentData: any): Promise<any> {
    const response = await this.api.put('/users/student-details', studentData);
    return response.data;
  }

  // Company endpoints
  async getCompanies(): Promise<any> {
    const response = await this.api.get('/users/companies');
    return response.data;
  }

  async getCompanyVisits(): Promise<any> {
    const response = await this.api.get('/users/company-visits');
    return response.data;
  }

  // Application endpoints
  async applyToCompany(visitId: number): Promise<any> {
    const response = await this.api.post(`/users/apply/${visitId}`);
    return response.data;
  }

  async getMyApplications(): Promise<any> {
    const response = await this.api.get('/users/my-applications');
    return response.data;
  }

  // Placement records
  async getMyPlacements(): Promise<any> {
    const response = await this.api.get('/users/my-placements');
    return response.data;
  }

  // Admin endpoints
  async getStudents(): Promise<any> {
    const response = await this.api.get('/admin/students');
    return response.data;
  }

  async createCompany(companyData: any): Promise<any> {
    const response = await this.api.post('/admin/companies', companyData);
    return response.data;
  }

  async updateCompany(id: number, companyData: any): Promise<any> {
    const response = await this.api.put(`/admin/companies/${id}`, companyData);
    return response.data;
  }

  async deleteCompany(id: number): Promise<any> {
    const response = await this.api.delete(`/admin/companies/${id}`);
    return response.data;
  }

  async createCompanyVisit(visitData: any): Promise<any> {
    const response = await this.api.post('/admin/visits', visitData);
    return response.data;
  }

  async getApplications(): Promise<any> {
    const response = await this.api.get('/admin/applications');
    return response.data;
  }

  async updateApplicationStatus(applicationId: number, status: string): Promise<any> {
    const response = await this.api.put(`/admin/applications/${applicationId}/status`, { status });
    return response.data;
  }

  async getPlacementStats(): Promise<any> {
    const response = await this.api.get('/admin/placement-stats');
    return response.data;
  }

  async getDepartments(): Promise<any> {
    const response = await this.api.get('/users/departments');
    return response.data;
  }
}

export const apiService = new ApiService();