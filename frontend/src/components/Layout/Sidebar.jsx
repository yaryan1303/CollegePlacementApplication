import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { 
  HomeIcon,
  UsersIcon,
  BuildingIcon,
  CalendarIcon,
  FileTextIcon,
  BarChartIcon,
  UserIcon,
  BriefcaseIcon,
  ClipboardListIcon,
  TrophyIcon,
  SettingsIcon
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const Sidebar = () => {
  const location = useLocation();
  const { isAdmin } = useAuth();

  const adminMenuItems = [
    { name: 'Dashboard', href: '/admin', icon: HomeIcon },
    { name: 'Students', href: '/admin/students', icon: UsersIcon },
    { name: 'Companies', href: '/admin/companies', icon: BuildingIcon },
    { name: 'Company Visits', href: '/admin/visits', icon: CalendarIcon },
    { name: 'Applications', href: '/admin/applications', icon: FileTextIcon },
    { name: 'Reports', href: '/admin/reports', icon: BarChartIcon },
    { name: 'Departments', href: '/admin/departments', icon: SettingsIcon },
  ];

  const userMenuItems = [
    { name: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { name: 'Profile', href: '/profile', icon: UserIcon },
    { name: 'Job Opportunities', href: '/opportunities', icon: BriefcaseIcon },
    { name: 'My Applications', href: '/applications', icon: ClipboardListIcon },
    { name: 'Placement Records', href: '/placements', icon: TrophyIcon },
  ];

  const menuItems = isAdmin ? adminMenuItems : userMenuItems;

  return (
    <div className="bg-gray-900 text-white w-64 min-h-screen p-4">
      <div className="space-y-2">
        {menuItems.map((item) => {
          const isActive = location.pathname === item.href;
          return (
            <Link
              key={item.name}
              to={item.href}
              className={`flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${
                isActive
                  ? 'bg-primary-600 text-white'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              }`}
            >
              <item.icon className="h-5 w-5" />
              <span className="font-medium">{item.name}</span>
            </Link>
          );
        })}
      </div>
    </div>
  );
};

export default Sidebar;