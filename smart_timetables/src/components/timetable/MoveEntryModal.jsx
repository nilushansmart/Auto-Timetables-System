import React, { useState } from 'react';
import Modal from '../common/Modal';
import Select from '../common/Select';
import Button from '../common/Button';

export const MoveEntryModal = ({
  isOpen,
  onClose,
  entry,
  timeSlots = [],
  rooms = [],
  onConfirmMove,
  isLoading = false,
}) => {
  const [targetSlotId, setTargetSlotId] = useState('');
  const [targetRoomId, setTargetRoomId] = useState(entry?.room?.id ? String(entry.room.id) : '');

  const dayNames = {
    1: 'Monday',
    2: 'Tuesday',
    3: 'Wednesday',
    4: 'Thursday',
    5: 'Friday',
    6: 'Saturday',
    7: 'Sunday',
  };

  const slotOptions = timeSlots
    .filter((s) => s.slotType === 'TEACHING')
    .map((s) => ({
      value: String(s.id),
      label: `${dayNames[s.dayOfWeek] || `Day ${s.dayOfWeek}`} - Period ${s.periodNo} (${s.startTime?.substring(0, 5)} - ${s.endTime?.substring(0, 5)})`,
    }));

  const roomOptions = [
    { value: '', label: 'No Room Specified' },
    ...rooms.map((r) => ({
      value: String(r.id),
      label: `${r.name} (${r.type})`,
    })),
  ];

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!targetSlotId) return;
    onConfirmMove(
      entry,
      Number(targetSlotId),
      targetRoomId ? Number(targetRoomId) : null
    );
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Move Lesson Entry"
      description={`Relocate ${entry?.lesson?.subject?.nameEn || 'lesson'} to another weekly slot`}
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <Select
          label="Target Period / Slot"
          options={slotOptions}
          value={targetSlotId}
          onChange={(e) => setTargetSlotId(e.target.value)}
          required
          placeholder="Select Day & Period"
        />

        <Select
          label="Room Assignment (Optional)"
          options={roomOptions}
          value={targetRoomId}
          onChange={(e) => setTargetRoomId(e.target.value)}
          placeholder="Select Room"
        />

        <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
          <Button variant="outline" onClick={onClose} disabled={isLoading}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="primary"
            disabled={!targetSlotId}
            isLoading={isLoading}
          >
            Confirm Move
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default MoveEntryModal;
