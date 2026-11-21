import React, { useState, useEffect } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Table, { TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Input from '../../components/common/Input';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import SubjectFormModal from './SubjectFormModal';
import { useToast } from '../../hooks/useToast';
import { useDebounce } from '../../hooks/useDebounce';
import { Plus, BookOpen, Edit, Trash2 } from 'lucide-react';
import subjectService from '../../services/subjectService';

export const SubjectListPage = () => {
  const [subjects, setSubjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 300);

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingSubject, setEditingSubject] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const toast = useToast();

  const fetchSubjects = async () => {
    setLoading(true);
    try {
      const data = await subjectService.getSubjects({ size: 100 });
      setSubjects(data.content || []);
    } catch (err) {
      toast.error('Failed to load subjects');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSubjects();
  }, []);

  const filteredSubjects = subjects.filter((s) => {
    if (!debouncedSearch) return true;
    const term = debouncedSearch.toLowerCase();
    return (
      s.code?.toLowerCase().includes(term) ||
      s.nameEn?.toLowerCase().includes(term) ||
      s.nameSi?.includes(term) ||
      s.nameTa?.includes(term)
    );
  });

  const handleOpenCreate = () => {
    setEditingSubject(null);
    setFormModalOpen(true);
  };

  const handleOpenEdit = (sub) => {
    setEditingSubject(sub);
    setFormModalOpen(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await subjectService.deleteSubject(deleteTarget.id);
      toast.success('Subject deleted');
      setDeleteTarget(null);
      fetchSubjects();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete subject');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Subject Curriculum"
        subtitle="Manage academic subjects with multilingual metadata (English, Sinhala, Tamil) and practical flags."
      >
        <Button variant="primary" icon={Plus} onClick={handleOpenCreate}>
          Add Subject
        </Button>
      </PageHeader>

      <div className="max-w-md">
        <Input
          placeholder="Search subjects by code or name..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-16">
          <Spinner size="lg" />
        </div>
      ) : filteredSubjects.length === 0 ? (
        <EmptyState
          icon={BookOpen}
          title="No Subjects Found"
          description={search ? 'No subjects match your search.' : 'Define your school curriculum subjects.'}
          actionLabel={search ? undefined : 'Add First Subject'}
          onAction={search ? undefined : handleOpenCreate}
        />
      ) : (
        <Table>
          <TableHeader>
            <tr>
              <TableHead>Code</TableHead>
              <TableHead>English Name</TableHead>
              <TableHead>Sinhala Name</TableHead>
              <TableHead>Tamil Name</TableHead>
              <TableHead>Categories</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </tr>
          </TableHeader>
          <TableBody>
            {filteredSubjects.map((sub) => (
              <TableRow key={sub.id}>
                <TableCell className="font-mono font-bold text-indigo-700">
                  {sub.code}
                </TableCell>
                <TableCell className="font-bold text-slate-900">{sub.nameEn}</TableCell>
                <TableCell className="font-medium text-slate-700">{sub.nameSi || '—'}</TableCell>
                <TableCell className="font-medium text-slate-700">{sub.nameTa || '—'}</TableCell>
                <TableCell>
                  <div className="flex items-center gap-1.5">
                    {sub.isPractical && <Badge variant="purple" size="sm">Practical</Badge>}
                    {sub.isReligion && <Badge variant="amber" size="sm">Religion</Badge>}
                    {!sub.isPractical && !sub.isReligion && (
                      <Badge variant="slate" size="sm">Standard</Badge>
                    )}
                  </div>
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex items-center justify-end gap-1.5">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleOpenEdit(sub)}
                      icon={Edit}
                      className="text-slate-600 hover:text-indigo-600"
                    >
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => setDeleteTarget(sub)}
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
      <SubjectFormModal
        isOpen={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        subject={editingSubject}
        onSuccess={fetchSubjects}
      />

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Subject?"
        message={`Are you sure you want to delete ${deleteTarget?.code} - ${deleteTarget?.nameEn}?`}
        isLoading={isDeleting}
      />
    </div>
  );
};

export default SubjectListPage;
