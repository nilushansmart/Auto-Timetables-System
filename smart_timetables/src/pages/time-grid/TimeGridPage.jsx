import React, { useState, useEffect } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Table, { TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import { useToast } from '../../hooks/useToast';
import { Plus, Clock, Trash2, Edit, CheckCircle } from 'lucide-react';
import timeSlotService from '../../services/timeSlotService';

export const TimeGridPage = () => {
  const [timeSlots, setTimeSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingSlot, setEditingSlot] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [isSaving, setIsSaving] = useState(false);

  const [formData, setFormData] = useState({
    dayOfWeek: '1',
    periodNo: '1',
    startTime: '07:50:00',
    endTime: '08:30:00',
    slotType: 'TEACHING',
  });

  const toast = useToast();

  const dayNames = {
    1: 'Monday',
    2: 'Tuesday',
    3: 'Wednesday',
    4: 'Thursday',
    5: 'Friday',
    6: 'Saturday',
    7: 'Sunday',
  };

  const slotTypeBadges = {
    TEACHING: 'indigo',
    INTERVAL: 'amber',
    ASSEMBLY: 'emerald',
    HOMEROOM: 'sky',
  };

  const fetchSlots = async () => {
    setLoading(true);
    try {
      const data = await timeSlotService.getTimeSlots();
      setTimeSlots(data.content || []);
    } catch (err) {
      toast.error('Failed to load time slots');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSlots();
  }, []);

  const handleOpenCreate = () => {
    setEditingSlot(null);
    setFormData({
      dayOfWeek: '1',
      periodNo: String((timeSlots.length % 8) + 1),
      startTime: '07:50:00',
      endTime: '08:30:00',
      slotType: 'TEACHING',
    });
    setModalOpen(true);
  };

  const handleOpenEdit = (slot) => {
    setEditingSlot(slot);
    setFormData({
      dayOfWeek: String(slot.dayOfWeek),
      periodNo: String(slot.periodNo),
      startTime: slot.startTime,
      endTime: slot.endTime,
      slotType: slot.slotType,
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    try {
      const payload = {
        dayOfWeek: Number(formData.dayOfWeek),
        periodNo: Number(formData.periodNo),
        startTime: formData.startTime,
        endTime: formData.endTime,
        slotType: formData.slotType,
      };

      if (editingSlot) {
        await timeSlotService.updateTimeSlot(editingSlot.id, payload);
        toast.success('Time slot updated');
      } else {
        await timeSlotService.createTimeSlot(payload);
        toast.success('Time slot created');
      }
      setModalOpen(false);
      fetchSlots();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save time slot');
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setIsSaving(true);
    try {
      await timeSlotService.deleteTimeSlot(deleteTarget.id);
      toast.success('Time slot deleted');
      setDeleteTarget(null);
      fetchSlots();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete time slot');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Weekly Time Grid Setup"
        subtitle="Define periods, school days, teaching slots, intervals and assemblies for the school timetable."
      >
        <Button variant="primary" icon={Plus} onClick={handleOpenCreate}>
          Add Time Slot
        </Button>
      </PageHeader>

      {loading ? (
        <div className="flex justify-center py-16">
          <Spinner size="lg" />
        </div>
      ) : timeSlots.length === 0 ? (
        <EmptyState
          icon={Clock}
          title="No Time Slots Defined"
          description="Create your school's weekly periods (e.g. Monday Period 1 to 8, interval) to generate the timetable grid."
          actionLabel="Create First Time Slot"
          onAction={handleOpenCreate}
        />
      ) : (
        <Table>
          <TableHeader>
            <tr>
              <TableHead>Day</TableHead>
              <TableHead>Period</TableHead>
              <TableHead>Start Time</TableHead>
              <TableHead>End Time</TableHead>
              <TableHead>Slot Type</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </tr>
          </TableHeader>
          <TableBody>
            {timeSlots.map((slot) => (
              <TableRow key={slot.id}>
                <TableCell className="font-bold text-slate-800">
                  {dayNames[slot.dayOfWeek] || `Day ${slot.dayOfWeek}`}
                </TableCell>
                <TableCell className="font-medium text-slate-700">
                  Period {slot.periodNo}
                </TableCell>
                <TableCell>{slot.startTime?.substring(0, 5)}</TableCell>
                <TableCell>{slot.endTime?.substring(0, 5)}</TableCell>
                <TableCell>
                  <Badge variant={slotTypeBadges[slot.slotType] || 'slate'}>
                    {slot.slotType}
                  </Badge>
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex items-center justify-end gap-1.5">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleOpenEdit(slot)}
                      icon={Edit}
                      className="text-slate-600 hover:text-indigo-600"
                    >
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => setDeleteTarget(slot)}
                      icon={Trash2}
                      className="text-slate-400 hover:text-rose-600"
                    >
                      Delete
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      {/* Create / Edit Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingSlot ? 'Edit Time Slot' : 'Add New Time Slot'}
        description="Configure period timing and slot categorization."
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <Select
              label="Day of Week"
              value={formData.dayOfWeek}
              onChange={(e) => setFormData({ ...formData, dayOfWeek: e.target.value })}
              options={[
                { value: '1', label: 'Monday' },
                { value: '2', label: 'Tuesday' },
                { value: '3', label: 'Wednesday' },
                { value: '4', label: 'Thursday' },
                { value: '5', label: 'Friday' },
                { value: '6', label: 'Saturday' },
                { value: '7', label: 'Sunday' },
              ]}
              required
            />
            <Input
              label="Period Number"
              type="number"
              min="1"
              max="15"
              value={formData.periodNo}
              onChange={(e) => setFormData({ ...formData, periodNo: e.target.value })}
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Start Time"
              type="time"
              step="1"
              value={formData.startTime}
              onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
              required
            />
            <Input
              label="End Time"
              type="time"
              step="1"
              value={formData.endTime}
              onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
              required
            />
          </div>

          <Select
            label="Slot Type"
            value={formData.slotType}
            onChange={(e) => setFormData({ ...formData, slotType: e.target.value })}
            options={[
              { value: 'TEACHING', label: 'TEACHING (Normal subject period)' },
              { value: 'INTERVAL', label: 'INTERVAL (Tea / Lunch break)' },
              { value: 'ASSEMBLY', label: 'ASSEMBLY (School assembly)' },
              { value: 'HOMEROOM', label: 'HOMEROOM (Class roll call)' },
            ]}
            required
          />

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" onClick={() => setModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" isLoading={isSaving}>
              {editingSlot ? 'Save Changes' : 'Create Time Slot'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Time Slot?"
        message={`Are you sure you want to delete ${dayNames[deleteTarget?.dayOfWeek]} Period ${deleteTarget?.periodNo}?`}
        isLoading={isSaving}
      />
    </div>
  );
};

export default TimeGridPage;
