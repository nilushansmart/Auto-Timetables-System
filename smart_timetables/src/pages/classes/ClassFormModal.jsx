import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Button from '../../components/common/Button';
import { useToast } from '../../hooks/useToast';
import masterService from '../../services/masterService';
import teacherService from '../../services/teacherService';

export const ClassFormModal = ({ isOpen, onClose, cls, grades = [], academicYear, onSuccess }) => {
  const [formData, setFormData] = useState({
    gradeId: '',
    mediumId: '1',
    streamId: '',
    name: '',
    classTeacherId: '',
    studentCount: 40,
  });
  const [teachers, setTeachers] = useState([]);
  const [streams, setStreams] = useState([]);
  const [loading, setLoading] = useState(false);
  const toast = useToast();

  useEffect(() => {
    const fetchAux = async () => {
      try {
        const [teacherData, streamData] = await Promise.all([
          teacherService.getTeachers({ size: 100 }),
          masterService.getStreams(),
        ]);
        setTeachers(teacherData.content || []);
        setStreams(streamData.content || []);
      } catch (err) {
        console.error('Failed to load auxiliary class data', err);
      }
    };
    if (isOpen) {
      fetchAux();
    }
  }, [isOpen]);

  useEffect(() => {
    if (cls) {
      setFormData({
        gradeId: cls.gradeId ? String(cls.gradeId) : '',
        mediumId: cls.mediumId ? String(cls.mediumId) : '1',
        streamId: cls.streamId ? String(cls.streamId) : '',
        name: cls.name || '',
        classTeacherId: cls.classTeacherId ? String(cls.classTeacherId) : '',
        studentCount: cls.studentCount || 40,
      });
    } else {
      setFormData({
        gradeId: grades[0]?.id ? String(grades[0].id) : '',
        mediumId: '1',
        streamId: '',
        name: '',
        classTeacherId: '',
        studentCount: 40,
      });
    }
  }, [cls, grades, isOpen]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!academicYear?.id) {
      toast.error('No active academic year found');
      return;
    }
    setLoading(true);

    try {
      const payload = {
        academicYearId: academicYear.id,
        gradeId: Number(formData.gradeId),
        mediumId: Number(formData.mediumId),
        streamId: formData.streamId ? Number(formData.streamId) : null,
        name: formData.name,
        classTeacherId: formData.classTeacherId ? Number(formData.classTeacherId) : null,
        studentCount: Number(formData.studentCount),
      };

      if (cls) {
        await masterService.updateClass(cls.id, payload);
        toast.success('Class group updated');
      } else {
        await masterService.createClass(payload);
        toast.success('Class group created');
      }
      onSuccess();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save class group');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={cls ? 'Edit Class Group' : 'Add Class Group'}
      description="Define a grade class unit with medium and assigned class teacher."
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-3">
          <Select
            label="Grade"
            value={formData.gradeId}
            onChange={(e) => setFormData({ ...formData, gradeId: e.target.value })}
            options={grades.map((g) => ({
              value: String(g.id),
              label: `Grade ${g.gradeNo} (${g.level})`,
            }))}
            placeholder="Select Grade"
            required
          />

          <Select
            label="Medium of Instruction"
            value={formData.mediumId}
            onChange={(e) => setFormData({ ...formData, mediumId: e.target.value })}
            options={[
              { value: '1', label: 'Sinhala' },
              { value: '2', label: 'Tamil' },
              { value: '3', label: 'English' },
            ]}
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Class Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="e.g. 6-A, 11-B, 12-Science"
            required
          />

          <Input
            label="Student Count"
            type="number"
            min="1"
            max="80"
            value={formData.studentCount}
            onChange={(e) => setFormData({ ...formData, studentCount: e.target.value })}
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <Select
            label="Class Teacher (Optional)"
            value={formData.classTeacherId}
            onChange={(e) => setFormData({ ...formData, classTeacherId: e.target.value })}
            options={[
              { value: '', label: 'None Assigned' },
              ...teachers.map((t) => ({
                value: String(t.id),
                label: `${t.fullName} (${t.shortCode || 'Teacher'})`,
              })),
            ]}
          />

          <Select
            label="Stream (A/L Only)"
            value={formData.streamId}
            onChange={(e) => setFormData({ ...formData, streamId: e.target.value })}
            options={[
              { value: '', label: 'General / Non-A/L' },
              ...streams.map((s) => ({
                value: String(s.id),
                label: s.name,
              })),
            ]}
          />
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" isLoading={loading}>
            {cls ? 'Save Changes' : 'Create Class'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default ClassFormModal;
