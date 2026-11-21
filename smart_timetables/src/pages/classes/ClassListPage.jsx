import React, { useState, useEffect } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Table, { TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Input from '../../components/common/Input';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import ClassFormModal from './ClassFormModal';
import { useToast } from '../../hooks/useToast';
import { Plus, GraduationCap, Edit, Trash2 } from 'lucide-react';
import masterService from '../../services/masterService';
import academicService from '../../services/academicService';

export const ClassListPage = () => {
  const [classes, setClasses] = useState([]);
  const [grades, setGrades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeYear, setActiveYear] = useState(null);

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingClass, setEditingClass] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const toast = useToast();

  const fetchClasses = async () => {
    setLoading(true);
    try {
      const year = await academicService.getActiveAcademicYear().catch(() => null);
      setActiveYear(year);

      const [clsData, gradeData] = await Promise.all([
        masterService.getClasses(year?.id || null),
        masterService.getGrades(),
      ]);

      setClasses(Array.isArray(clsData) ? clsData : clsData.content || []);
      setGrades(gradeData.content || []);
    } catch (err) {
      toast.error('Failed to load class groups');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClasses();
  }, []);

  const mediumNames = { 1: 'Sinhala', 2: 'Tamil', 3: 'English' };

  const handleOpenCreate = () => {
    setEditingClass(null);
    setFormModalOpen(true);
  };

  const handleOpenEdit = (cls) => {
    setEditingClass(cls);
    setFormModalOpen(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await masterService.deleteClass(deleteTarget.id);
      toast.success('Class group deleted');
      setDeleteTarget(null);
      fetchClasses();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete class group');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Class Groups"
        subtitle="Manage student class divisions (e.g. 6-A, 11-B, 12-Bio) with medium and assigned class teacher."
      >
        <Button variant="primary" icon={Plus} onClick={handleOpenCreate}>
          Add Class Group
        </Button>
      </PageHeader>

      {loading ? (
        <div className="flex justify-center py-16">
          <Spinner size="lg" />
        </div>
      ) : classes.length === 0 ? (
        <EmptyState
          icon={GraduationCap}
          title="No Class Groups Found"
          description="Create classes for your active academic year."
          actionLabel="Add First Class"
          onAction={handleOpenCreate}
        />
      ) : (
        <Table>
          <TableHeader>
            <tr>
              <TableHead>Class Name</TableHead>
              <TableHead>Grade</TableHead>
              <TableHead>Medium</TableHead>
              <TableHead>Stream (A/L)</TableHead>
              <TableHead>Class Teacher</TableHead>
              <TableHead>Students</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </tr>
          </TableHeader>
          <TableBody>
            {classes.map((cls) => (
              <TableRow key={cls.id}>
                <TableCell className="font-bold text-slate-900 text-base">
                  {cls.name}
                </TableCell>
                <TableCell>
                  <Badge variant="indigo">Grade {cls.gradeNo}</Badge>
                </TableCell>
                <TableCell className="font-semibold text-slate-700">
                  {mediumNames[cls.mediumId] || cls.mediumCode || 'Sinhala'}
                </TableCell>
                <TableCell className="text-slate-500">
                  {cls.streamName || '—'}
                </TableCell>
                <TableCell className="font-medium text-slate-800">
                  {cls.classTeacherName || '—'}
                </TableCell>
                <TableCell className="text-slate-600 font-medium">
                  {cls.studentCount || 0}
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex items-center justify-end gap-1.5">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleOpenEdit(cls)}
                      icon={Edit}
                      className="text-slate-600 hover:text-indigo-600"
                    >
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => setDeleteTarget(cls)}
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
      <ClassFormModal
        isOpen={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        cls={editingClass}
        grades={grades}
        academicYear={activeYear}
        onSuccess={fetchClasses}
      />

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Class Group?"
        message={`Are you sure you want to delete class ${deleteTarget?.name}?`}
        isLoading={isDeleting}
      />
    </div>
  );
};

export default ClassListPage;
