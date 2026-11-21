import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Spinner from '../../components/common/Spinner';
import {
  CalendarDays,
  Users,
  GraduationCap,
  DoorOpen,
  BookOpen,
  ArrowRight,
  Clock,
  CheckCircle2,
} from 'lucide-react';
import teacherService from '../../services/teacherService';
import masterService from '../../services/masterService';
import subjectService from '../../services/subjectService';
import timetableService from '../../services/timetableService';

export const DashboardPage = () => {
  const { user } = useAuth();
  const [counts, setCounts] = useState({
    teachers: 0,
    classes: 0,
    rooms: 0,
    subjects: 0,
    versions: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const [teachersData, classesData, roomsData, subjectsData, versionsData] =
          await Promise.allSettled([
            teacherService.getTeachers({ size: 1 }),
            masterService.getClasses(null, { size: 1 }),
            masterService.getRooms({ size: 1 }),
            subjectService.getSubjects({ size: 1 }),
            timetableService.getVersions(null, { size: 1 }),
          ]);

        setCounts({
          teachers: teachersData.status === 'fulfilled' ? teachersData.value.totalElements || teachersData.value.length || 0 : 0,
          classes: classesData.status === 'fulfilled' ? classesData.value.totalElements || classesData.value.length || 0 : 0,
          rooms: roomsData.status === 'fulfilled' ? roomsData.value.totalElements || roomsData.value.length || 0 : 0,
          subjects: subjectsData.status === 'fulfilled' ? subjectsData.value.totalElements || subjectsData.value.length || 0 : 0,
          versions: versionsData.status === 'fulfilled' ? versionsData.value.totalElements || versionsData.value.length || 0 : 0,
        });
      } finally {
        setLoading(false);
      }
    };

    fetchCounts();
  }, []);

  const statCards = [
    {
      title: 'Total Teachers',
      count: counts.teachers,
      icon: Users,
      color: 'bg-indigo-500 text-white',
      link: '/teachers',
    },
    {
      title: 'Class Groups',
      count: counts.classes,
      icon: GraduationCap,
      color: 'bg-emerald-500 text-white',
      link: '/classes',
    },
    {
      title: 'Rooms & Labs',
      count: counts.rooms,
      icon: DoorOpen,
      color: 'bg-amber-500 text-white',
      link: '/rooms',
    },
    {
      title: 'Subjects',
      count: counts.subjects,
      icon: BookOpen,
      color: 'bg-sky-500 text-white',
      link: '/subjects',
    },
  ];

  return (
    <div className="space-y-6">
      <PageHeader
        title={`Welcome, ${user?.fullName || 'Administrator'}`}
        subtitle="Manage your school schedule, teachers, classes, and automatic timetable generation."
      >
        <Link to="/timetable">
          <Button variant="primary" icon={CalendarDays}>
            Open Timetable Viewer
          </Button>
        </Link>
      </PageHeader>

      {/* Primary Hero Banner */}
      <div className="rounded-3xl bg-linear-to-r from-indigo-700 via-indigo-800 to-slate-900 p-6 sm:p-8 text-white shadow-xl relative overflow-hidden">
        <div className="max-w-2xl relative z-10">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 text-white text-xs font-semibold backdrop-blur-md mb-4 border border-white/10">
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
            Active Term: 2026 Term 1 Master Schedule
          </div>
          <h2 className="text-2xl sm:text-3xl font-black tracking-tight mb-2">
            Conflict-Free Timetable Generator
          </h2>
          <p className="text-indigo-100 text-sm leading-relaxed mb-6">
            Review the weekly period grid, assign teachers to classes, and manually fine-tune timetable placements with instant multi-tenant clash detection.
          </p>
          <div className="flex flex-wrap gap-3">
            <Link to="/timetable">
              <Button variant="secondary" className="bg-white text-indigo-900 hover:bg-slate-100 font-bold">
                View Weekly Timetable
                <ArrowRight className="w-4 h-4 ml-1" />
              </Button>
            </Link>
            <Link to="/time-grid">
              <Button variant="outline" className="text-white border-white/30 hover:bg-white/10">
                <Clock className="w-4 h-4 mr-1.5" />
                Configure Time Grid
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {/* Metric Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map((stat) => {
          const Icon = stat.icon;
          return (
            <Link
              key={stat.title}
              to={stat.link}
              className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs hover:shadow-md hover:border-slate-300 transition-all flex items-center justify-between group"
            >
              <div>
                <div className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-1">
                  {stat.title}
                </div>
                <div className="text-2xl font-black text-slate-900">
                  {loading ? <Spinner size="sm" /> : stat.count}
                </div>
              </div>
              <div
                className={`w-12 h-12 rounded-2xl flex items-center justify-center shadow-md ${stat.color} group-hover:scale-105 transition-transform`}
              >
                <Icon className="w-6 h-6" />
              </div>
            </Link>
          );
        })}
      </div>
    </div>
  );
};

export default DashboardPage;
