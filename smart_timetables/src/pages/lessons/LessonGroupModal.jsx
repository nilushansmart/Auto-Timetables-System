import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Button from '../../components/common/Button';
import Badge from '../../components/common/Badge';
import { useToast } from '../../hooks/useToast';
import { Plus, Trash2, Network } from 'lucide-react';
import lessonService from '../../services/lessonService';

export const LessonGroupModal = ({ isOpen, onClose, academicYear }) => {
  const [groups, setGroups] = useState([]);
  const [name, setName] = useState('');
  const [type, setType] = useState('RELIGION_SPLIT');
  const [isCreating, setIsCreating] = useState(false);
  const [loading, setLoading] = useState(true);

  const toast = useToast();

  const fetchGroups = async () => {
    setLoading(true);
    try {
      const data = await lessonService.getLessonGroups();
      setGroups(data.content || []);
    } catch (err) {
      toast.error('Failed to load lesson groups');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      fetchGroups();
    }
  }, [isOpen]);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!name || !academicYear?.id) return;
    setIsCreating(true);

    try {
      await lessonService.createLessonGroup({
        academicYearId: academicYear.id,
        name,
        type,
      });
      toast.success('Parallel lesson group created');
      setName('');
      fetchGroups();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create group');
    } finally {
      setIsCreating(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await lessonService.deleteLessonGroup(id);
      toast.success('Group deleted');
      fetchGroups();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete group');
    }
  };

  const groupTypeBadges = {
    RELIGION_SPLIT: 'amber',
    OL_BASKET: 'indigo',
    AL_STREAM: 'emerald',
    ELECTIVE_SPLIT: 'purple',
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Parallel Lesson Groups"
      description="Define groups where multiple lessons intentionally share the same slot (e.g. Religion splits or O/L bucket subjects)."
      maxWidth="max-w-lg"
    >
      <div className="space-y-5">
        <form onSubmit={handleCreate} className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-3">
          <Input
            label="Group Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Grade 6 Religion Split, Grade 10 Bucket 1"
            required
          />

          <Select
            label="Grouping Mechanism"
            value={type}
            onChange={(e) => setType(e.target.value)}
            options={[
              { value: 'RELIGION_SPLIT', label: 'Religion Split (Buddhism, Islam, etc.)' },
              { value: 'OL_BASKET', label: 'O/L Basket Subject' },
              { value: 'AL_STREAM', label: 'A/L Stream Elective' },
              { value: 'ELECTIVE_SPLIT', label: 'General Elective Split' },
            ]}
            required
          />

          <div className="flex justify-end pt-1">
            <Button type="submit" variant="primary" icon={Plus} isLoading={isCreating}>
              Create Group
            </Button>
          </div>
        </form>

        <div className="space-y-2">
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
            Configured Groups ({groups.length})
          </h4>
          {groups.length === 0 ? (
            <p className="text-xs text-slate-400 italic">No parallel groups configured.</p>
          ) : (
            groups.map((g) => (
              <div
                key={g.id}
                className="flex items-center justify-between p-3 rounded-xl border border-slate-200 bg-white"
              >
                <div>
                  <div className="font-bold text-xs text-slate-900">{g.name}</div>
                  <Badge variant={groupTypeBadges[g.type] || 'slate'} size="sm" className="mt-1">
                    {g.type?.replace('_', ' ')}
                  </Badge>
                </div>
                <button
                  type="button"
                  onClick={() => handleDelete(g.id)}
                  className="text-slate-400 hover:text-rose-600 p-1 rounded-lg"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))
          )}
        </div>

        <div className="flex justify-end pt-4 border-t border-slate-100">
          <Button variant="secondary" onClick={onClose}>
            Done
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default LessonGroupModal;
