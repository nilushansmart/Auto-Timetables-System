import React, { useState } from 'react';
import LessonCard from './LessonCard';
import { Plus } from 'lucide-react';

export const TimetableSlotCell = ({
  dayOfWeek,
  timeSlot,
  entries = [],
  onDropEntry,
  canEdit = true,
  onMoveClick,
  onDeleteClick,
  perspective = 'class',
}) => {
  const [isDragOver, setIsDragOver] = useState(false);

  const isTeaching = timeSlot.slotType === 'TEACHING';

  const handleDragOver = (e) => {
    if (!canEdit || !isTeaching) return;
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    setIsDragOver(true);
  };

  const handleDragLeave = () => {
    setIsDragOver(false);
  };

  const handleDrop = (e) => {
    if (!canEdit || !isTeaching) return;
    e.preventDefault();
    setIsDragOver(false);
    try {
      const dataStr = e.dataTransfer.getData('application/json');
      if (dataStr) {
        const { entry } = JSON.parse(dataStr);
        if (entry && entry.timeSlot?.id !== timeSlot.id) {
          onDropEntry(entry, timeSlot);
        }
      }
    } catch (err) {
      console.error('Failed to parse dropped entry data', err);
    }
  };

  if (!isTeaching) {
    return (
      <div className="h-full min-h-[90px] p-2 bg-slate-100/70 border border-slate-200/80 rounded-xl flex flex-col items-center justify-center text-center">
        <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">
          {timeSlot.slotType}
        </span>
        <span className="text-[10px] text-slate-400">
          {timeSlot.startTime?.substring(0, 5)} - {timeSlot.endTime?.substring(0, 5)}
        </span>
      </div>
    );
  }

  return (
    <div
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
      className={`min-h-[90px] p-1.5 rounded-xl border transition-all flex flex-col gap-1.5 ${
        isDragOver
          ? 'border-indigo-500 bg-indigo-50/70 ring-2 ring-indigo-200 scale-[1.01]'
          : 'border-slate-200 bg-white hover:border-slate-300'
      }`}
    >
      {entries.length > 0 ? (
        entries.map((entry) => (
          <LessonCard
            key={entry.id}
            entry={entry}
            canDrag={canEdit}
            onMoveClick={onMoveClick}
            onDeleteClick={onDeleteClick}
            perspective={perspective}
          />
        ))
      ) : (
        <div className="flex-1 flex items-center justify-center text-slate-300 text-xs italic">
          {isDragOver ? 'Drop lesson here' : 'Empty'}
        </div>
      )}
    </div>
  );
};

export default TimetableSlotCell;
