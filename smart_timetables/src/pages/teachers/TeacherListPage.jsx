import React, { useState, useEffect } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Table, { TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Input from '../../components/common/Input';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import TeacherFormModal from './TeacherFormModal';
import TeacherAvailabilityModal from './TeacherAvailabilityModal';
import { useToast } from '../../hooks/useToast';
import { useDebounce } from '../../hooks/useDebounce';
import { Plus, Users, Search, Edit, Trash2, CalendarCheck, BookOpen } from 'lucide-react';
import teacherService from '../../services/teacherService';

export const TeacherListPage = () => {
  const [teachers, setTeachers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 300);

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState(null);
  const [availabilityModalOpen, setAvailabilityModalOpen] = useState(false);
  const [selectedTeacherForAvail, setSelectedTeacherForAvail] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const toast = useToast();

  const fetchTeachers = async () => {
    setLoading(true);
    try {
      const data = await teacherService.getTeachers({ size: 100 });
      setTeachers(data.content || []);
    } catch (err) {
      toast.error('Failed to load teachers');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTeachers();
  }, []);

  const filteredTeachers = teachers.filter((t) => {
    if (!debouncedSearch) return true;
    const term = debouncedSearch.toLowerCase();
    return (
      t.fullName?.toLowerCase().includes(term) ||
      t.shortCode?.toLowerCase().includes(term) ||
      t.staffNo?.toLowerCase().includes(term) ||
      t.email?.toLowerCase().includes(term)
    );
  });

  const handleOpenCreate = () => {
    setEditingTeacher(null);
    setFormModalOpen(true);
  };

  const handleOpenEdit = (t) => {
    setEditingTeacher(t);
    setFormModalOpen(true);
  };

  const handleOpenAvailability = (t) => {
    setSelectedTeacherForAvail(t);
    setAvailabilityModalOpen(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await teacherService.deleteTeacher(deleteTarget.id);
      toast.success('Teacher deleted');
      setDeleteTarget(null);
      fetchTeachers();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete teacher');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Teacher Directory"
        subtitle="Manage academic staff, workload limits, subject specializations, and time slot availability."
      >
        <Button variant="primary" icon={Plus} onClick={handleOpenCreate}>
          Add Teacher
        </Button>
      </PageHeader>

      <div className="flex items-center gap-3 max-w-md">
        <Input
          placeholder="Search by name, short code, or staff ID..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full"
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-16">
          <Spinner size="lg" />
        </div>
      ) : filteredTeachers.length === 0 ? (
        <EmptyState
          icon={Users}
          title="No Teachers Found"
          description={search ? 'No teacher records match your search criteria.' : 'Get started by adding teachers to the school database.'}
          actionLabel={search ? undefined : 'Add First Teacher'}
          onAction={search ? undefined : handleOpenCreate}
        />
      ) : (
        <Table>
          <TableHeader>
            <tr>
              <TableHead>Teacher</TableHead>
              <TableHead>Staff ID</TableHead>
              <TableHead>Short Code</TableHead>
              <TableHead>Contact</TableHead>
              <TableHead>Max Periods (Wk/Day)</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </tr>
          </TableHeader>
          <TableBody>
            {filteredTeachers.map((teacher) => (
              <TableRow key={teacher.id}>
                <TableCell>
                  <div className="font-bold text-slate-900">{teacher.fullName}</div>
                  <div className="text-xs text-slate-400">
                    {teacher.subjects?.length || 0} subjects qualified
                  </div>
                </TableCell>
                <TableCell className="font-mono text-xs text-slate-600">
                  {teacher.staffNo || '—'}
                </TableCell>
                <TableCell>
                  {teacher.shortCode ? (
                    <Badge variant="indigo" size="sm">
                      {teacher.shortCode}
                    </Badge>
                  ) : (
                    '—'
                  )}
                </TableCell>
                <TableCell>
                  <div className="text-xs text-slate-700">{teacher.email || '—'}</div>
                  <div className="text-xs text-slate-400">{teacher.phone || ''}</div>
                </TableCell>
                <TableCell className="text-xs font-semibold text-slate-700">
                  {teacher.maxPeriodsWeek || 30} / {teacher.maxPeriodsDay || 6}
                </TableCell>
                <TableCell>
                  <Badge variant={teacher.active ? 'emerald' : 'slate'}>
                    {teacher.active ? 'Active' : 'Inactive'}
                  </Badge>
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex items-center justify-end gap-1.5">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleOpenAvailability(teacher)}
                      icon={CalendarCheck}
                      title="Manage Subjects & Availability"
                    >
                      Availability
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleOpenEdit(teacher)}
                      icon={Edit}
                      className="text-slate-600 hover:text-indigo-600"
                    >
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => setDeleteTarget(teacher)}
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
      <TeacherFormModal
        isOpen={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        teacher={editingTeacher}
        onSuccess={fetchTeachers}
      />

      {/* Availability / Subject Modal */}
      <TeacherAvailabilityModal
        isOpen={availabilityModalOpen}
        onClose={() => setAvailabilityModalOpen(false)}
        teacher={selectedTeacherForAvail}
        onUpdated={fetchTeachers}
      />

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Teacher?"
        message={`Are you sure you want to remove ${deleteTarget?.fullName}? Any assigned lessons may need reassignment.`}
        isLoading={isDeleting}
      />
    </div>
  );
};

export default TeacherListPage;
