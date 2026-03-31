import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  BuildingOfficeIcon,
  ClipboardDocumentListIcon,
  CheckCircleIcon,
  ClockIcon,
  XCircleIcon,
  BanknotesIcon,
  CalendarDaysIcon,
} from '@heroicons/react/24/outline';
import { apiService } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';

interface DashboardStats {
  totalApplications: number;
  pendingApplications: number;
  acceptedApplications: number;
  rejectedApplications: number;
  availableCompanies: number;
  upcomingDeadlines: number;
}

interface RecentApplication {
  applicationId: number;
  companyName: string;
  jobRole: string;
  status: string;
  appliedAt: string;
  packageOffered?: number;
}

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalApplications: 0,
    pendingApplications: 0,
    acceptedApplications: 0,
    rejectedApplications: 0,
    availableCompanies: 0,
    upcomingDeadlines: 0,
  });
  const [recentApplications, setRecentApplications] = useState<RecentApplication[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setIsLoading(true);
      const [applicationsData, companiesData] = await Promise.all([
        apiService.getMyApplications(),
        apiService.getCompanyVisits(),
      ]);

      // Calculate stats from applications
      const applications = applicationsData || [];
      const companies = companiesData || [];

      const dashboardStats: DashboardStats = {
        totalApplications: applications.length,
        pendingApplications: applications.filter((app: any) => app.status === 'PENDING').length,
        acceptedApplications: applications.filter((app: any) => app.status === 'ACCEPTED').length,
        rejectedApplications: applications.filter((app: any) => app.status === 'REJECTED').length,
        availableCompanies: companies.length,
        upcomingDeadlines: companies.filter((company: any) => {
          const deadline = new Date(company.applicationDeadline);
          const now = new Date();
          const diffTime = deadline.getTime() - now.getTime();
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          return diffDays > 0 && diffDays <= 7;
        }).length,
      };

      setStats(dashboardStats);
      setRecentApplications(applications.slice(0, 5));
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setIsLoading(false);
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

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="animate-pulse space-y-8">
            <div className="h-8 bg-gray-200 rounded w-1/4"></div>
            <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
              {[...Array(6)].map((_, i) => (
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
          <h1 className="text-2xl font-bold text-gray-900">
            Welcome back, {user?.username}!
          </h1>
          <p className="mt-2 text-gray-600">
            Here's what's happening with your placement journey
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 mb-8">
          <div className="bg-white overflow-hidden shadow rounded-lg">
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <ClipboardDocumentListIcon className="h-6 w-6 text-gray-400" />
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className="text-sm font-medium text-gray-500 truncate">
                      Total Applications
                    </dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {stats.totalApplications}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white overflow-hidden shadow rounded-lg">
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <ClockIcon className="h-6 w-6 text-yellow-400" />
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className="text-sm font-medium text-gray-500 truncate">
                      Pending Applications
                    </dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {stats.pendingApplications}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white overflow-hidden shadow rounded-lg">
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <CheckCircleIcon className="h-6 w-6 text-green-400" />
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className="text-sm font-medium text-gray-500 truncate">
                      Accepted Applications
                    </dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {stats.acceptedApplications}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white overflow-hidden shadow rounded-lg">
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <XCircleIcon className="h-6 w-6 text-red-400" />
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className="text-sm font-medium text-gray-500 truncate">
                      Rejected Applications
                    </dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {stats.rejectedApplications}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white overflow-hidden shadow rounded-lg">
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <BuildingOfficeIcon className="h-6 w-6 text-blue-400" />
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className="text-sm font-medium text-gray-500 truncate">
                      Available Companies
                    </dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {stats.availableCompanies}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white overflow-hidden shadow rounded-lg">
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <CalendarDaysIcon className="h-6 w-6 text-purple-400" />
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className="text-sm font-medium text-gray-500 truncate">
                      Upcoming Deadlines
                    </dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {stats.upcomingDeadlines}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white shadow rounded-lg mb-8">
          <div className="px-4 py-5 sm:p-6">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
              Quick Actions
            </h3>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
              <Link
                to="/companies"
                className="relative rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm flex items-center space-x-3 hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                <div className="flex-shrink-0">
                  <BuildingOfficeIcon className="h-6 w-6 text-primary-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <span className="absolute inset-0" aria-hidden="true" />
                  <p className="text-sm font-medium text-gray-900">Browse Companies</p>
                  <p className="text-sm text-gray-500">Find new opportunities</p>
                </div>
              </Link>

              <Link
                to="/applications"
                className="relative rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm flex items-center space-x-3 hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                <div className="flex-shrink-0">
                  <ClipboardDocumentListIcon className="h-6 w-6 text-primary-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <span className="absolute inset-0" aria-hidden="true" />
                  <p className="text-sm font-medium text-gray-900">View Applications</p>
                  <p className="text-sm text-gray-500">Track your progress</p>
                </div>
              </Link>

              <Link
                to="/profile"
                className="relative rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm flex items-center space-x-3 hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                <div className="flex-shrink-0">
                  <ClipboardDocumentListIcon className="h-6 w-6 text-primary-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <span className="absolute inset-0" aria-hidden="true" />
                  <p className="text-sm font-medium text-gray-900">Update Profile</p>
                  <p className="text-sm text-gray-500">Keep your info current</p>
                </div>
              </Link>
            </div>
          </div>
        </div>

        {/* Recent Applications */}
        {recentApplications.length > 0 && (
          <div className="bg-white shadow rounded-lg">
            <div className="px-4 py-5 sm:p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg leading-6 font-medium text-gray-900">
                  Recent Applications
                </h3>
                <Link
                  to="/applications"
                  className="text-sm font-medium text-primary-600 hover:text-primary-500"
                >
                  View all
                </Link>
              </div>
              <div className="flow-root">
                <ul className="-my-5 divide-y divide-gray-200">
                  {recentApplications.map((application) => (
                    <li key={application.applicationId} className="py-4">
                      <div className="flex items-center space-x-4">
                        <div className="flex-shrink-0">
                          <BuildingOfficeIcon className="h-6 w-6 text-gray-400" />
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-gray-900 truncate">
                            {application.companyName}
                          </p>
                          <p className="text-sm text-gray-500 truncate">
                            {application.jobRole}
                          </p>
                        </div>
                        <div className="flex-shrink-0">
                          <span
                            className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                              application.status
                            )}`}
                          >
                            {application.status}
                          </span>
                        </div>
                        {application.packageOffered && (
                          <div className="flex-shrink-0">
                            <div className="flex items-center text-sm text-gray-500">
                              <BanknotesIcon className="h-4 w-4 mr-1" />
                              ₹{application.packageOffered.toLocaleString()}
                            </div>
                          </div>
                        )}
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;