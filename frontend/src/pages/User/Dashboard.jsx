import React, { useState, useEffect } from 'react';
import { userAPI } from '../../services/api';
import { 
  BriefcaseIcon, 
  ClipboardListIcon, 
  TrophyIcon,
  CalendarIcon,
  UserIcon,
  CheckCircleIcon
} from 'lucide-react';
import { Link } from 'react-router-dom';

const Dashboard = () => {
  const [applications, setApplications] = useState([]);
  const [activeVisits, setActiveVisits] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [applicationsRes, visitsRes] = await Promise.all([
        userAPI.getAllApplications(),
        userAPI.getActiveVisits()
      ]);
      
      setApplications(applicationsRes.data);
      setActiveVisits(visitsRes.data);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const statusColors = {
      SELECTED: 'bg-green-100 text-green-800',
      REJECTED: 'bg-red-100 text-red-800',
      PENDING: 'bg-yellow-100 text-yellow-800'
    };
    
    return (
      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${statusColors[status] || 'bg-gray-100 text-gray-800'}`}>
        {status}
      </span>
    );
  };

  const quickStats = [
    {
      title: 'Total Applications',
      value: applications.length,
      icon: ClipboardListIcon,
      color: 'bg-blue-500',
      link: '/applications'
    },
    {
      title: 'Selected',
      value: applications.filter(app => app.applicationStatus === 'SELECTED').length,
      icon: CheckCircleIcon,
      color: 'bg-green-500',
      link: '/applications'
    },
    {
      title: 'Pending',
      value: applications.filter(app => app.applicationStatus === 'PENDING').length,
      icon: CalendarIcon,
      color: 'bg-yellow-500',
      link: '/applications'
    },
    {
      title: 'Active Opportunities',
      value: activeVisits.length,
      icon: BriefcaseIcon,
      color: 'bg-purple-500',
      link: '/opportunities'
    },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Student Dashboard</h1>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {quickStats.map((stat, index) => (
          <Link key={index} to={stat.link} className="card p-6 hover:shadow-lg transition-shadow">
            <div className="flex items-center">
              <div className={`${stat.color} p-3 rounded-lg`}>
                <stat.icon className="h-6 w-6 text-white" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Applications */}
        <div className="card p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold text-gray-900">Recent Applications</h2>
            <Link to="/applications" className="text-primary-600 hover:text-primary-800 text-sm font-medium">
              View All
            </Link>
          </div>
          
          <div className="space-y-4">
            {applications.slice(0, 5).map((application) => (
              <div key={application.applicationId} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <p className="font-medium text-gray-900">{application.company.name}</p>
                  <p className="text-sm text-gray-600">{application.jobPositions}</p>
                  <p className="text-xs text-gray-500">Applied: {new Date(application.applicationDate).toLocaleDateString()}</p>
                </div>
                <div>
                  {getStatusBadge(application.applicationStatus)}
                </div>
              </div>
            ))}
            
            {applications.length === 0 && (
              <p className="text-gray-500 text-center py-4">No applications yet. Start applying to companies!</p>
            )}
          </div>
        </div>

        {/* Active Opportunities */}
        <div className="card p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold text-gray-900">Active Opportunities</h2>
            <Link to="/opportunities" className="text-primary-600 hover:text-primary-800 text-sm font-medium">
              View All
            </Link>
          </div>
          
          <div className="space-y-4">
            {activeVisits.slice(0, 5).map((visit) => (
              <div key={visit.visitId} className="p-3 bg-gray-50 rounded-lg">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-medium text-gray-900">{visit.company.name}</p>
                    <p className="text-sm text-gray-600">{visit.jobPositions}</p>
                    <p className="text-sm text-gray-600">Package: {visit.salaryPackage}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-gray-500">Visit: {new Date(visit.visitDate).toLocaleDateString()}</p>
                    <p className="text-xs text-gray-500">Deadline: {new Date(visit.applicationDeadline).toLocaleDateString()}</p>
                  </div>
                </div>
                <div className="mt-2">
                  <Link 
                    to={`/opportunities/${visit.visitId}`}
                    className="text-primary-600 hover:text-primary-800 text-sm font-medium"
                  >
                    View Details →
                  </Link>
                </div>
              </div>
            ))}
            
            {activeVisits.length === 0 && (
              <p className="text-gray-500 text-center py-4">No active opportunities at the moment.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;