import React, { useState, useEffect } from 'react';
import Select from '../../components/common/Select';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import TimetableSlotCell from '../../components/timetable/TimetableSlotCell';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/useToast';
import { DoorOpen } from 'lucide-react';
import timetableService from '../../services/timetableService';

export const RoomTimetableView = ({
  versionId,
  timeSlots = [],
  rooms = [],
  refreshKey,
  onMoveEntry,
  onMoveClick,
  onDeleteClick,
}) => {
  const [selectedRoomId, setSelectedRoomId] = useState('');
  const [entries, setEntries] = useState([]);
  const [loading, setLoading] = useState(false);

  const { isTeacher } = useAuth();
  const toast = useToast();

  useEffect(() => {
    if (rooms.length > 0 && !selectedRoomId) {
      setSelectedRoomId(String(rooms[0].id));
    }
  }, [rooms]);

  useEffect(() => {
    const fetchEntries = async () => {
      if (!versionId || !selectedRoomId) return;
      setLoading(true);
      try {
        const data = await timetableService.getEntriesByRoom(
          Number(versionId),
          Number(selectedRoomId)
        );
        setEntries(data || []);
      } catch (err) {
        toast.error('Failed to load room timetable entries');
      } finally {
        setLoading(false);
      }
    };
    fetchEntries();
  }, [versionId, selectedRoomId, refreshKey]);

  const days = [
    { num: 1, name: 'Monday' },
    { num: 2, name: 'Tuesday' },
    { num: 3, name: 'Wednesday' },
    { num: 4, name: 'Thursday' },
    { num: 5, name: 'Friday' },
  ];

  const periods = Array.from(new Set(timeSlots.map((s) => s.periodNo))).sort((a, b) => a - b);

  return (
    <div className="space-y-4">
      <div className="max-w-xs">
        <Select
          label="Select Room / Facility"
          value={selectedRoomId}
          onChange={(e) => setSelectedRoomId(e.target.value)}
          options={rooms.map((r) => ({
            value: String(r.id),
            label: `${r.name} (${r.type})`,
          }))}
          placeholder="Choose Room"
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-20">
          <Spinner size="lg" />
        </div>
      ) : !selectedRoomId ? (
        <EmptyState
          icon={DoorOpen}
          title="Select a Room"
          description="Choose a physical classroom or laboratory to inspect its schedule."
        />
      ) : (
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
                          onMoveEntry(draggedEntry, targetSlot.id, Number(selectedRoomId))
                        }
                        canEdit={!isTeacher()}
                        onMoveClick={onMoveClick}
                        onDeleteClick={onDeleteClick}
                        perspective="room"
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

export default RoomTimetableView;
