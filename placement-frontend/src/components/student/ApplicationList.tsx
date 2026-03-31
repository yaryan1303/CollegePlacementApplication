import React, { useState, useEffect } from 'react';
import {
  BuildingOfficeIcon,
  CalendarIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  EyeIcon,
} from '@heroicons/react/24/outline';
import { apiService } from '../../services/api';

interface Application {
  applicationId: number;
  companyVisit: {
    visitId: number;
    company: {
      name: string;
      website?: string;
    };
    jobRole: string;
    packageOffered: number;
    visitDate: string;
  };
  status: string;
  appliedAt: string;
  updatedAt: string;
}

const ApplicationList: React.FC = () => {
  const [applications, setApplications] = useState<Application[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [filter, setFilter] = useState<'all' | 'pending' | 'accepted' | 'rejected'>('all');

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      setIsLoading(true);
      const data = await apiService.getMyApplications();
      setApplications(data || []);
    } catch (error) {
      console.error('Error fetching applications:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ACCEPTED':
        return <CheckCircleIcon className="h-5 w-5 text-green-500" />;
      case 'REJECTED':
        return <XCircleIcon className="h-5 w-5 text-red-500" />;
      case 'SHORTLISTED':
        return <EyeIcon className="h-5 w-5 text-blue-500" />;
      default:
        return <ClockIcon className="h-5 w-5 text-yellow-500" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACCEPTED':
        return 'text-green-600 bg-green-100';
      case 'REJECTED':
        return 'text-red-600 bg-red-100';
      case 'SHORTLISTED':
        return 'text-blue-600 bg-blue-100';
      default:
        return 'text-yellow-600 bg-yellow-100';
    }
  };

  const filteredApplications = applications.filter((app) => {
    if (filter === 'all') return true;
    return app.status.toLowerCase() === filter;
  });

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="animate-pulse space-y-8">
            <div className="h-8 bg-gray-200 rounded w-1/4"></div>
            <div className="space-y-4">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="h-32 bg-gray-200 rounded-lg"></div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">My Applications</h1>
          <p className="mt-2 text-gray-600">
            Track the status of your job applications
          </p>
        </div>

        {/* Filter Tabs */}
        <div className="mb-6">
          <nav className="flex space-x-8">
            {[
              { key: 'all', label: 'All Applications', count: applications.length },
              { key: 'pending', label: 'Pending', count: applications.filter(app => app.status === 'PENDING').length },
              { key: 'accepted', label: 'Accepted', count: applications.filter(app => app.status === 'ACCEPTED').length },
              { key: 'rejected', label: 'Rejected', count: applications.filter(app => app.status === 'REJECTED').length },
            ].map((tab) => (
              <button
                key={tab.key}
                onClick={() => setFilter(tab.key as any)}
                className={`${
                  filter === tab.key
                    ? 'border-primary-500 text-primary-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                } whitespace-nowrap py-2 px-1 border-b-2 font-medium text-sm`}
              >
                {tab.label}
                <span
                  className={`${
                    filter === tab.key
                      ? 'bg-primary-100 text-primary-600'
                      : 'bg-gray-100 text-gray-900'
                  } ml-2 py-0.5 px-2.5 rounded-full text-xs font-medium`}
                >
                  {tab.count}
                </span>
              </button>
            ))}
          </nav>
        </div>

        {/* Applications List */}
        <div className="bg-white shadow rounded-lg">
          {filteredApplications.length === 0 ? (
            <div className="text-center py-12">
              <BuildingOfficeIcon className="mx-auto h-12 w-12 text-gray-400" />
              <h3 className="mt-2 text-sm font-medium text-gray-900">No applications found</h3>
              <p className="mt-1 text-sm text-gray-500">
                {filter === 'all'
                  ? "You haven't applied to any companies yet."
                  : `No ${filter} applications found.`}
              </p>
            </div>
          ) : (
            <ul className="divide-y divide-gray-200">
              {filteredApplications.map((application) => (
                <li key={application.applicationId} className="px-6 py-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div className="flex-shrink-0">
                        <BuildingOfficeIcon className="h-10 w-10 text-gray-400" />
                      </div>
                      <div className="ml-4">
                        <div className="flex items-center">
                          <h3 className="text-lg font-medium text-gray-900">
                            {application.companyVisit.company.name}
                          </h3>
                          {application.companyVisit.company.website && (
                            <a
                              href={application.companyVisit.company.website}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="ml-2 text-primary-600 hover:text-primary-500"
                            >
                              <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
                                <path
                                  fillRule="evenodd"
                                  d="M4.5 3A1.5 1.5 0 003 4.5v11A1.5 1.5 0 004.5 17h11a1.5 1.5 0 001.5-1.5v-6a.5.5 0 00-1 0v6a.5.5 0 01-.5.5h-11a.5.5 0 01-.5-.5v-11a.5.5 0 01.5-.5H10a.5.5 0 000-1H4.5z"
                                  clipRule="evenodd"
                                />
                                <path
                                  fillRule="evenodd"
                                  d="M8.636 10.364a.5.5 0 00.708.708l3-3a.5.5 0 00-.708-.708L9 9.293V6.5a.5.5 0 00-1 0v3.5a.5.5 0 00.146.354l1.49 1.49z"
                                  clipRule="evenodd"
                                />
                              </svg>
                            </a>
                          )}
                        </div>
                        <p className="text-sm text-gray-500">{application.companyVisit.jobRole}</p>
                        <div className="mt-2 flex items-center text-sm text-gray-500">
                          <CalendarIcon className="h-4 w-4 mr-1" />
                          Applied on {new Date(application.appliedAt).toLocaleDateString()}
                        </div>
                      </div>
                    </div>
                    
                    <div className="flex items-center space-x-4">
                      <div className="text-right">
                        <div className="text-lg font-medium text-gray-900">
                          ₹{application.companyVisit.packageOffered.toLocaleString()} LPA
                        </div>
                        <div className="text-sm text-gray-500">
                          Visit: {new Date(application.companyVisit.visitDate).toLocaleDateString()}
                        </div>
                      </div>
                      
                      <div className="flex items-center">
                        {getStatusIcon(application.status)}
                        <span
                          className={`ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                            application.status
                          )}`}
                        >
                          {application.status}
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  {application.status !== 'PENDING' && (
                    <div className="mt-2 text-sm text-gray-500">
                      Last updated: {new Date(application.updatedAt).toLocaleDateString()}
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
};

export default ApplicationList;