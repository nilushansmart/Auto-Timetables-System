import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import { useToast } from '../../hooks/useToast';
import subjectService from '../../services/subjectService';

export const SubjectFormModal = ({ isOpen, onClose, subject, onSuccess }) => {
  const [formData, setFormData] = useState({
    code: '',
    nameEn: '',
    nameSi: '',
    nameTa: '',
    isReligion: false,
    isPractical: false,
  });
  const [loading, setLoading] = useState(false);
  const toast = useToast();

  useEffect(() => {
    if (subject) {
      setFormData({
        code: subject.code || '',
        nameEn: subject.nameEn || '',
        nameSi: subject.nameSi || '',
        nameTa: subject.nameTa || '',
        isReligion: !!subject.isReligion,
        isPractical: !!subject.isPractical,
      });
    } else {
      setFormData({
        code: '',
        nameEn: '',
        nameSi: '',
        nameTa: '',
        isReligion: false,
        isPractical: false,
      });
    }
  }, [subject, isOpen]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const payload = {
        code: formData.code.toUpperCase(),
        nameEn: formData.nameEn,
        nameSi: formData.nameSi || null,
        nameTa: formData.nameTa || null,
        isReligion: formData.isReligion,
        isPractical: formData.isPractical,
      };

      if (subject) {
        await subjectService.updateSubject(subject.id, payload);
        toast.success('Subject updated');
      } else {
        await subjectService.createSubject(payload);
        toast.success('Subject created');
      }
      onSuccess();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save subject');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={subject ? 'Edit Subject' : 'Add New Subject'}
      description="Record subject metadata with trilingual Sri Lankan labels."
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Subject Code"
          value={formData.code}
          onChange={(e) => setFormData({ ...formData, code: e.target.value })}
          placeholder="e.g. MATH6, SCI6"
          required
          maxLength={20}
        />

        <Input
          label="English Name"
          value={formData.nameEn}
          onChange={(e) => setFormData({ ...formData, nameEn: e.target.value })}
          placeholder="e.g. Mathematics"
          required
        />

        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Sinhala Name (සිංහල)"
            value={formData.nameSi}
            onChange={(e) => setFormData({ ...formData, nameSi: e.target.value })}
            placeholder="ගණිතය"
          />
          <Input
            label="Tamil Name (தமிழ்)"
            value={formData.nameTa}
            onChange={(e) => setFormData({ ...formData, nameTa: e.target.value })}
            placeholder="கணிதம்"
          />
        </div>

        <div className="space-y-2 pt-2 border-t border-slate-100">
          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="practical-sub"
              checked={formData.isPractical}
              onChange={(e) => setFormData({ ...formData, isPractical: e.target.checked })}
              className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 border-slate-300 cursor-pointer"
            />
            <label htmlFor="practical-sub" className="text-sm font-medium text-slate-700 cursor-pointer">
              Practical / Lab Subject (requires science or computer lab)
            </label>
          </div>

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="religion-sub"
              checked={formData.isReligion}
              onChange={(e) => setFormData({ ...formData, isReligion: e.target.checked })}
              className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 border-slate-300 cursor-pointer"
            />
            <label htmlFor="religion-sub" className="text-sm font-medium text-slate-700 cursor-pointer">
              Religion Subject (split parallel group across classes)
            </label>
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" isLoading={loading}>
            {subject ? 'Save Changes' : 'Create Subject'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default SubjectFormModal;
