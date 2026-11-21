import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Button from '../../components/common/Button';
import { useToast } from '../../hooks/useToast';
import masterService from '../../services/masterService';

export const RoomFormModal = ({ isOpen, onClose, room, onSuccess }) => {
  const [formData, setFormData] = useState({
    name: '',
    type: 'CLASSROOM',
    capacity: 45,
  });
  const [loading, setLoading] = useState(false);
  const toast = useToast();

  useEffect(() => {
    if (room) {
      setFormData({
        name: room.name || '',
        type: room.type || 'CLASSROOM',
        capacity: room.capacity || 45,
      });
    } else {
      setFormData({
        name: '',
        type: 'CLASSROOM',
        capacity: 45,
      });
    }
  }, [room, isOpen]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const payload = {
        name: formData.name,
        type: formData.type,
        capacity: formData.capacity ? Number(formData.capacity) : null,
      };

      if (room) {
        await masterService.updateRoom(room.id, payload);
        toast.success('Room updated');
      } else {
        await masterService.createRoom(payload);
        toast.success('Room created');
      }
      onSuccess();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save room');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={room ? 'Edit Room' : 'Add New Room'}
      description="Specify room label, facility categorization, and student seating limit."
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Room Name / Label"
          value={formData.name}
          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          placeholder="e.g. Room 101, Science Lab 1, ICT Lab"
          required
        />

        <div className="grid grid-cols-2 gap-3">
          <Select
            label="Room Type"
            value={formData.type}
            onChange={(e) => setFormData({ ...formData, type: e.target.value })}
            options={[
              { value: 'CLASSROOM', label: 'Classroom' },
              { value: 'SCIENCE_LAB', label: 'Science Laboratory' },
              { value: 'COMPUTER_LAB', label: 'Computer / ICT Lab' },
              { value: 'SPECIAL_ROOM', label: 'Special Room / Music' },
              { value: 'HALL', label: 'Main Assembly Hall' },
              { value: 'LIBRARY', label: 'Library' },
            ]}
            required
          />

          <Input
            label="Seating Capacity"
            type="number"
            min="1"
            max="300"
            value={formData.capacity}
            onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
            placeholder="e.g. 45"
          />
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" isLoading={loading}>
            {room ? 'Save Changes' : 'Create Room'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default RoomFormModal;
