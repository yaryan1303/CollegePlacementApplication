import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, LoginRequest, RegisterRequest, LoginResponse } from '../types';
import { apiService } from '../services/api';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => void;
  isAdmin: () => boolean;
  isStudent: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Initialize auth state from localStorage
  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');

    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginRequest): Promise<void> => {
    try {
      const response: LoginResponse = await apiService.login(credentials);
      
      setToken(response.token);
      setUser(response.user);
      
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(response.user));
    } catch (error) {
      throw error;
    }
  };

  const register = async (userData: RegisterRequest): Promise<void> => {
    try {
      await apiService.register(userData);
      // After successful registration, user still needs to login
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const isAdmin = (): boolean => {
    return user?.role === 'ROLE_ADMIN';
  };

  const isStudent = (): boolean => {
    return user?.role === 'ROLE_USER' || user?.role === 'ROLE_STUDENT';
  };

  const value: AuthContextType = {
    user,
    token,
    isLoading,
    login,
    register,
    logout,
    isAdmin,
    isStudent,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};