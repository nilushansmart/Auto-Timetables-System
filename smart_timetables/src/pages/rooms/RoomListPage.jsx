import React, { useState, useEffect } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Table, { TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Input from '../../components/common/Input';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import RoomFormModal from './RoomFormModal';
import { useToast } from '../../hooks/useToast';
import { useDebounce } from '../../hooks/useDebounce';
import { Plus, DoorOpen, Edit, Trash2 } from 'lucide-react';
import masterService from '../../services/masterService';

export const RoomListPage = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 300);

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingRoom, setEditingRoom] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const toast = useToast();

  const fetchRooms = async () => {
    setLoading(true);
    try {
      const data = await masterService.getRooms({ size: 100 });
      setRooms(data.content || []);
    } catch (err) {
      toast.error('Failed to load rooms');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRooms();
  }, []);

  const filteredRooms = rooms.filter((r) => {
    if (!debouncedSearch) return true;
    const term = debouncedSearch.toLowerCase();
    return (
      r.name?.toLowerCase().includes(term) ||
      r.type?.toLowerCase().includes(term)
    );
  });

  const roomTypeBadges = {
    CLASSROOM: 'indigo',
    SCIENCE_LAB: 'emerald',
    COMPUTER_LAB: 'sky',
    SPECIAL_ROOM: 'purple',
    HALL: 'amber',
    LIBRARY: 'slate',
  };

  const handleOpenCreate = () => {
    setEditingRoom(null);
    setFormModalOpen(true);
  };

  const handleOpenEdit = (room) => {
    setEditingRoom(room);
    setFormModalOpen(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await masterService.deleteRoom(deleteTarget.id);
      toast.success('Room deleted');
      setDeleteTarget(null);
      fetchRooms();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete room');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Rooms & Laboratories"
        subtitle="Manage physical learning spaces, specialized science/computer labs, and room capacities."
      >
        <Button variant="primary" icon={Plus} onClick={handleOpenCreate}>
          Add Room
        </Button>
      </PageHeader>

      <div className="max-w-md">
        <Input
          placeholder="Search rooms by name or type..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-16">
          <Spinner size="lg" />
        </div>
      ) : filteredRooms.length === 0 ? (
        <EmptyState
          icon={DoorOpen}
          title="No Rooms Found"
          description={search ? 'No rooms match your search query.' : 'Add your school classrooms and specialized laboratories.'}
          actionLabel={search ? undefined : 'Add First Room'}
          onAction={search ? undefined : handleOpenCreate}
        />
      ) : (
        <Table>
          <TableHeader>
            <tr>
              <TableHead>Room Name</TableHead>
              <TableHead>Room Type</TableHead>
              <TableHead>Capacity</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </tr>
          </TableHeader>
          <TableBody>
            {filteredRooms.map((room) => (
              <TableRow key={room.id}>
                <TableCell className="font-bold text-slate-900">
                  {room.name}
                </TableCell>
                <TableCell>
                  <Badge variant={roomTypeBadges[room.type] || 'slate'}>
                    {room.type?.replace('_', ' ')}
                  </Badge>
                </TableCell>
                <TableCell className="font-semibold text-slate-700">
                  {room.capacity ? `${room.capacity} students` : '—'}
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex items-center justify-end gap-1.5">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleOpenEdit(room)}
                      icon={Edit}
                      className="text-slate-600 hover:text-indigo-600"
                    >
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => setDeleteTarget(room)}
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

      {/* Form Modal */}
      <RoomFormModal
        isOpen={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        room={editingRoom}
        onSuccess={fetchRooms}
      />

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Room?"
        message={`Are you sure you want to delete ${deleteTarget?.name}?`}
        isLoading={isDeleting}
      />
    </div>
  );
};

export default RoomListPage;
