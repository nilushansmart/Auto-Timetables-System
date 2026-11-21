import React, { useState, useEffect } from 'react';
import Select from '../../components/common/Select';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import TimetableSlotCell from '../../components/timetable/TimetableSlotCell';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/useToast';
import { GraduationCap } from 'lucide-react';
import masterService from '../../services/masterService';
import timetableService from '../../services/timetableService';

export const ClassTimetableView = ({
  versionId,
  timeSlots = [],
  refreshKey,
  onMoveEntry,
  onMoveClick,
  onDeleteClick,
}) => {
  const [classes, setClasses] = useState([]);
  const [selectedClassId, setSelectedClassId] = useState('');
  const [entries, setEntries] = useState([]);
  const [loading, setLoading] = useState(false);

  const { isTeacher } = useAuth();
  const toast = useToast();

  useEffect(() => {
    const fetchClasses = async () => {
      try {
        const clsData = await masterService.getClasses();
        const list = Array.isArray(clsData) ? clsData : clsData.content || [];
        setClasses(list);
        if (list.length > 0 && !selectedClassId) {
          setSelectedClassId(String(list[0].id));
        }
      } catch (err) {
        toast.error('Failed to load classes');
      }
    };
    fetchClasses();
  }, []);

  useEffect(() => {
    const fetchEntries = async () => {
      if (!versionId || !selectedClassId) return;
      setLoading(true);
      try {
        const data = await timetableService.getEntriesByClass(
          Number(versionId),
          Number(selectedClassId)
        );
        setEntries(data || []);
      } catch (err) {
        toast.error('Failed to load class timetable entries');
      } finally {
        setLoading(false);
      }
    };
    fetchEntries();
  }, [versionId, selectedClassId, refreshKey]);

  const days = [
    { num: 1, name: 'Monday' },
    { num: 2, name: 'Tuesday' },
    { num: 3, name: 'Wednesday' },
    { num: 4, name: 'Thursday' },
    { num: 5, name: 'Friday' },
  ];

  // Distinct periods across teaching slots
  const periods = Array.from(new Set(timeSlots.map((s) => s.periodNo))).sort((a, b) => a - b);

  return (
    <div className="space-y-4">
      {/* Selector */}
      <div className="flex items-center gap-3 max-w-xs">
        <Select
          label="Select Class Group"
          value={selectedClassId}
          onChange={(e) => setSelectedClassId(e.target.value)}
          options={classes.map((c) => ({
            value: String(c.id),
            label: `${c.name} (Grade ${c.gradeNo})`,
          }))}
          placeholder="Choose Class"
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-20">
          <Spinner size="lg" />
        </div>
      ) : !selectedClassId ? (
        <EmptyState
          icon={GraduationCap}
          title="Select a Class Group"
          description="Choose a class from the dropdown above to view their weekly schedule."
        />
      ) : (
        <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white shadow-xs p-4">
          <div className="min-w-[800px]">
            {/* Header row: Periods */}
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

            {/* Matrix Rows: Days */}
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
                        perspective="class"
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

export default ClassTimetableView;
