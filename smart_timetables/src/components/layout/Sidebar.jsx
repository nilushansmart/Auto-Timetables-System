import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import {
  CalendarDays,
  LayoutGrid,
  Clock,
  Users,
  GraduationCap,
  BookOpen,
  DoorOpen,
  Layers,
  Calendar,
  X,
} from 'lucide-react';

export const Sidebar = ({ isOpen, onClose }) => {
  const { hasRole } = useAuth();

  const navItems = [
    {
      label: 'Timetable Viewer',
      to: '/timetable',
      icon: CalendarDays,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER'],
    },
    {
      label: 'Dashboard',
      to: '/dashboard',
      icon: LayoutGrid,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER'],
    },
    {
      label: 'Time Grid Setup',
      to: '/time-grid',
      icon: Clock,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR'],
    },
    {
      label: 'Teachers',
      to: '/teachers',
      icon: Users,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER'],
    },
    {
      label: 'Classes',
      to: '/classes',
      icon: GraduationCap,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER'],
    },
    {
      label: 'Subjects',
      to: '/subjects',
      icon: BookOpen,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER'],
    },
    {
      label: 'Rooms & Labs',
      to: '/rooms',
      icon: DoorOpen,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER'],
    },
    {
      label: 'Lessons & Groups',
      to: '/lessons',
      icon: Layers,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR'],
    },
    {
      label: 'Academic Years',
      to: '/academic-years',
      icon: Calendar,
      roles: ['SCHOOL_ADMIN', 'COORDINATOR'],
    },
  ];

  const filteredNavItems = navItems.filter((item) => hasRole(item.roles));

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/40 backdrop-blur-xs lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar Panel */}
      <aside
        className={`fixed inset-y-0 left-0 z-40 w-64 border-r border-slate-200 bg-white transition-transform duration-200 ease-in-out lg:static lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-16 items-center justify-between px-6 border-b border-slate-200 lg:hidden">
          <span className="font-bold text-slate-900 text-sm">Navigation</span>
          <button
            type="button"
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 p-1 rounded-lg"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="px-4 py-5">
          <div className="px-3 mb-2 text-[11px] font-bold uppercase tracking-wider text-slate-400">
            Main Menu
          </div>
          <nav className="space-y-1">
            {filteredNavItems.map((item) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  onClick={() => onClose()}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                      isActive
                        ? 'bg-indigo-50 text-indigo-700 shadow-xs shadow-indigo-100'
                        : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                    }`
                  }
                >
                  <Icon className="w-4 h-4 shrink-0" />
                  <span>{item.label}</span>
                </NavLink>
              );
            })}
          </nav>
        </div>

        <div className="absolute bottom-4 left-4 right-4 p-3 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-500">
          <div className="font-bold text-slate-700 mb-0.5">Sri Lanka School Edition</div>
          <div>Multi-School Timetable MVP v1.0</div>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
