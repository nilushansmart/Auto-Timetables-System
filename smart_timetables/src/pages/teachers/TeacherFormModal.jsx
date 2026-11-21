import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import { useToast } from '../../hooks/useToast';
import teacherService from '../../services/teacherService';

export const TeacherFormModal = ({ isOpen, onClose, teacher, onSuccess }) => {
  const [formData, setFormData] = useState({
    fullName: '',
    staffNo: '',
    shortCode: '',
    email: '',
    phone: '',
    maxPeriodsWeek: 30,
    maxPeriodsDay: 6,
    active: true,
  });
  const [loading, setLoading] = useState(false);
  const toast = useToast();

  useEffect(() => {
    if (teacher) {
      setFormData({
        fullName: teacher.fullName || '',
        staffNo: teacher.staffNo || '',
        shortCode: teacher.shortCode || '',
        email: teacher.email || '',
        phone: teacher.phone || '',
        maxPeriodsWeek: teacher.maxPeriodsWeek || 30,
        maxPeriodsDay: teacher.maxPeriodsDay || 6,
        active: teacher.active !== false,
      });
    } else {
      setFormData({
        fullName: '',
        staffNo: '',
        shortCode: '',
        email: '',
        phone: '',
        maxPeriodsWeek: 30,
        maxPeriodsDay: 6,
        active: true,
      });
    }
  }, [teacher, isOpen]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const payload = {
        fullName: formData.fullName,
        staffNo: formData.staffNo || null,
        shortCode: formData.shortCode || null,
        email: formData.email || null,
        phone: formData.phone || null,
        maxPeriodsWeek: Number(formData.maxPeriodsWeek),
        maxPeriodsDay: Number(formData.maxPeriodsDay),
        active: formData.active,
      };

      if (teacher) {
        await teacherService.updateTeacher(teacher.id, payload);
        toast.success('Teacher details updated');
      } else {
        await teacherService.createTeacher(payload);
        toast.success('Teacher created successfully');
      }
      onSuccess();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save teacher');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={teacher ? 'Edit Teacher' : 'Add New Teacher'}
      description="Record teacher workload parameters and contact information."
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Full Name"
          value={formData.fullName}
          onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
          placeholder="e.g. Sunil Perera"
          required
        />

        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Staff ID / No"
            value={formData.staffNo}
            onChange={(e) => setFormData({ ...formData, staffNo: e.target.value })}
            placeholder="e.g. ST-001"
          />
          <Input
            label="Short Code"
            value={formData.shortCode}
            onChange={(e) => setFormData({ ...formData, shortCode: e.target.value })}
            placeholder="e.g. SP"
            maxLength={10}
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Email Address"
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            placeholder="teacher@school.lk"
          />
          <Input
            label="Phone Number"
            value={formData.phone}
            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
            placeholder="0711234567"
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Max Periods / Week"
            type="number"
            min="1"
            max="45"
            value={formData.maxPeriodsWeek}
            onChange={(e) => setFormData({ ...formData, maxPeriodsWeek: e.target.value })}
            required
          />
          <Input
            label="Max Periods / Day"
            type="number"
            min="1"
            max="10"
            value={formData.maxPeriodsDay}
            onChange={(e) => setFormData({ ...formData, maxPeriodsDay: e.target.value })}
            required
          />
        </div>

        <div className="flex items-center gap-2 pt-2">
          <input
            type="checkbox"
            id="active-teacher"
            checked={formData.active}
            onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
            className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 border-slate-300 cursor-pointer"
          />
          <label htmlFor="active-teacher" className="text-sm font-medium text-slate-700 cursor-pointer">
            Teacher is active in timetable generation
          </label>
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" isLoading={loading}>
            {teacher ? 'Save Changes' : 'Create Teacher'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default TeacherFormModal;
