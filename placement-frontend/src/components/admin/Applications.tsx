import React from 'react';
import { ClipboardDocumentListIcon } from '@heroicons/react/24/outline';

const AdminApplications: React.FC = () => {
  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Applications Management</h1>
          <p className="mt-2 text-gray-600">
            Review and manage student applications
          </p>
        </div>

        <div className="text-center py-12">
          <ClipboardDocumentListIcon className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">Applications Management</h3>
          <p className="mt-1 text-sm text-gray-500">
            This section will allow admins to review and update application statuses.
          </p>
        </div>
      </div>
    </div>
  );
};

export default AdminApplications;