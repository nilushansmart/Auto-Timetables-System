import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import Badge from '../common/Badge';
import { LogOut, User as UserIcon, School, Menu } from 'lucide-react';

export const Navbar = ({ onToggleSidebar }) => {
  const { user, logout } = useAuth();

  const roleColors = {
    SCHOOL_ADMIN: 'indigo',
    COORDINATOR: 'emerald',
    TEACHER: 'sky',
  };

  return (
    <header className="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b border-slate-200 bg-white/80 px-4 sm:px-6 backdrop-blur-md">
      <div className="flex items-center gap-3">
        <button
          type="button"
          onClick={onToggleSidebar}
          className="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-xl lg:hidden cursor-pointer"
          aria-label="Toggle navigation menu"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-indigo-600 text-white flex items-center justify-center font-bold text-sm shadow-xs shadow-indigo-200">
            ST
          </div>
          <div className="hidden sm:block">
            <span className="text-sm font-bold text-slate-900 tracking-tight block">
              Smart Timetables
            </span>
            <span className="text-xs text-slate-500 flex items-center gap-1 font-medium">
              <School className="w-3 h-3 text-slate-400" />
              Demo Central College
            </span>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-3">
        <div className="flex items-center gap-2 pl-3 border-l border-slate-200">
          <div className="w-8 h-8 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-600 font-semibold text-xs">
            {user?.fullName?.charAt(0) || <UserIcon className="w-4 h-4" />}
          </div>
          <div className="hidden md:block text-left">
            <div className="text-xs font-bold text-slate-900 leading-tight">
              {user?.fullName || 'User'}
            </div>
            <div className="text-[11px] text-slate-500 leading-tight">
              {user?.email}
            </div>
          </div>
          <Badge
            variant={roleColors[user?.role] || 'slate'}
            size="sm"
            className="hidden sm:inline-flex uppercase tracking-wider font-bold"
          >
            {user?.role?.replace('_', ' ')}
          </Badge>
        </div>

        <button
          type="button"
          onClick={logout}
          className="flex items-center gap-1.5 p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition-colors cursor-pointer text-xs font-semibold"
          title="Sign out"
        >
          <LogOut className="w-4 h-4" />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </header>
  );
};

export default Navbar;
