import React, { useState, useEffect, useCallback } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Select from '../../components/common/Select';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import ClashAlertModal from '../../components/timetable/ClashAlertModal';
import MoveEntryModal from '../../components/timetable/MoveEntryModal';
import ClassTimetableView from './ClassTimetableView';
import TeacherTimetableView from './TeacherTimetableView';
import RoomTimetableView from './RoomTimetableView';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/useToast';
import { CalendarDays, Users, GraduationCap, DoorOpen, Plus, Sparkles } from 'lucide-react';
import timetableService from '../../services/timetableService';
import timeSlotService from '../../services/timeSlotService';
import masterService from '../../services/masterService';
import teacherService from '../../services/teacherService';
import academicService from '../../services/academicService';

export const TimetablePage = () => {
  const { user, isTeacher } = useAuth();
  const toast = useToast();

  const [activeTab, setActiveTab] = useState('class'); // 'class' | 'teacher' | 'room'
  const [versions, setVersions] = useState([]);
  const [selectedVersionId, setSelectedVersionId] = useState('');
  const [timeSlots, setTimeSlots] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);

  // Clash & Move modals
  const [clashModalOpen, setClashModalOpen] = useState(false);
  const [clashMessage, setClashMessage] = useState('');
  const [moveModalOpen, setMoveModalOpen] = useState(false);
  const [movingEntry, setMovingEntry] = useState(null);
  const [isMoving, setIsMoving] = useState(false);

  // Entries refresh trigger
  const [refreshKey, setRefreshKey] = useState(0);

  const initialLoad = async () => {
    setLoading(true);
    try {
      const [vData, slotData, roomData] = await Promise.all([
        timetableService.getVersions(),
        timeSlotService.getTimeSlots(),
        masterService.getRooms({ size: 100 }),
      ]);

      const versionList = Array.isArray(vData) ? vData : vData.content || [];
      setVersions(versionList);
      if (versionList.length > 0) {
        setSelectedVersionId(String(versionList[0].id));
      }

      setTimeSlots(slotData.content || []);
      setRooms(roomData.content || []);

      // If user is a teacher, default to teacher tab
      if (isTeacher()) {
        setActiveTab('teacher');
      }
    } catch (err) {
      toast.error('Failed to initialize timetable viewer');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    initialLoad();
  }, []);

  // Move handler used by both Drag-and-Drop and MoveEntryModal
  const handleMoveEntry = async (entry, targetSlotId, targetRoomId = null) => {
    if (!selectedVersionId) return;
    setIsMoving(true);

    const payload = {
      lessonId: entry.lesson.id,
      timeSlotId: targetSlotId,
      roomId: targetRoomId !== null ? targetRoomId : (entry.room?.id || null),
      isPinned: entry.isPinned || false,
    };

    try {
      await timetableService.updateEntry(Number(selectedVersionId), entry.id, payload);
      toast.success('Lesson moved successfully');
      setMoveModalOpen(false);
      setRefreshKey((k) => k + 1); // trigger reload of grid
    } catch (err) {
      if (err.response?.status === 409) {
        const errorMsg =
          err.response.data?.message ||
          'Clash detected: Double-booking in this time slot.';
        setClashMessage(errorMsg);
        setClashModalOpen(true);
      } else {
        toast.error(err.response?.data?.message || 'Failed to move lesson');
      }
      setRefreshKey((k) => k + 1); // reload to revert any optimistic change
    } finally {
      setIsMoving(false);
    }
  };

  const handleDeleteEntry = async (entry) => {
    if (!selectedVersionId) return;
    try {
      await timetableService.deleteEntry(Number(selectedVersionId), entry.id);
      toast.success('Lesson placement removed');
      setRefreshKey((k) => k + 1);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to remove placement');
    }
  };

  const handleOpenMoveModal = (entry) => {
    setMovingEntry(entry);
    setMoveModalOpen(true);
  };

  const activeVersion = versions.find((v) => String(v.id) === selectedVersionId);

  return (
    <div className="space-y-6">
      <PageHeader
        title="Interactive Timetable Viewer"
        subtitle="Review weekly schedules by Class, Teacher, or Room with conflict-free drag-and-drop adjustments."
      >
        <div className="flex items-center gap-3">
          {versions.length > 0 && (
            <div className="w-56">
              <Select
                value={selectedVersionId}
                onChange={(e) => setSelectedVersionId(e.target.value)}
                options={versions.map((v) => ({
                  value: String(v.id),
                  label: `${v.name || `Version #${v.versionNumber}`} (${v.status})`,
                }))}
              />
            </div>
          )}
        </div>
      </PageHeader>

      {/* View Perspective Tabs */}
      <div className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-200">
        <div className="flex items-center gap-1 sm:gap-2">
          <button
            type="button"
            onClick={() => setActiveTab('class')}
            className={`flex items-center gap-2 px-4 py-3 text-sm font-bold border-b-2 transition-all cursor-pointer ${
              activeTab === 'class'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <GraduationCap className="w-4 h-4" />
            <span>Class Timetable</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('teacher')}
            className={`flex items-center gap-2 px-4 py-3 text-sm font-bold border-b-2 transition-all cursor-pointer ${
              activeTab === 'teacher'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Users className="w-4 h-4" />
            <span>Teacher Schedule</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('room')}
            className={`flex items-center gap-2 px-4 py-3 text-sm font-bold border-b-2 transition-all cursor-pointer ${
              activeTab === 'room'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <DoorOpen className="w-4 h-4" />
            <span>Room Utilization</span>
          </button>
        </div>

        {activeVersion && (
          <div className="text-xs text-slate-500 pb-2 flex items-center gap-2">
            <span className="font-semibold text-slate-700">{activeVersion.name}</span>
            <span className="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500" />
            <span>Drag lessons to adjust slots</span>
          </div>
        )}
      </div>

      {loading ? (
        <div className="flex justify-center py-20">
          <Spinner size="lg" />
        </div>
      ) : versions.length === 0 ? (
        <EmptyState
          icon={CalendarDays}
          title="No Timetable Versions Created"
          description="Create a timetable draft version for a school term to view the schedule grid."
        />
      ) : (
        <div>
          {activeTab === 'class' && (
            <ClassTimetableView
              versionId={selectedVersionId}
              timeSlots={timeSlots}
              refreshKey={refreshKey}
              onMoveEntry={handleMoveEntry}
              onMoveClick={handleOpenMoveModal}
              onDeleteClick={handleDeleteEntry}
            />
          )}

          {activeTab === 'teacher' && (
            <TeacherTimetableView
              versionId={selectedVersionId}
              timeSlots={timeSlots}
              refreshKey={refreshKey}
              onMoveEntry={handleMoveEntry}
              onMoveClick={handleOpenMoveModal}
              onDeleteClick={handleDeleteEntry}
            />
          )}

          {activeTab === 'room' && (
            <RoomTimetableView
              versionId={selectedVersionId}
              timeSlots={timeSlots}
              rooms={rooms}
              refreshKey={refreshKey}
              onMoveEntry={handleMoveEntry}
              onMoveClick={handleOpenMoveModal}
              onDeleteClick={handleDeleteEntry}
            />
          )}
        </div>
      )}

      {/* Conflict Modal on 409 */}
      <ClashAlertModal
        isOpen={clashModalOpen}
        onClose={() => setClashModalOpen(false)}
        clashMessage={clashMessage}
      />

      {/* Alternative Accessible / Mobile Move Modal */}
      <MoveEntryModal
        isOpen={moveModalOpen}
        onClose={() => setMoveModalOpen(false)}
        entry={movingEntry}
        timeSlots={timeSlots}
        rooms={rooms}
        onConfirmMove={(entry, targetSlotId, targetRoomId) =>
          handleMoveEntry(entry, targetSlotId, targetRoomId)
        }
        isLoading={isMoving}
      />
    </div>
  );
};

export default TimetablePage;
