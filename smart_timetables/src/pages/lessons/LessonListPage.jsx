import React, { useState, useEffect } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Table, { TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import LessonFormModal from './LessonFormModal';
import LessonGroupModal from './LessonGroupModal';
import { useToast } from '../../hooks/useToast';
import { Plus, Layers, Edit, Trash2, Network } from 'lucide-react';
import lessonService from '../../services/lessonService';
import academicService from '../../services/academicService';

export const LessonListPage = () => {
  const [lessons, setLessons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeYear, setActiveYear] = useState(null);

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingLesson, setEditingLesson] = useState(null);
  const [groupModalOpen, setGroupModalOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const toast = useToast();

  const fetchLessons = async () => {
    setLoading(true);
    try {
      const year = await academicService.getActiveAcademicYear().catch(() => null);
      setActiveYear(year);

      const data = await lessonService.getLessons(year?.id || null);
      setLessons(Array.isArray(data) ? data : data.content || []);
    } catch (err) {
      toast.error('Failed to load lessons');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLessons();
  }, []);

  const handleOpenCreate = () => {
    setEditingLesson(null);
    setFormModalOpen(true);
  };

  const handleOpenEdit = (lesson) => {
    setEditingLesson(lesson);
    setFormModalOpen(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await lessonService.deleteLesson(deleteTarget.id);
      toast.success('Lesson deleted');
      setDeleteTarget(null);
      fetchLessons();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete lesson');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Lessons & Workload Allocations"
        subtitle="Configure lesson requirements: assigned teacher, class allocations, periods per week, block sizes, and parallel groups."
      >
        <div className="flex items-center gap-2">
          <Button variant="outline" icon={Network} onClick={() => setGroupModalOpen(true)}>
            Parallel Groups
          </Button>
          <Button variant="primary" icon={Plus} onClick={handleOpenCreate}>
            Add Lesson
          </Button>
        </div>
      </PageHeader>

      {loading ? (
        <div className="flex justify-center py-16">
          <Spinner size="lg" />
        </div>
      ) : lessons.length === 0 ? (
        <EmptyState
          icon={Layers}
          title="No Lessons Found"
          description="Create lesson assignments for your classes and teachers to generate schedules."
          actionLabel="Add First Lesson"
          onAction={handleOpenCreate}
        />
      ) : (
        <Table>
          <TableHeader>
            <tr>
              <TableHead>Subject</TableHead>
              <TableHead>Teacher</TableHead>
              <TableHead>Allocated Classes</TableHead>
              <TableHead>Periods / Wk</TableHead>
              <TableHead>Block Size</TableHead>
              <TableHead>Max / Day</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </tr>
          </TableHeader>
          <TableBody>
            {lessons.map((lesson) => (
              <TableRow key={lesson.id}>
                <TableCell>
                  <div className="font-bold text-slate-900">
                    {lesson.subject?.code} - {lesson.subject?.nameEn}
                  </div>
                  {lesson.lessonGroupId && (
                    <Badge variant="amber" size="sm" className="mt-0.5">
                      Parallel Group #{lesson.lessonGroupId}
                    </Badge>
                  )}
                </TableCell>
                <TableCell className="font-medium text-slate-800">
                  {lesson.teacher?.fullName || '—'}
                </TableCell>
                <TableCell>
                  <div className="flex flex-wrap gap-1">
                    {lesson.classGroups?.map((cg) => (
                      <Badge key={cg.id} variant="indigo" size="sm">
                        {cg.name}
                      </Badge>
                    ))}
                  </div>
                </TableCell>
                <TableCell className="font-bold text-slate-800">
                  {lesson.periodsPerWeek}
                </TableCell>
                <TableCell>
                  {lesson.blockSize > 1 ? (
                    <Badge variant="purple" size="sm">
                      Double ({lesson.blockSize})
                    </Badge>
                  ) : (
                    <span className="text-slate-500 text-xs">Single (1)</span>
                  )}
                </TableCell>
                <TableCell className="text-slate-600 font-medium">
                  {lesson.maxPerDay}
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex items-center justify-end gap-1.5">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleOpenEdit(lesson)}
                      icon={Edit}
                      className="text-slate-600 hover:text-indigo-600"
                    >
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => setDeleteTarget(lesson)}
                      icon={Trash2}
                      className="text-slate-400 hover:text-rose-600"
                    >
                      Delete
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      {/* Form Modal */}
      <LessonFormModal
        isOpen={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        lesson={editingLesson}
        academicYear={activeYear}
        onSuccess={fetchLessons}
      />

      {/* Parallel Group Modal */}
      <LessonGroupModal
        isOpen={groupModalOpen}
        onClose={() => setGroupModalOpen(false)}
        academicYear={activeYear}
      />

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Lesson?"
        message={`Are you sure you want to delete this ${deleteTarget?.subject?.nameEn} lesson? Placed timetable entries will be removed.`}
        isLoading={isDeleting}
      />
    </div>
  );
};

export default LessonListPage;
