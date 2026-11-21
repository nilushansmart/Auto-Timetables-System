import React from 'react';
import { Pin, MoreVertical, Move, Trash2 } from 'lucide-react';

export const LessonCard = ({
  entry,
  canDrag = true,
  onMoveClick,
  onDeleteClick,
  perspective = 'class', // 'class' | 'teacher' | 'room'
}) => {
  const { lesson, room, isPinned } = entry;
  const subject = lesson?.subject;
  const teacher = lesson?.teacher;
  const classGroups = lesson?.classGroups || [];

  const handleDragStart = (e) => {
    if (!canDrag) return;
    e.dataTransfer.setData('application/json', JSON.stringify({ entry }));
    e.dataTransfer.effectAllowed = 'move';
  };

  // Generate a consistent pastel color based on subject code
  const getSubjectColor = (code = '') => {
    const colors = [
      'bg-indigo-50 border-indigo-200 text-indigo-900',
      'bg-emerald-50 border-emerald-200 text-emerald-900',
      'bg-amber-50 border-amber-200 text-amber-900',
      'bg-sky-50 border-sky-200 text-sky-900',
      'bg-purple-50 border-purple-200 text-purple-900',
      'bg-rose-50 border-rose-200 text-rose-900',
      'bg-teal-50 border-teal-200 text-teal-900',
    ];
    let hash = 0;
    for (let i = 0; i < code.length; i++) {
      hash = code.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % colors.length;
    return colors[index];
  };

  const colorClass = getSubjectColor(subject?.code);

  return (
    <div
      draggable={canDrag}
      onDragStart={handleDragStart}
      className={`group relative rounded-xl border p-2.5 text-xs shadow-xs transition-all select-none ${colorClass} ${
        canDrag ? 'cursor-grab active:cursor-grabbing hover:shadow-md hover:-translate-y-0.5' : ''
      }`}
    >
      <div className="flex items-start justify-between gap-1 mb-1">
        <span className="font-bold tracking-tight text-xs line-clamp-1">
          {subject?.code || subject?.nameEn || 'Lesson'}
        </span>
        <div className="flex items-center gap-1 shrink-0">
          {isPinned && <Pin className="w-3 h-3 text-amber-600 fill-amber-500" />}
          {canDrag && (
            <div className="opacity-0 group-hover:opacity-100 transition-opacity flex items-center gap-0.5">
              {onMoveClick && (
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    onMoveClick(entry);
                  }}
                  className="p-1 rounded hover:bg-black/5 text-slate-600 cursor-pointer"
                  title="Move to another slot"
                >
                  <Move className="w-3 h-3" />
                </button>
              )}
              {onDeleteClick && (
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    onDeleteClick(entry);
                  }}
                  className="p-1 rounded hover:bg-rose-100 text-rose-600 cursor-pointer"
                  title="Remove placement"
                >
                  <Trash2 className="w-3 h-3" />
                </button>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="space-y-0.5 text-[11px] opacity-90 leading-tight">
        {perspective !== 'teacher' && teacher && (
          <div className="font-medium text-slate-700 truncate">
            {teacher.callingName || teacher.fullName}
          </div>
        )}
        {perspective !== 'class' && classGroups.length > 0 && (
          <div className="font-medium text-slate-600 truncate">
            {classGroups.map((c) => c.name).join(', ')}
          </div>
        )}
        {perspective !== 'room' && room && (
          <div className="text-[10px] text-slate-500 truncate">
            {room.name}
          </div>
        )}
      </div>
    </div>
  );
};

export default LessonCard;
