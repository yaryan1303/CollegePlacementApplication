import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  UserIcon,
  AcademicCapIcon,
  PhoneIcon,
  DocumentIcon,
  PencilIcon,
} from '@heroicons/react/24/outline';
import { apiService } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';

const profileSchema = yup.object({
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  rollNumber: yup.string().required('Roll number is required'),
  phoneNumber: yup.string().required('Phone number is required'),
  batchYear: yup.number().required('Batch year is required').min(2000).max(2030),
  cgpa: yup.number().required('CGPA is required').min(0).max(10),
  resumeUrl: yup.string().url('Please enter a valid URL'),
});

interface ProfileFormData {
  firstName: string;
  lastName: string;
  rollNumber: string;
  phoneNumber: string;
  batchYear: number;
  cgpa: number;
  resumeUrl?: string;
  departmentId: number;
}

interface Department {
  id: number;
  name: string;
  code: string;
}

const Profile: React.FC = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [profile, setProfile] = useState<any>(null);
  const { user } = useAuth();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ProfileFormData>({
    resolver: yupResolver(profileSchema),
  });

  useEffect(() => {
    fetchProfileData();
  }, []);

  const fetchProfileData = async () => {
    try {
      setIsLoading(true);
      const [profileData, departmentsData] = await Promise.all([
        apiService.getCurrentUser(),
        apiService.getDepartments(),
      ]);

      setProfile(profileData);
      setDepartments(departmentsData || []);

      if (profileData?.studentDetails) {
        reset({
          firstName: profileData.studentDetails.firstName || '',
          lastName: profileData.studentDetails.lastName || '',
          rollNumber: profileData.studentDetails.rollNumber || '',
          phoneNumber: profileData.studentDetails.phoneNumber || '',
          batchYear: profileData.studentDetails.batchYear || new Date().getFullYear(),
          cgpa: profileData.studentDetails.cgpa || 0,
          resumeUrl: profileData.studentDetails.resumeUrl || '',
          departmentId: profileData.studentDetails.department?.id || 1,
        });
      }
    } catch (error) {
      console.error('Error fetching profile data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const onSubmit = async (data: ProfileFormData) => {
    try {
      setIsSaving(true);
      await apiService.updateStudentDetails(data);
      await fetchProfileData(); // Refresh data
      setIsEditing(false);
    } catch (error) {
      console.error('Error updating profile:', error);
      alert('Failed to update profile. Please try again.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    if (profile?.studentDetails) {
      reset({
        firstName: profile.studentDetails.firstName || '',
        lastName: profile.studentDetails.lastName || '',
        rollNumber: profile.studentDetails.rollNumber || '',
        phoneNumber: profile.studentDetails.phoneNumber || '',
        batchYear: profile.studentDetails.batchYear || new Date().getFullYear(),
        cgpa: profile.studentDetails.cgpa || 0,
        resumeUrl: profile.studentDetails.resumeUrl || '',
        departmentId: profile.studentDetails.department?.id || 1,
      });
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="animate-pulse space-y-8">
            <div className="h-8 bg-gray-200 rounded w-1/4"></div>
            <div className="h-64 bg-gray-200 rounded-lg"></div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Profile</h1>
          <p className="mt-2 text-gray-600">
            Manage your personal information and academic details
          </p>
        </div>

        <div className="bg-white shadow rounded-lg">
          <div className="px-6 py-4 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-medium text-gray-900">Personal Information</h2>
              {!isEditing && (
                <button
                  onClick={() => setIsEditing(true)}
                  className="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
                >
                  <PencilIcon className="h-4 w-4 mr-1" />
                  Edit
                </button>
              )}
            </div>
          </div>

          <div className="px-6 py-6">
            {/* Account Information */}
            <div className="mb-8">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Account Details</h3>
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Username</label>
                  <div className="mt-1 p-3 border border-gray-200 rounded-md bg-gray-50">
                    {user?.username}
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Email</label>
                  <div className="mt-1 p-3 border border-gray-200 rounded-md bg-gray-50">
                    {user?.email}
                  </div>
                </div>
              </div>
            </div>

            {/* Student Details Form */}
            <form onSubmit={handleSubmit(onSubmit)}>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Student Details</h3>
              
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                <div>
                  <label htmlFor="firstName" className="block text-sm font-medium text-gray-700">
                    First Name
                  </label>
                  <div className="mt-1 relative">
                    <input
                      {...register('firstName')}
                      type="text"
                      disabled={!isEditing}
                      className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                        !isEditing ? 'bg-gray-50 text-gray-500' : ''
                      } ${errors.firstName ? 'border-red-300' : 'border-gray-300'}`}
                    />
                    {!isEditing && (
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center">
                        <UserIcon className="h-5 w-5 text-gray-400" />
                      </div>
                    )}
                  </div>
                  {errors.firstName && (
                    <p className="mt-1 text-sm text-red-600">{errors.firstName.message}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="lastName" className="block text-sm font-medium text-gray-700">
                    Last Name
                  </label>
                  <input
                    {...register('lastName')}
                    type="text"
                    disabled={!isEditing}
                    className={`mt-1 block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                      !isEditing ? 'bg-gray-50 text-gray-500' : ''
                    } ${errors.lastName ? 'border-red-300' : 'border-gray-300'}`}
                  />
                  {errors.lastName && (
                    <p className="mt-1 text-sm text-red-600">{errors.lastName.message}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="rollNumber" className="block text-sm font-medium text-gray-700">
                    Roll Number
                  </label>
                  <input
                    {...register('rollNumber')}
                    type="text"
                    disabled={!isEditing}
                    className={`mt-1 block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                      !isEditing ? 'bg-gray-50 text-gray-500' : ''
                    } ${errors.rollNumber ? 'border-red-300' : 'border-gray-300'}`}
                  />
                  {errors.rollNumber && (
                    <p className="mt-1 text-sm text-red-600">{errors.rollNumber.message}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700">
                    Phone Number
                  </label>
                  <div className="mt-1 relative">
                    <input
                      {...register('phoneNumber')}
                      type="tel"
                      disabled={!isEditing}
                      className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                        !isEditing ? 'bg-gray-50 text-gray-500' : ''
                      } ${errors.phoneNumber ? 'border-red-300' : 'border-gray-300'}`}
                    />
                    {!isEditing && (
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center">
                        <PhoneIcon className="h-5 w-5 text-gray-400" />
                      </div>
                    )}
                  </div>
                  {errors.phoneNumber && (
                    <p className="mt-1 text-sm text-red-600">{errors.phoneNumber.message}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="batchYear" className="block text-sm font-medium text-gray-700">
                    Batch Year
                  </label>
                  <input
                    {...register('batchYear')}
                    type="number"
                    disabled={!isEditing}
                    className={`mt-1 block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                      !isEditing ? 'bg-gray-50 text-gray-500' : ''
                    } ${errors.batchYear ? 'border-red-300' : 'border-gray-300'}`}
                  />
                  {errors.batchYear && (
                    <p className="mt-1 text-sm text-red-600">{errors.batchYear.message}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="cgpa" className="block text-sm font-medium text-gray-700">
                    CGPA
                  </label>
                  <div className="mt-1 relative">
                    <input
                      {...register('cgpa')}
                      type="number"
                      step="0.01"
                      min="0"
                      max="10"
                      disabled={!isEditing}
                      className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                        !isEditing ? 'bg-gray-50 text-gray-500' : ''
                      } ${errors.cgpa ? 'border-red-300' : 'border-gray-300'}`}
                    />
                    {!isEditing && (
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center">
                        <AcademicCapIcon className="h-5 w-5 text-gray-400" />
                      </div>
                    )}
                  </div>
                  {errors.cgpa && (
                    <p className="mt-1 text-sm text-red-600">{errors.cgpa.message}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="departmentId" className="block text-sm font-medium text-gray-700">
                    Department
                  </label>
                  <select
                    {...register('departmentId')}
                    disabled={!isEditing}
                    className={`mt-1 block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                      !isEditing ? 'bg-gray-50 text-gray-500' : ''
                    } ${errors.departmentId ? 'border-red-300' : 'border-gray-300'}`}
                  >
                    {departments.map((dept) => (
                      <option key={dept.id} value={dept.id}>
                        {dept.name} ({dept.code})
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label htmlFor="resumeUrl" className="block text-sm font-medium text-gray-700">
                    Resume URL
                  </label>
                  <div className="mt-1 relative">
                    <input
                      {...register('resumeUrl')}
                      type="url"
                      disabled={!isEditing}
                      placeholder="https://drive.google.com/..."
                      className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${
                        !isEditing ? 'bg-gray-50 text-gray-500' : ''
                      } ${errors.resumeUrl ? 'border-red-300' : 'border-gray-300'}`}
                    />
                    {!isEditing && (
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center">
                        <DocumentIcon className="h-5 w-5 text-gray-400" />
                      </div>
                    )}
                  </div>
                  {errors.resumeUrl && (
                    <p className="mt-1 text-sm text-red-600">{errors.resumeUrl.message}</p>
                  )}
                </div>
              </div>

              {isEditing && (
                <div className="mt-8 flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={handleCancel}
                    className="px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={isSaving}
                    className="px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
                  >
                    {isSaving ? 'Saving...' : 'Save Changes'}
                  </button>
                </div>
              )}
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;