import React, { useState, useEffect } from 'react';
import Select from '../../components/common/Select';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import TimetableSlotCell from '../../components/timetable/TimetableSlotCell';
import Badge from '../../components/common/Badge';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/useToast';
import { Users, Calendar, Smartphone, LayoutGrid } from 'lucide-react';
import teacherService from '../../services/teacherService';
import timetableService from '../../services/timetableService';

export const TeacherTimetableView = ({
  versionId,
  timeSlots = [],
  refreshKey,
  onMoveEntry,
  onMoveClick,
  onDeleteClick,
}) => {
  const { user, isTeacher } = useAuth();
  const [teachers, setTeachers] = useState([]);
  const [selectedTeacherId, setSelectedTeacherId] = useState('');
  const [entries, setEntries] = useState([]);
  const [loading, setLoading] = useState(false);
  const [mobileDay, setMobileDay] = useState(1); // 1 = Monday
  const [mobileView, setMobileView] = useState(false);

  const toast = useToast();

  useEffect(() => {
    const fetchTeachers = async () => {
      try {
        const tData = await teacherService.getTeachers({ size: 100 });
        const list = tData.content || [];
        setTeachers(list);

        // If logged-in user is a teacher, match by email or teacher ID
        if (isTeacher() && user?.email) {
          const matched = list.find((t) => t.email === user.email);
          if (matched) {
            setSelectedTeacherId(String(matched.id));
            return;
          }
        }

        if (list.length > 0 && !selectedTeacherId) {
          setSelectedTeacherId(String(list[0].id));
        }
      } catch (err) {
        toast.error('Failed to load teachers');
      }
    };
    fetchTeachers();
  }, [user]);

  useEffect(() => {
    const fetchEntries = async () => {
      if (!versionId || !selectedTeacherId) return;
      setLoading(true);
      try {
        const data = await timetableService.getEntriesByTeacher(
          Number(versionId),
          Number(selectedTeacherId)
        );
        setEntries(data || []);
      } catch (err) {
        toast.error('Failed to load teacher timetable entries');
      } finally {
        setLoading(false);
      }
    };
    fetchEntries();
  }, [versionId, selectedTeacherId, refreshKey]);

  const days = [
    { num: 1, name: 'Monday', short: 'Mon' },
    { num: 2, name: 'Tuesday', short: 'Tue' },
    { num: 3, name: 'Wednesday', short: 'Wed' },
    { num: 4, name: 'Thursday', short: 'Thu' },
    { num: 5, name: 'Friday', short: 'Fri' },
  ];

  const periods = Array.from(new Set(timeSlots.map((s) => s.periodNo))).sort((a, b) => a - b);

  const selectedTeacher = teachers.find((t) => String(t.id) === selectedTeacherId);

  return (
    <div className="space-y-4">
      {/* Controls */}
      <div className="flex flex-col sm:flex-row items-start sm:items-end justify-between gap-3">
        <div className="max-w-xs w-full">
          <Select
            label="Select Teacher"
            value={selectedTeacherId}
            onChange={(e) => setSelectedTeacherId(e.target.value)}
            options={teachers.map((t) => ({
              value: String(t.id),
              label: `${t.fullName} (${t.shortCode || 'Teacher'})`,
            }))}
            disabled={isTeacher() && !!selectedTeacherId}
            placeholder="Choose Teacher"
          />
        </div>

        {/* Mobile View Toggle */}
        <div className="flex items-center gap-2 text-xs font-semibold">
          <button
            type="button"
            onClick={() => setMobileView(false)}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg border transition-all cursor-pointer ${
              !mobileView
                ? 'bg-indigo-50 border-indigo-200 text-indigo-700'
                : 'bg-white border-slate-200 text-slate-500'
            }`}
          >
            <LayoutGrid className="w-4 h-4" />
            <span className="hidden sm:inline">Weekly Grid</span>
          </button>
          <button
            type="button"
            onClick={() => setMobileView(true)}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg border transition-all cursor-pointer ${
              mobileView
                ? 'bg-indigo-50 border-indigo-200 text-indigo-700'
                : 'bg-white border-slate-200 text-slate-500'
            }`}
          >
            <Smartphone className="w-4 h-4" />
            <span>Mobile Day Cards</span>
          </button>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center py-20">
          <Spinner size="lg" />
        </div>
      ) : !selectedTeacherId ? (
        <EmptyState
          icon={Users}
          title="Select a Teacher"
          description="Choose a teacher to inspect their weekly teaching timetable."
        />
      ) : mobileView ? (
        /* Mobile-Friendly Day-by-Day Card View */
        <div className="space-y-4">
          {/* Day Selector Pills */}
          <div className="flex items-center justify-between gap-1 p-1 bg-slate-100 rounded-2xl overflow-x-auto">
            {days.map((d) => (
              <button
                key={d.num}
                type="button"
                onClick={() => setMobileDay(d.num)}
                className={`flex-1 py-2 px-3 text-xs font-bold rounded-xl transition-all cursor-pointer text-center ${
                  mobileDay === d.num
                    ? 'bg-white text-indigo-700 shadow-xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {d.name}
              </button>
            ))}
          </div>

          {/* Cards for Mobile Day */}
          <div className="space-y-3">
            {periods.map((p) => {
              const slot = timeSlots.find(
                (s) => s.dayOfWeek === mobileDay && s.periodNo === p
              );
              if (!slot) return null;

              const slotEntries = entries.filter((e) => e.timeSlot?.id === slot.id);
              const isTeaching = slot.slotType === 'TEACHING';

              return (
                <div
                  key={slot.id}
                  className={`p-4 rounded-2xl border transition-all ${
                    !isTeaching
                      ? 'bg-slate-50 border-slate-200 opacity-75'
                      : slotEntries.length > 0
                      ? 'bg-white border-indigo-100 shadow-xs ring-1 ring-indigo-50'
                      : 'bg-white border-slate-200'
                  }`}
                >
                  <div className="flex items-center justify-between mb-2 pb-2 border-b border-slate-100">
                    <span className="font-bold text-xs text-slate-700">
                      Period {slot.periodNo}
                    </span>
                    <span className="text-xs font-mono text-slate-400">
                      {slot.startTime?.substring(0, 5)} - {slot.endTime?.substring(0, 5)}
                    </span>
                  </div>

                  {!isTeaching ? (
                    <div className="text-xs font-bold uppercase tracking-wider text-slate-400">
                      {slot.slotType}
                    </div>
                  ) : slotEntries.length > 0 ? (
                    slotEntries.map((e) => (
                      <div key={e.id} className="space-y-1">
                        <div className="flex items-center justify-between">
                          <span className="font-black text-sm text-indigo-950">
                            {e.lesson?.subject?.nameEn} ({e.lesson?.subject?.code})
                          </span>
                          {e.room && (
                            <Badge variant="emerald" size="sm">
                              {e.room.name}
                            </Badge>
                          )}
                        </div>
                        <div className="text-xs text-slate-600">
                          Class:{' '}
                          <span className="font-semibold text-slate-900">
                            {e.lesson?.classGroups?.map((c) => c.name).join(', ')}
                          </span>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="text-xs text-slate-400 italic">Free Period</div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      ) : (
        /* Full Weekly Desktop Grid */
        <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white shadow-xs p-4">
          <div className="min-w-[800px]">
            {/* Header row */}
            <div className="grid grid-cols-9 gap-2 pb-3 mb-2 border-b border-slate-100 text-center">
              <div className="font-bold text-xs uppercase tracking-wider text-slate-400 p-2">
                Day
              </div>
              {periods.map((p) => {
                const sampleSlot = timeSlots.find((s) => s.periodNo === p);
                return (
                  <div key={p} className="p-2 rounded-xl bg-slate-50 font-bold text-xs text-slate-700">
                    <div>Period {p}</div>
                    {sampleSlot && (
                      <div className="text-[10px] font-normal text-slate-400">
                        {sampleSlot.startTime?.substring(0, 5)} - {sampleSlot.endTime?.substring(0, 5)}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>

            {/* Matrix Rows */}
            <div className="space-y-2">
              {days.map((day) => (
                <div key={day.num} className="grid grid-cols-9 gap-2 items-stretch">
                  <div className="flex flex-col items-center justify-center p-2 rounded-xl bg-slate-50/80 border border-slate-100 font-bold text-xs text-slate-800">
                    <span>{day.name}</span>
                  </div>

                  {periods.map((p) => {
                    const slot = timeSlots.find(
                      (s) => s.dayOfWeek === day.num && s.periodNo === p
                    );

                    if (!slot) {
                      return (
                        <div
                          key={`${day.num}-${p}`}
                          className="min-h-[90px] rounded-xl bg-slate-50/30 border border-slate-100/50"
                        />
                      );
                    }

                    const slotEntries = entries.filter((e) => e.timeSlot?.id === slot.id);

                    return (
                      <TimetableSlotCell
                        key={slot.id}
                        dayOfWeek={day.num}
                        timeSlot={slot}
                        entries={slotEntries}
                        onDropEntry={(draggedEntry, targetSlot) =>
                          onMoveEntry(draggedEntry, targetSlot.id)
                        }
                        canEdit={!isTeacher()}
                        onMoveClick={onMoveClick}
                        onDeleteClick={onDeleteClick}
                        perspective="teacher"
                      />
                    );
                  })}
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TeacherTimetableView;
