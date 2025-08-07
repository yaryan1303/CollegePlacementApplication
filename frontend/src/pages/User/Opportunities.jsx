import React, { useState, useEffect } from 'react';
import { userAPI } from '../../services/api';
import { Link } from 'react-router-dom';
import { 
  BriefcaseIcon, 
  CalendarIcon, 
  DollarSignIcon,
  BuildingIcon,
  ClockIcon,
  ExternalLinkIcon
} from 'lucide-react';

const Opportunities = () => {
  const [opportunities, setOpportunities] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOpportunities();
  }, []);

  const fetchOpportunities = async () => {
    try {
      const response = await userAPI.getActiveVisits();
      setOpportunities(response.data);
    } catch (error) {
      console.error('Error fetching opportunities:', error);
    } finally {
      setLoading(false);
    }
  };

  const isDeadlinePassed = (deadline) => {
    return new Date(deadline) < new Date();
  };

  const getDaysUntilDeadline = (deadline) => {
    const today = new Date();
    const deadlineDate = new Date(deadline);
    const diffTime = deadlineDate - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

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
        <h1 className="text-3xl font-bold text-gray-900">Job Opportunities</h1>
        <div className="text-sm text-gray-600">
          {opportunities.length} active opportunities
        </div>
      </div>

      {opportunities.length === 0 ? (
        <div className="text-center py-12">
          <BriefcaseIcon className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">No opportunities available</h3>
          <p className="mt-1 text-sm text-gray-500">Check back later for new job postings.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {opportunities.map((opportunity) => {
            const daysLeft = getDaysUntilDeadline(opportunity.applicationDeadline);
            const isExpired = isDeadlinePassed(opportunity.applicationDeadline);
            
            return (
              <div key={opportunity.visitId} className={`card p-6 ${isExpired ? 'opacity-60' : ''}`}>
                <div className="flex justify-between items-start mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="bg-primary-100 p-2 rounded-lg">
                      <BuildingIcon className="h-6 w-6 text-primary-600" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">
                        {opportunity.company.name}
                      </h3>
                      {opportunity.company.website && (
                        <a 
                          href={opportunity.company.website} 
                          target="_blank" 
                          rel="noopener noreferrer"
                          className="text-sm text-blue-600 hover:text-blue-800 flex items-center space-x-1"
                        >
                          <span>Visit Website</span>
                          <ExternalLinkIcon className="h-3 w-3" />
                        </a>
                      )}
                    </div>
                  </div>
                  
                  {!isExpired && (
                    <div className={`px-3 py-1 rounded-full text-xs font-medium ${
                      daysLeft <= 3 
                        ? 'bg-red-100 text-red-800' 
                        : daysLeft <= 7 
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-green-100 text-green-800'
                    }`}>
                      {daysLeft > 0 ? `${daysLeft} days left` : 'Last day!'}
                    </div>
                  )}
                  
                  {isExpired && (
                    <div className="px-3 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                      Expired
                    </div>
                  )}
                </div>

                <div className="space-y-3 mb-4">
                  <div className="flex items-center space-x-2 text-gray-600">
                    <BriefcaseIcon className="h-4 w-4" />
                    <span className="text-sm font-medium">{opportunity.jobPositions}</span>
                  </div>
                  
                  <div className="flex items-center space-x-2 text-gray-600">
                    <DollarSignIcon className="h-4 w-4" />
                    <span className="text-sm">{opportunity.salaryPackage}</span>
                  </div>
                  
                  <div className="flex items-center space-x-2 text-gray-600">
                    <CalendarIcon className="h-4 w-4" />
                    <span className="text-sm">
                      Visit Date: {new Date(opportunity.visitDate).toLocaleDateString()}
                    </span>
                  </div>
                  
                  <div className="flex items-center space-x-2 text-gray-600">
                    <ClockIcon className="h-4 w-4" />
                    <span className="text-sm">
                      Apply by: {new Date(opportunity.applicationDeadline).toLocaleDateString()}
                    </span>
                  </div>
                </div>

                <div className="bg-gray-50 p-3 rounded-lg mb-4">
                  <p className="text-sm text-gray-700">
                    <span className="font-medium">Eligibility:</span> {opportunity.eligibilityCriteria}
                  </p>
                  <p className="text-sm text-gray-700 mt-1">
                    <span className="font-medium">Batch:</span> {opportunity.batchYear}
                  </p>
                </div>

                <div className="flex justify-end">
                  <Link
                    to={`/opportunities/${opportunity.visitId}`}
                    className={`btn-primary ${isExpired ? 'opacity-50 cursor-not-allowed' : ''}`}
                    onClick={isExpired ? (e) => e.preventDefault() : undefined}
                  >
                    {isExpired ? 'Application Closed' : 'View Details & Apply'}
                  </Link>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default Opportunities;