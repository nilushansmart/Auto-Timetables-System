package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.timetable.ClashCheckResult;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.model.entity.*;
import me.nilushan.timetables_be.model.enums.AvailabilityKind;
import me.nilushan.timetables_be.model.enums.SlotType;
import me.nilushan.timetables_be.repository.*;
import me.nilushan.timetables_be.service.ClashDetectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ClashDetectionServiceImpl implements ClashDetectionService {

    private final TimetableEntryRepository timetableEntryRepository;
    private final LessonRepository lessonRepository;
    private final LessonClassRepository lessonClassRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final RoomRepository roomRepository;
    private final ClassGroupRepository classGroupRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateNoClash(Long schoolId, Long versionId, Long lessonId, Long timeSlotId, Long roomId, Long excludeEntryId) {
        ClashCheckResult result = checkClash(schoolId, versionId, lessonId, timeSlotId, roomId, excludeEntryId);
        if (result.hasClash()) {
            throw new ConflictException(result.reason());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClashCheckResult checkClash(Long schoolId, Long versionId, Long lessonId, Long timeSlotId, Long roomId, Long excludeEntryId) {
        TimeSlot slot = timeSlotRepository.findBySchoolIdAndId(schoolId, timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", timeSlotId));

        // 1. Teaching Slot Check (FR-5.7)
        if (slot.getSlotType() != SlotType.TEACHING) {
            return ClashCheckResult.conflict(
                    "NON_TEACHING_SLOT",
                    String.format("Cannot place lesson in day %d period %d: slot type is %s (only TEACHING slots are allowed)",
                            slot.getDayOfWeek(), slot.getPeriodNo(), slot.getSlotType())
            );
        }

        Lesson targetLesson = lessonRepository.findBySchoolIdAndId(schoolId, lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

        Teacher teacher = teacherRepository.findBySchoolIdAndId(schoolId, targetLesson.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", targetLesson.getTeacherId()));

        // 2. Teacher Availability Check (FR-5.5)
        Optional<TeacherAvailability> availabilityOpt = teacherAvailabilityRepository
                .findByIdTeacherIdAndIdTimeSlotId(teacher.getId(), slot.getId());
        if (availabilityOpt.isPresent() && availabilityOpt.get().getKind() == AvailabilityKind.UNAVAILABLE) {
            return ClashCheckResult.conflict(
                    "TEACHER_UNAVAILABLE",
                    String.format("Teacher %s (%s) is marked UNAVAILABLE on day %d period %d",
                            teacher.getFullName(), teacher.getShortCode() != null ? teacher.getShortCode() : "",
                            slot.getDayOfWeek(), slot.getPeriodNo())
            );
        }

        // Get all other entries in this version and time slot
        List<TimetableEntry> slotEntries = timetableEntryRepository.findByVersionIdAndTimeSlotId(versionId, timeSlotId)
                .stream()
                .filter(e -> excludeEntryId == null || !e.getId().equals(excludeEntryId))
                .toList();

        // 3. Teacher Double-Booking Check (FR-5.1)
        for (TimetableEntry entry : slotEntries) {
            Lesson existingLesson = lessonRepository.findById(entry.getLessonId()).orElse(null);
            if (existingLesson != null && existingLesson.getTeacherId().equals(targetLesson.getTeacherId())) {
                Subject existingSubject = subjectRepository.findById(existingLesson.getSubjectId()).orElse(null);
                String subName = existingSubject != null ? existingSubject.getNameEn() : "Lesson " + existingLesson.getId();
                return ClashCheckResult.conflict(
                        "TEACHER_DOUBLE_BOOKING",
                        String.format("Teacher clash: %s is already scheduled for '%s' on day %d period %d",
                                teacher.getFullName(), subName, slot.getDayOfWeek(), slot.getPeriodNo())
                );
            }
        }

        // 4. Room Double-Booking Check (FR-5.3)
        if (roomId != null) {
            Room room = roomRepository.findBySchoolIdAndId(schoolId, roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room", roomId));

            for (TimetableEntry entry : slotEntries) {
                if (entry.getRoomId() != null && entry.getRoomId().equals(roomId)) {
                    return ClashCheckResult.conflict(
                            "ROOM_DOUBLE_BOOKING",
                            String.format("Room clash: Room '%s' is already occupied on day %d period %d",
                                    room.getName(), slot.getDayOfWeek(), slot.getPeriodNo())
                    );
                }
            }
        }

        // 5. Class Double-Booking & Parallel Lesson Group Exception (FR-5.2)
        List<Long> targetClassGroupIds = lessonClassRepository.findByIdLessonId(targetLesson.getId()).stream()
                .map(lc -> lc.getId().getClassGroupId())
                .toList();

        for (TimetableEntry entry : slotEntries) {
            Lesson existingLesson = lessonRepository.findById(entry.getLessonId()).orElse(null);
            if (existingLesson == null) continue;

            List<Long> existingClassGroupIds = lessonClassRepository.findByIdLessonId(existingLesson.getId()).stream()
                    .map(lc -> lc.getId().getClassGroupId())
                    .toList();

            Set<Long> sharedClassIds = new HashSet<>(targetClassGroupIds);
            sharedClassIds.retainAll(existingClassGroupIds);

            if (!sharedClassIds.isEmpty()) {
                // Lessons in the same lesson_group may share a slot for the same class (e.g. religion, basket)
                boolean sameGroup = targetLesson.getLessonGroupId() != null
                        && existingLesson.getLessonGroupId() != null
                        && targetLesson.getLessonGroupId().equals(existingLesson.getLessonGroupId());

                if (!sameGroup) {
                    Long conflictingClassId = sharedClassIds.iterator().next();
                    ClassGroup conflictingClass = classGroupRepository.findById(conflictingClassId).orElse(null);
                    String className = conflictingClass != null ? conflictingClass.getName() : "Class " + conflictingClassId;
                    Subject existingSubject = subjectRepository.findById(existingLesson.getSubjectId()).orElse(null);
                    String subName = existingSubject != null ? existingSubject.getNameEn() : "Lesson " + existingLesson.getId();

                    return ClashCheckResult.conflict(
                            "CLASS_DOUBLE_BOOKING",
                            String.format("Class clash: %s is already scheduled for '%s' on day %d period %d",
                                    className, subName, slot.getDayOfWeek(), slot.getPeriodNo())
                    );
                }
            }
        }

        return ClashCheckResult.ok();
    }
}
