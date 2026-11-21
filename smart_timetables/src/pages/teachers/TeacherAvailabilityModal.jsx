import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import Select from '../../components/common/Select';
import Badge from '../../components/common/Badge';
import Spinner from '../../components/common/Spinner';
import { useToast } from '../../hooks/useToast';
import { BookOpen, Clock, Plus, Trash2, CheckCircle2 } from 'lucide-react';
import teacherService from '../../services/teacherService';
import subjectService from '../../services/subjectService';
import timeSlotService from '../../services/timeSlotService';

export const TeacherAvailabilityModal = ({ isOpen, onClose, teacher, onUpdated }) => {
  const [activeTab, setActiveTab] = useState('subjects'); // 'subjects' | 'availability'
  const [teacherData, setTeacherData] = useState(null);
  const [loading, setLoading] = useState(true);

  // Subjects tab
  const [allSubjects, setAllSubjects] = useState([]);
  const [selectedSubjectId, setSelectedSubjectId] = useState('');
  const [selectedMediumId, setSelectedMediumId] = useState('1'); // 1: Sinhala, 2: Tamil, 3: English
  const [isAddingSubject, setIsAddingSubject] = useState(false);

  // Availability tab
  const [timeSlots, setTimeSlots] = useState([]);
  const [isUpdatingSlot, setIsUpdatingSlot] = useState(null);

  const toast = useToast();

  const fetchDetails = async () => {
    if (!teacher?.id) return;
    setLoading(true);
    try {
      const [fullTeacher, subList, slotList] = await Promise.all([
        teacherService.getTeacherById(teacher.id),
        subjectService.getSubjects({ size: 100 }),
        timeSlotService.getTimeSlots(),
      ]);
      setTeacherData(fullTeacher);
      setAllSubjects(subList.content || []);
      setTimeSlots(slotList.content || []);
    } catch (err) {
      toast.error('Failed to load teacher availability details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen && teacher?.id) {
      fetchDetails();
    }
  }, [isOpen, teacher]);

  // Subject Qualifications
  const handleAddSubject = async (e) => {
    e.preventDefault();
    if (!selectedSubjectId) return;
    setIsAddingSubject(true);
    try {
      await teacherService.assignSubject(
        teacher.id,
        Number(selectedSubjectId),
        Number(selectedMediumId)
      );
      toast.success('Subject qualification assigned');
      setSelectedSubjectId('');
      fetchDetails();
      onUpdated?.();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to assign subject');
    } finally {
      setIsAddingSubject(false);
    }
  };

  const handleRemoveSubject = async (subjectId, mediumId) => {
    try {
      await teacherService.removeSubject(teacher.id, subjectId, mediumId);
      toast.success('Subject qualification removed');
      fetchDetails();
      onUpdated?.();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to remove subject');
    }
  };

  // Availability toggle
  const getSlotAvailability = (slotId) => {
    return teacherData?.availabilities?.find((a) => a.timeSlotId === slotId);
  };

  const handleToggleSlot = async (slot) => {
    if (slot.slotType !== 'TEACHING') return;
    setIsUpdatingSlot(slot.id);
    const existing = getSlotAvailability(slot.id);

    try {
      if (!existing) {
        // None -> UNAVAILABLE
        await teacherService.setAvailability(teacher.id, slot.id, 'UNAVAILABLE');
      } else if (existing.kind === 'UNAVAILABLE') {
        // UNAVAILABLE -> PREFERRED
        await teacherService.setAvailability(teacher.id, slot.id, 'PREFERRED');
      } else {
        // PREFERRED -> Removed
        await teacherService.removeAvailability(teacher.id, slot.id);
      }
      fetchDetails();
      onUpdated?.();
    } catch (err) {
      toast.error('Failed to update availability constraint');
    } finally {
      setIsUpdatingSlot(null);
    }
  };

  const dayNames = { 1: 'Mon', 2: 'Tue', 3: 'Wed', 4: 'Thu', 5: 'Fri', 6: 'Sat', 7: 'Sun' };
  const mediumNames = { 1: 'Sinhala', 2: 'Tamil', 3: 'English' };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`${teacher?.fullName || 'Teacher'} - Profile & Availability`}
      description="Manage subject competencies and weekly slot preferences."
      maxWidth="max-w-2xl"
    >
      {/* Tab Switcher */}
      <div className="flex border-b border-slate-200 mb-5">
        <button
          type="button"
          onClick={() => setActiveTab('subjects')}
          className={`flex items-center gap-2 px-4 py-2.5 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'subjects'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          <BookOpen className="w-4 h-4" />
          Subject Qualifications ({teacherData?.subjects?.length || 0})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('availability')}
          className={`flex items-center gap-2 px-4 py-2.5 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'availability'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          <Clock className="w-4 h-4" />
          Weekly Availability Grid
        </button>
      </div>

      {loading ? (
        <div className="flex justify-center py-12">
          <Spinner size="lg" />
        </div>
      ) : activeTab === 'subjects' ? (
        <div className="space-y-5">
          {/* Add Subject Qualification */}
          <form onSubmit={handleAddSubject} className="p-4 rounded-xl bg-slate-50 border border-slate-200">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700 mb-3">
              Assign New Subject Qualification
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 items-end">
              <Select
                label="Subject"
                value={selectedSubjectId}
                onChange={(e) => setSelectedSubjectId(e.target.value)}
                options={allSubjects.map((s) => ({
                  value: String(s.id),
                  label: `${s.code} - ${s.nameEn}`,
                }))}
                placeholder="Choose subject..."
                required
              />
              <Select
                label="Medium"
                value={selectedMediumId}
                onChange={(e) => setSelectedMediumId(e.target.value)}
                options={[
                  { value: '1', label: 'Sinhala' },
                  { value: '2', label: 'Tamil' },
                  { value: '3', label: 'English' },
                ]}
                required
              />
              <Button
                type="submit"
                variant="primary"
                icon={Plus}
                isLoading={isAddingSubject}
                disabled={!selectedSubjectId}
              >
                Assign
              </Button>
            </div>
          </form>

          {/* Qualified Subjects List */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">
              Assigned Subject Qualifications
            </h4>
            {teacherData?.subjects?.length === 0 ? (
              <p className="text-xs text-slate-400 italic">No subjects qualified yet.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                {teacherData?.subjects?.map((ts) => (
                  <div
                    key={`${ts.subjectId}-${ts.mediumId}`}
                    className="flex items-center justify-between p-3 rounded-xl border border-slate-200 bg-white shadow-2xs"
                  >
                    <div>
                      <span className="font-bold text-xs text-slate-900 block">
                        {ts.subjectCode} - {ts.subjectName}
                      </span>
                      <span className="text-[11px] text-slate-500">
                        Medium: {mediumNames[ts.mediumId] || ts.mediumCode}
                      </span>
                    </div>
                    <button
                      type="button"
                      onClick={() => handleRemoveSubject(ts.subjectId, ts.mediumId)}
                      className="text-slate-400 hover:text-rose-600 p-1 rounded-lg"
                      title="Remove qualification"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      ) : (
        <div className="space-y-4">
          <div className="text-xs text-slate-500 flex items-center justify-between">
            <span>Click any teaching period to toggle availability constraint:</span>
            <div className="flex items-center gap-2 text-[11px]">
              <span className="inline-flex items-center gap-1 font-semibold text-rose-700 bg-rose-50 px-2 py-0.5 rounded border border-rose-200">
                Unavailable
              </span>
              <span className="inline-flex items-center gap-1 font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                Preferred
              </span>
              <span className="text-slate-400">Available (Default)</span>
            </div>
          </div>

          <div className="overflow-x-auto border border-slate-200 rounded-xl p-2 bg-white">
            <div className="grid grid-cols-6 gap-1 min-w-[500px] text-center text-xs">
              <div className="font-bold text-slate-400 p-1.5">Period</div>
              {[1, 2, 3, 4, 5].map((d) => (
                <div key={d} className="font-bold text-slate-700 p-1.5 bg-slate-50 rounded">
                  {dayNames[d]}
                </div>
              ))}

              {[1, 2, 3, 4, 5, 6, 7, 8].map((period) => (
                <React.Fragment key={period}>
                  <div className="font-bold text-slate-500 py-2 flex items-center justify-center">
                    P{period}
                  </div>
                  {[1, 2, 3, 4, 5].map((day) => {
                    const slot = timeSlots.find(
                      (s) => s.dayOfWeek === day && s.periodNo === period
                    );
                    if (!slot) {
                      return <div key={`${day}-${period}`} className="bg-slate-50/50 rounded" />;
                    }

                    const avail = getSlotAvailability(slot.id);
                    let color = 'bg-white hover:bg-slate-50 border-slate-200 text-slate-700';
                    let label = 'Avail';

                    if (avail?.kind === 'UNAVAILABLE') {
                      color = 'bg-rose-100 hover:bg-rose-200 border-rose-300 text-rose-800 font-bold';
                      label = 'Unavail';
                    } else if (avail?.kind === 'PREFERRED') {
                      color = 'bg-emerald-100 hover:bg-emerald-200 border-emerald-300 text-emerald-800 font-bold';
                      label = 'Preferred';
                    }

                    return (
                      <button
                        key={`${day}-${period}`}
                        type="button"
                        onClick={() => handleToggleSlot(slot)}
                        disabled={slot.slotType !== 'TEACHING'}
                        className={`p-2 rounded-lg border text-xs transition-all cursor-pointer ${color} ${
                          slot.slotType !== 'TEACHING' ? 'opacity-40 cursor-not-allowed bg-slate-100' : ''
                        }`}
                      >
                        {slot.slotType !== 'TEACHING' ? slot.slotType.substring(0, 4) : label}
                      </button>
                    );
                  })}
                </React.Fragment>
              ))}
            </div>
          </div>
        </div>
      )}

      <div className="flex justify-end pt-5 border-t border-slate-100 mt-6">
        <Button variant="secondary" onClick={onClose}>
          Done
        </Button>
      </div>
    </Modal>
  );
};

export default TeacherAvailabilityModal;
