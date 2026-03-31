// User types
export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  createdAt: string;
  updatedAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role?: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

// Student types
export interface StudentDetails {
  studentId: number;
  firstName: string;
  lastName: string;
  rollNumber: string;
  batchYear: number;
  department: Department;
  cgpa: number;
  resumeUrl?: string;
  phoneNumber: string;
  currentStatus: PlacementStatus;
  createdAt: string;
  updatedAt: string;
}

export interface Department {
  id: number;
  name: string;
  code: string;
}

export enum PlacementStatus {
  NOT_PLACED = 'NOT_PLACED',
  PLACED = 'PLACED',
  IN_PROCESS = 'IN_PROCESS'
}

// Company types
export interface Company {
  companyId: number;
  name: string;
  description: string;
  website: string;
  contactEmail: string;
  contactPhone: string;
  createdAt: string;
  updatedAt: string;
}

export interface CompanyVisit {
  visitId: number;
  company: Company;
  visitDate: string;
  jobRole: string;
  eligibilityCriteria: string;
  packageOffered: number;
  applicationDeadline: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

// Application types
export interface StudentApplication {
  applicationId: number;
  student: StudentDetails;
  companyVisit: CompanyVisit;
  status: ApplicationStatus;
  appliedAt: string;
  updatedAt: string;
}

export enum ApplicationStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  SHORTLISTED = 'SHORTLISTED'
}

// Placement Record types
export interface PlacementRecord {
  recordId: number;
  student: StudentDetails;
  company: Company;
  jobRole: string;
  packageOffered: number;
  placementDate: string;
  createdAt: string;
  updatedAt: string;
}

// DTOs
export interface GenericResponse {
  success: boolean;
  message: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

// API Response wrappers
export interface ApiResponse<T> {
  data: T;
  success: boolean;
  message?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}