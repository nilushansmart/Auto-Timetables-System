import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import AppLayout from '../components/layout/AppLayout';

import LoginPage from '../pages/auth/LoginPage';
import DashboardPage from '../pages/dashboard/DashboardPage';
import TimetablePage from '../pages/timetable/TimetablePage';
import TimeGridPage from '../pages/time-grid/TimeGridPage';
import TeacherListPage from '../pages/teachers/TeacherListPage';
import ClassListPage from '../pages/classes/ClassListPage';
import SubjectListPage from '../pages/subjects/SubjectListPage';
import RoomListPage from '../pages/rooms/RoomListPage';
import LessonListPage from '../pages/lessons/LessonListPage';
import AcademicYearsPage from '../pages/academic/AcademicYearsPage';

export const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<LoginPage />} />

      {/* Protected Routes */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/timetable" replace />} />
        <Route path="timetable" element={<TimetablePage />} />
        <Route path="dashboard" element={<DashboardPage />} />

        {/* Admin & Coordinator Only */}
        <Route
          path="time-grid"
          element={
            <ProtectedRoute allowedRoles={['SCHOOL_ADMIN', 'COORDINATOR']}>
              <TimeGridPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="teachers"
          element={
            <ProtectedRoute allowedRoles={['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER']}>
              <TeacherListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="classes"
          element={
            <ProtectedRoute allowedRoles={['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER']}>
              <ClassListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="subjects"
          element={
            <ProtectedRoute allowedRoles={['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER']}>
              <SubjectListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="rooms"
          element={
            <ProtectedRoute allowedRoles={['SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER']}>
              <RoomListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="lessons"
          element={
            <ProtectedRoute allowedRoles={['SCHOOL_ADMIN', 'COORDINATOR']}>
              <LessonListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="academic-years"
          element={
            <ProtectedRoute allowedRoles={['SCHOOL_ADMIN', 'COORDINATOR']}>
              <AcademicYearsPage />
            </ProtectedRoute>
          }
        />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/timetable" replace />} />
    </Routes>
  );
};

export default AppRoutes;
