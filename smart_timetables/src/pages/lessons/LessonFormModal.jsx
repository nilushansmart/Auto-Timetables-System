import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Button from '../../components/common/Button';
import { useToast } from '../../hooks/useToast';
import lessonService from '../../services/lessonService';
import subjectService from '../../services/subjectService';
import teacherService from '../../services/teacherService';
import masterService from '../../services/masterService';

export const LessonFormModal = ({ isOpen, onClose, lesson, academicYear, onSuccess }) => {
  const [formData, setFormData] = useState({
    subjectId: '',
    teacherId: '',
    classGroupIds: [],
    periodsPerWeek: 6,
    blockSize: 1,
    maxPerDay: 2,
    lessonGroupId: '',
    requiredRoomType: '',
    fixedRoomId: '',
  });

  const [subjects, setSubjects] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [classes, setClasses] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [lessonGroups, setLessonGroups] = useState([]);
  const [loading, setLoading] = useState(false);

  const toast = useToast();

  useEffect(() => {
    const fetchAux = async () => {
      try {
        const [subData, teachData, clsData, roomData, groupData] = await Promise.all([
          subjectService.getSubjects({ size: 100 }),
          teacherService.getTeachers({ size: 100 }),
          masterService.getClasses(academicYear?.id || null),
          masterService.getRooms({ size: 100 }),
          lessonService.getLessonGroups(),
        ]);
        setSubjects(subData.content || []);
        setTeachers(teachData.content || []);
        setClasses(Array.isArray(clsData) ? clsData : clsData.content || []);
        setRooms(roomData.content || []);
        setLessonGroups(groupData.content || []);
      } catch (err) {
        console.error('Failed to load aux data for lesson form', err);
      }
    };
    if (isOpen) {
      fetchAux();
    }
  }, [isOpen, academicYear]);

  useEffect(() => {
    if (lesson) {
      setFormData({
        subjectId: lesson.subject?.id ? String(lesson.subject.id) : '',
        teacherId: lesson.teacher?.id ? String(lesson.teacher.id) : '',
        classGroupIds: lesson.classGroups?.map((c) => c.id) || [],
        periodsPerWeek: lesson.periodsPerWeek || 6,
        blockSize: lesson.blockSize || 1,
        maxPerDay: lesson.maxPerDay || 2,
        lessonGroupId: lesson.lessonGroupId ? String(lesson.lessonGroupId) : '',
        requiredRoomType: lesson.requiredRoomType || '',
        fixedRoomId: lesson.fixedRoom?.id ? String(lesson.fixedRoom.id) : '',
      });
    } else {
      setFormData({
        subjectId: '',
        teacherId: '',
        classGroupIds: [],
        periodsPerWeek: 6,
        blockSize: 1,
        maxPerDay: 2,
        lessonGroupId: '',
        requiredRoomType: '',
        fixedRoomId: '',
      });
    }
  }, [lesson, isOpen]);

  const handleToggleClass = (classId) => {
    setFormData((prev) => {
      const exists = prev.classGroupIds.includes(classId);
      return {
        ...prev,
        classGroupIds: exists
          ? prev.classGroupIds.filter((id) => id !== classId)
          : [...prev.classGroupIds, classId],
      };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!academicYear?.id) {
      toast.error('No active academic year found');
      return;
    }
    if (formData.classGroupIds.length === 0) {
      toast.error('Select at least one class group');
      return;
    }
    setLoading(true);

    try {
      const payload = {
        academicYearId: academicYear.id,
        subjectId: Number(formData.subjectId),
        teacherId: Number(formData.teacherId),
        classGroupIds: formData.classGroupIds,
        periodsPerWeek: Number(formData.periodsPerWeek),
        blockSize: Number(formData.blockSize),
        maxPerDay: Number(formData.maxPerDay),
        lessonGroupId: formData.lessonGroupId ? Number(formData.lessonGroupId) : null,
        requiredRoomType: formData.requiredRoomType || null,
        fixedRoomId: formData.fixedRoomId ? Number(formData.fixedRoomId) : null,
      };

      if (lesson) {
        await lessonService.updateLesson(lesson.id, payload);
        toast.success('Lesson updated');
      } else {
        await lessonService.createLesson(payload);
        toast.success('Lesson created');
      }
      onSuccess();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save lesson');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={lesson ? 'Edit Lesson' : 'Add New Lesson'}
      description="Configure subject requirements, assigned teacher, and student classes."
      maxWidth="max-w-xl"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-3">
          <Select
            label="Subject"
            value={formData.subjectId}
            onChange={(e) => setFormData({ ...formData, subjectId: e.target.value })}
            options={subjects.map((s) => ({
              value: String(s.id),
              label: `${s.code} - ${s.nameEn}`,
            }))}
            placeholder="Select Subject"
            required
          />

          <Select
            label="Assigned Teacher"
            value={formData.teacherId}
            onChange={(e) => setFormData({ ...formData, teacherId: e.target.value })}
            options={teachers.map((t) => ({
              value: String(t.id),
              label: `${t.fullName} (${t.shortCode || 'Teacher'})`,
            }))}
            placeholder="Select Teacher"
            required
          />
        </div>

        {/* Classes Multi-Select */}
        <div>
          <label className="block text-xs font-semibold text-slate-700 tracking-wide uppercase mb-1.5">
            Allocated Class Groups <span className="text-rose-500">*</span>
          </label>
          <div className="p-3 border border-slate-200 rounded-xl bg-slate-50/50 max-h-36 overflow-y-auto space-y-1.5">
            {classes.length === 0 ? (
              <p className="text-xs text-slate-400 italic">No class groups created yet.</p>
            ) : (
              classes.map((cls) => {
                const checked = formData.classGroupIds.includes(cls.id);
                return (
                  <label
                    key={cls.id}
                    className="flex items-center gap-2.5 p-1.5 rounded-lg hover:bg-white text-xs font-medium text-slate-800 cursor-pointer transition-colors"
                  >
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => handleToggleClass(cls.id)}
                      className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 border-slate-300"
                    />
                    <span>
                      {cls.name} (Grade {cls.gradeNo})
                    </span>
                  </label>
                );
              })
            )}
          </div>
        </div>

        <div className="grid grid-cols-3 gap-3">
          <Input
            label="Periods / Wk"
            type="number"
            min="1"
            max="20"
            value={formData.periodsPerWeek}
            onChange={(e) => setFormData({ ...formData, periodsPerWeek: e.target.value })}
            required
          />
          <Select
            label="Block Size"
            value={String(formData.blockSize)}
            onChange={(e) => setFormData({ ...formData, blockSize: Number(e.target.value) })}
            options={[
              { value: '1', label: 'Single (1)' },
              { value: '2', label: 'Double (2)' },
              { value: '3', label: 'Triple (3)' },
            ]}
            required
          />
          <Input
            label="Max / Day"
            type="number"
            min="1"
            max="4"
            value={formData.maxPerDay}
            onChange={(e) => setFormData({ ...formData, maxPerDay: e.target.value })}
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-3 pt-2 border-t border-slate-100">
          <Select
            label="Parallel Lesson Group"
            value={formData.lessonGroupId}
            onChange={(e) => setFormData({ ...formData, lessonGroupId: e.target.value })}
            options={[
              { value: '', label: 'None (Standalone lesson)' },
              ...lessonGroups.map((g) => ({
                value: String(g.id),
                label: `${g.name} (${g.type})`,
              })),
            ]}
          />

          <Select
            label="Required Room Type"
            value={formData.requiredRoomType}
            onChange={(e) => setFormData({ ...formData, requiredRoomType: e.target.value })}
            options={[
              { value: '', label: 'Standard Classroom' },
              { value: 'SCIENCE_LAB', label: 'Science Laboratory' },
              { value: 'COMPUTER_LAB', label: 'Computer / ICT Lab' },
              { value: 'SPECIAL_ROOM', label: 'Special Music/Art Room' },
              { value: 'HALL', label: 'Main Assembly Hall' },
            ]}
          />
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" isLoading={loading}>
            {lesson ? 'Save Changes' : 'Create Lesson'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default LessonFormModal;
