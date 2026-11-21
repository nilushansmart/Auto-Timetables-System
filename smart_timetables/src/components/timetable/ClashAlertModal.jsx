import React from 'react';
import Modal from '../common/Modal';
import Button from '../common/Button';
import { AlertTriangle, Undo2 } from 'lucide-react';

export const ClashAlertModal = ({
  isOpen,
  onClose,
  clashMessage,
}) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} maxWidth="max-w-md">
      <div className="flex items-start gap-4 mb-4">
        <div className="w-12 h-12 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center shrink-0">
          <AlertTriangle className="w-6 h-6" />
        </div>
        <div>
          <h4 className="text-base font-bold text-slate-900">
            Timetable Conflict Detected
          </h4>
          <p className="text-xs text-slate-500 mt-0.5">
            The move was rejected by the system clash detection engine.
          </p>
        </div>
      </div>

      <div className="p-3.5 rounded-xl bg-amber-50 border border-amber-200 text-amber-950 text-sm font-medium leading-relaxed mb-5">
        {clashMessage || 'Double-booking clash detected for teacher, class, or room.'}
      </div>

      <div className="flex items-center justify-between pt-3 border-t border-slate-100">
        <div className="flex items-center gap-1.5 text-xs text-slate-500">
          <Undo2 className="w-4 h-4 text-slate-400" />
          <span>Lesson has been reverted to original slot</span>
        </div>
        <Button variant="primary" onClick={onClose}>
          Understood
        </Button>
      </div>
    </Modal>
  );
};

export default ClashAlertModal;
