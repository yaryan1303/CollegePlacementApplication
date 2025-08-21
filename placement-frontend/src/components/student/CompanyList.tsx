import React, { useState, useEffect } from 'react';
import {
  BuildingOfficeIcon,
  CalendarIcon,
  CurrencyRupeeIcon,
  GlobeAltIcon,
  MagnifyingGlassIcon,
  CheckCircleIcon,
} from '@heroicons/react/24/outline';
import { apiService } from '../../services/api';

interface Company {
  companyId: number;
  name: string;
  description: string;
  website: string;
  contactEmail: string;
  contactPhone: string;
}

interface CompanyVisit {
  visitId: number;
  company: Company;
  visitDate: string;
  jobRole: string;
  eligibilityCriteria: string;
  packageOffered: number;
  applicationDeadline: string;
  status: string;
  isApplied?: boolean;
}

const CompanyList: React.FC = () => {
  const [companyVisits, setCompanyVisits] = useState<CompanyVisit[]>([]);
  const [filteredVisits, setFilteredVisits] = useState<CompanyVisit[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState<'name' | 'package' | 'deadline'>('name');
  const [filterBy, setFilterBy] = useState<'all' | 'active' | 'applied'>('all');
  const [applying, setApplying] = useState<number | null>(null);

  useEffect(() => {
    fetchCompanyVisits();
  }, []);

  useEffect(() => {
    filterAndSortData();
  }, [companyVisits, searchTerm, sortBy, filterBy]);

  const fetchCompanyVisits = async () => {
    try {
      setIsLoading(true);
      const [visitsData, applicationsData] = await Promise.all([
        apiService.getCompanyVisits(),
        apiService.getMyApplications(),
      ]);

      const appliedVisitIds = new Set(
        applicationsData.map((app: any) => app.companyVisit.visitId)
      );

      const visitsWithApplicationStatus = visitsData.map((visit: CompanyVisit) => ({
        ...visit,
        isApplied: appliedVisitIds.has(visit.visitId),
      }));

      setCompanyVisits(visitsWithApplicationStatus);
    } catch (error) {
      console.error('Error fetching company visits:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const filterAndSortData = () => {
    let filtered = [...companyVisits];

    // Search filter
    if (searchTerm) {
      filtered = filtered.filter(
        (visit) =>
          visit.company.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          visit.jobRole.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Status filter
    if (filterBy === 'active') {
      const now = new Date();
      filtered = filtered.filter(
        (visit) => new Date(visit.applicationDeadline) > now && !visit.isApplied
      );
    } else if (filterBy === 'applied') {
      filtered = filtered.filter((visit) => visit.isApplied);
    }

    // Sort
    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'name':
          return a.company.name.localeCompare(b.company.name);
        case 'package':
          return b.packageOffered - a.packageOffered;
        case 'deadline':
          return new Date(a.applicationDeadline).getTime() - new Date(b.applicationDeadline).getTime();
        default:
          return 0;
      }
    });

    setFilteredVisits(filtered);
  };

  const handleApply = async (visitId: number) => {
    try {
      setApplying(visitId);
      await apiService.applyToCompany(visitId);
      
      // Update the local state to reflect the application
      setCompanyVisits((prev) =>
        prev.map((visit) =>
          visit.visitId === visitId ? { ...visit, isApplied: true } : visit
        )
      );
    } catch (error) {
      console.error('Error applying to company:', error);
      alert('Failed to apply. Please try again.');
    } finally {
      setApplying(null);
    }
  };

  const isDeadlinePassed = (deadline: string) => {
    return new Date(deadline) < new Date();
  };

  const getDaysUntilDeadline = (deadline: string) => {
    const deadlineDate = new Date(deadline);
    const now = new Date();
    const diffTime = deadlineDate.getTime() - now.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="animate-pulse space-y-8">
            <div className="h-8 bg-gray-200 rounded w-1/4"></div>
            <div className="space-y-4">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="h-48 bg-gray-200 rounded-lg"></div>
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
          <h1 className="text-2xl font-bold text-gray-900">Company Opportunities</h1>
          <p className="mt-2 text-gray-600">
            Explore available job opportunities and apply to companies
          </p>
        </div>

        {/* Filters and Search */}
        <div className="bg-white shadow rounded-lg p-6 mb-6">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-4">
            <div>
              <label htmlFor="search" className="block text-sm font-medium text-gray-700">
                Search
              </label>
              <div className="mt-1 relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center">
                  <MagnifyingGlassIcon className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  type="text"
                  id="search"
                  className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500"
                  placeholder="Search companies or roles..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>

            <div>
              <label htmlFor="sort" className="block text-sm font-medium text-gray-700">
                Sort by
              </label>
              <select
                id="sort"
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500"
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as any)}
              >
                <option value="name">Company Name</option>
                <option value="package">Package (High to Low)</option>
                <option value="deadline">Application Deadline</option>
              </select>
            </div>

            <div>
              <label htmlFor="filter" className="block text-sm font-medium text-gray-700">
                Filter
              </label>
              <select
                id="filter"
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500"
                value={filterBy}
                onChange={(e) => setFilterBy(e.target.value as any)}
              >
                <option value="all">All Companies</option>
                <option value="active">Available to Apply</option>
                <option value="applied">Already Applied</option>
              </select>
            </div>

            <div className="flex items-end">
              <div className="text-sm text-gray-500">
                {filteredVisits.length} companies found
              </div>
            </div>
          </div>
        </div>

        {/* Company Cards */}
        <div className="space-y-6">
          {filteredVisits.map((visit) => (
            <div
              key={visit.visitId}
              className="bg-white shadow rounded-lg overflow-hidden"
            >
              <div className="px-6 py-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <BuildingOfficeIcon className="h-10 w-10 text-gray-400" />
                    </div>
                    <div className="ml-4">
                      <h3 className="text-lg font-medium text-gray-900">
                        {visit.company.name}
                      </h3>
                      <p className="text-sm text-gray-500">{visit.jobRole}</p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-4">
                    <div className="text-right">
                      <div className="flex items-center text-sm text-gray-900">
                        <CurrencyRupeeIcon className="h-4 w-4 mr-1" />
                        {visit.packageOffered.toLocaleString()} LPA
                      </div>
                      <div className="flex items-center text-sm text-gray-500 mt-1">
                        <CalendarIcon className="h-4 w-4 mr-1" />
                        {getDaysUntilDeadline(visit.applicationDeadline) > 0
                          ? `${getDaysUntilDeadline(visit.applicationDeadline)} days left`
                          : 'Deadline passed'}
                      </div>
                    </div>
                    {visit.isApplied ? (
                      <div className="flex items-center px-3 py-2 rounded-md bg-green-100 text-green-800">
                        <CheckCircleIcon className="h-4 w-4 mr-1" />
                        Applied
                      </div>
                    ) : (
                      <button
                        onClick={() => handleApply(visit.visitId)}
                        disabled={
                          isDeadlinePassed(visit.applicationDeadline) ||
                          applying === visit.visitId
                        }
                        className="px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {applying === visit.visitId ? 'Applying...' : 'Apply Now'}
                      </button>
                    )}
                  </div>
                </div>

                <div className="mt-4">
                  <p className="text-sm text-gray-600 mb-3">
                    {visit.company.description}
                  </p>
                  
                  <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                      <h4 className="text-sm font-medium text-gray-900 mb-2">
                        Eligibility Criteria
                      </h4>
                      <p className="text-sm text-gray-600">
                        {visit.eligibilityCriteria}
                      </p>
                    </div>
                    
                    <div>
                      <h4 className="text-sm font-medium text-gray-900 mb-2">
                        Visit Details
                      </h4>
                      <div className="space-y-1">
                        <div className="flex items-center text-sm text-gray-600">
                          <CalendarIcon className="h-4 w-4 mr-2" />
                          Visit Date: {new Date(visit.visitDate).toLocaleDateString()}
                        </div>
                        {visit.company.website && (
                          <div className="flex items-center text-sm text-gray-600">
                            <GlobeAltIcon className="h-4 w-4 mr-2" />
                            <a
                              href={visit.company.website}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-primary-600 hover:text-primary-500"
                            >
                              Company Website
                            </a>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}

          {filteredVisits.length === 0 && (
            <div className="text-center py-12">
              <BuildingOfficeIcon className="mx-auto h-12 w-12 text-gray-400" />
              <h3 className="mt-2 text-sm font-medium text-gray-900">No companies found</h3>
              <p className="mt-1 text-sm text-gray-500">
                Try adjusting your search or filter criteria.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CompanyList;