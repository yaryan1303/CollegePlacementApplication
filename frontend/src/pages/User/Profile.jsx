import React, { useState, useEffect } from 'react';
import { userAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { UserIcon, SaveIcon } from 'lucide-react';

const Profile = () => {
  const { user } = useAuth();
  const [departments, setDepartments] = useState([]);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    rollNumber: '',
    batchYear: '',
    departmentId: '',
    cgpa: '',
    resumeUrl: '',
    phoneNumber: '',
    currentStatus: 'NOT_PLACED'
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      const response = await userAPI.getAllDepartments();
      setDepartments(response.data);
    } catch (error) {
      console.error('Error fetching departments:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const dataToSubmit = {
        ...formData,
        userId: user.userId,
        departmentId: parseInt(formData.departmentId),
        batchYear: parseInt(formData.batchYear),
        cgpa: parseFloat(formData.cgpa)
      };

      await userAPI.saveStudentDetails(dataToSubmit);
      setSuccess('Profile saved successfully!');
    } catch (error) {
      setError(error.response?.data?.message || 'Error saving profile. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center space-x-3">
        <UserIcon className="h-8 w-8 text-primary-600" />
        <h1 className="text-3xl font-bold text-gray-900">Student Profile</h1>
      </div>

      <div className="card p-6">
        {success && (
          <div className="mb-4 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
            {success}
          </div>
        )}

        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium text-gray-700">First Name</label>
              <input
                type="text"
                name="firstName"
                required
                className="input-field mt-1"
                value={formData.firstName}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Last Name</label>
              <input
                type="text"
                name="lastName"
                className="input-field mt-1"
                value={formData.lastName}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Roll Number</label>
              <input
                type="text"
                name="rollNumber"
                required
                className="input-field mt-1"
                value={formData.rollNumber}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Batch Year</label>
              <input
                type="number"
                name="batchYear"
                required
                className="input-field mt-1"
                value={formData.batchYear}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Department</label>
              <select
                name="departmentId"
                required
                className="input-field mt-1"
                value={formData.departmentId}
                onChange={handleChange}
              >
                <option value="">Select Department</option>
                {departments.map(dept => (
                  <option key={dept.id} value={dept.id}>{dept.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">CGPA</label>
              <input
                type="number"
                name="cgpa"
                step="0.01"
                min="0"
                max="10"
                required
                className="input-field mt-1"
                value={formData.cgpa}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Phone Number</label>
              <input
                type="tel"
                name="phoneNumber"
                required
                className="input-field mt-1"
                value={formData.phoneNumber}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Current Status</label>
              <select
                name="currentStatus"
                className="input-field mt-1"
                value={formData.currentStatus}
                onChange={handleChange}
              >
                <option value="NOT_PLACED">Not Placed</option>
                <option value="PLACED">Placed</option>
                <option value="PENDING">Pending</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Resume URL</label>
            <input
              type="url"
              name="resumeUrl"
              className="input-field mt-1"
              placeholder="https://drive.google.com/..."
              value={formData.resumeUrl}
              onChange={handleChange}
            />
            <p className="mt-1 text-sm text-gray-500">
              Upload your resume to Google Drive or similar service and paste the public link here.
            </p>
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="btn-primary flex items-center space-x-2 disabled:opacity-50"
            >
              <SaveIcon className="h-4 w-4" />
              <span>{loading ? 'Saving...' : 'Save Profile'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Profile;