package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.timetable.TimetableEntryRequest;
import me.nilushan.timetables_be.dto.timetable.TimetableEntryResponse;
import me.nilushan.timetables_be.dto.timetable.TimetableVersionRequest;
import me.nilushan.timetables_be.dto.timetable.TimetableVersionResponse;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.*;
import me.nilushan.timetables_be.model.enums.VersionStatus;
import me.nilushan.timetables_be.repository.*;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.ClashDetectionService;
import me.nilushan.timetables_be.service.TimetableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {

    private final TimetableVersionRepository versionRepository;
    private final TimetableEntryRepository entryRepository;
    private final TermRepository termRepository;
    private final LessonRepository lessonRepository;
    private final LessonClassRepository lessonClassRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ClassGroupRepository classGroupRepository;
    private final ClashDetectionService clashDetectionService;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    @Override
    @Transactional
    public TimetableVersionResponse createVersion(TimetableVersionRequest request) {
        Long schoolId = getRequiredSchoolId();
        termRepository.findBySchoolIdAndId(schoolId, request.termId())
                .orElseThrow(() -> new ResourceNotFoundException("Term", request.termId()));

        TimetableVersion version = TimetableVersion.builder()
                .schoolId(schoolId)
                .termId(request.termId())
                .name(request.name())
                .status(VersionStatus.DRAFT)
                .build();

        TimetableVersion saved = versionRepository.save(version);
        return mapToVersionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableVersionResponse getVersionById(Long id) {
        Long schoolId = getRequiredSchoolId();
        TimetableVersion version = versionRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("TimetableVersion", id));
        return mapToVersionResponse(version);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimetableVersionResponse> getVersions(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return versionRepository.findBySchoolId(schoolId, pageable).map(this::mapToVersionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableVersionResponse> getVersionsByTerm(Long termId) {
        Long schoolId = getRequiredSchoolId();
        return versionRepository.findBySchoolIdAndTermId(schoolId, termId).stream()
                .map(this::mapToVersionResponse)
                .toList();
    }

    @Override
    @Transactional
    public TimetableEntryResponse createEntry(Long versionId, TimetableEntryRequest request) {
        Long schoolId = getRequiredSchoolId();
        TimetableVersion version = versionRepository.findBySchoolIdAndId(schoolId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("TimetableVersion", versionId));

        lessonRepository.findBySchoolIdAndId(schoolId, request.lessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", request.lessonId()));

        timeSlotRepository.findBySchoolIdAndId(schoolId, request.timeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", request.timeSlotId()));

        if (request.roomId() != null) {
            roomRepository.findBySchoolIdAndId(schoolId, request.roomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", request.roomId()));
        }

        // Check if exact (version, lesson, slot) already placed
        Optional<TimetableEntry> existing = entryRepository.findByVersionIdAndLessonIdAndTimeSlotId(
                version.getId(), request.lessonId(), request.timeSlotId());
        if (existing.isPresent()) {
            throw new ConflictException("This lesson is already placed in this time slot in this timetable version");
        }

        // Clash Detection Check (FR-5.1, FR-5.2, FR-5.3, FR-5.5, FR-5.7)
        clashDetectionService.validateNoClash(
                schoolId, version.getId(), request.lessonId(), request.timeSlotId(), request.roomId(), null
        );

        TimetableEntry entry = TimetableEntry.builder()
                .schoolId(schoolId)
                .versionId(version.getId())
                .lessonId(request.lessonId())
                .timeSlotId(request.timeSlotId())
                .roomId(request.roomId())
                .isPinned(Boolean.TRUE.equals(request.isPinned()))
                .build();

        TimetableEntry saved = entryRepository.save(entry);
        return mapToEntryResponse(saved);
    }

    @Override
    @Transactional
    public TimetableEntryResponse updateEntry(Long versionId, Long entryId, TimetableEntryRequest request) {
        Long schoolId = getRequiredSchoolId();
        versionRepository.findBySchoolIdAndId(schoolId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("TimetableVersion", versionId));

        TimetableEntry existing = entryRepository.findById(entryId)
                .filter(e -> e.getVersionId().equals(versionId) && e.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new ResourceNotFoundException("TimetableEntry", entryId));

        lessonRepository.findBySchoolIdAndId(schoolId, request.lessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", request.lessonId()));

        timeSlotRepository.findBySchoolIdAndId(schoolId, request.timeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", request.timeSlotId()));

        if (request.roomId() != null) {
            roomRepository.findBySchoolIdAndId(schoolId, request.roomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", request.roomId()));
        }

        // Clash Detection Check (excluding current entry)
        clashDetectionService.validateNoClash(
                schoolId, versionId, request.lessonId(), request.timeSlotId(), request.roomId(), entryId
        );

        existing.setLessonId(request.lessonId());
        existing.setTimeSlotId(request.timeSlotId());
        existing.setRoomId(request.roomId());
        if (request.isPinned() != null) {
            existing.setIsPinned(request.isPinned());
        }

        TimetableEntry saved = entryRepository.save(existing);
        return mapToEntryResponse(saved);
    }

    @Override
    @Transactional
    public void deleteEntry(Long versionId, Long entryId) {
        Long schoolId = getRequiredSchoolId();
        TimetableEntry existing = entryRepository.findById(entryId)
                .filter(e -> e.getVersionId().equals(versionId) && e.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new ResourceNotFoundException("TimetableEntry", entryId));
        entryRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableEntryResponse> getEntriesByClass(Long versionId, Long classGroupId) {
        Long schoolId = getRequiredSchoolId();
        versionRepository.findBySchoolIdAndId(schoolId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("TimetableVersion", versionId));
        return entryRepository.findByVersionIdAndClassGroupId(versionId, classGroupId).stream()
                .map(this::mapToEntryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableEntryResponse> getEntriesByTeacher(Long versionId, Long teacherId) {
        Long schoolId = getRequiredSchoolId();
        versionRepository.findBySchoolIdAndId(schoolId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("TimetableVersion", versionId));
        return entryRepository.findByVersionIdAndTeacherId(versionId, teacherId).stream()
                .map(this::mapToEntryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableEntryResponse> getEntriesByRoom(Long versionId, Long roomId) {
        Long schoolId = getRequiredSchoolId();
        versionRepository.findBySchoolIdAndId(schoolId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("TimetableVersion", versionId));
        return entryRepository.findByVersionIdAndRoomId(versionId, roomId).stream()
                .map(this::mapToEntryResponse)
                .toList();
    }

    private TimetableVersionResponse mapToVersionResponse(TimetableVersion v) {
        return new TimetableVersionResponse(
                v.getId(),
                v.getSchoolId(),
                v.getTermId(),
                v.getSolveJobId(),
                v.getName(),
                v.getStatus(),
                v.getHardScore(),
                v.getSoftScore(),
                v.getCreatedBy(),
                v.getCreatedAt(),
                v.getPublishedAt()
        );
    }

    private TimetableEntryResponse mapToEntryResponse(TimetableEntry e) {
        Lesson l = lessonRepository.findById(e.getLessonId()).orElse(null);
        TimeSlot s = timeSlotRepository.findById(e.getTimeSlotId()).orElse(null);
        Room r = e.getRoomId() != null ? roomRepository.findById(e.getRoomId()).orElse(null) : null;

        String subjectCode = null;
        String subjectName = null;
        String teacherName = null;
        String teacherShort = null;
        List<String> classNames = List.of();

        if (l != null) {
            Subject sub = subjectRepository.findById(l.getSubjectId()).orElse(null);
            if (sub != null) {
                subjectCode = sub.getCode();
                subjectName = sub.getNameEn();
            }
            Teacher t = teacherRepository.findById(l.getTeacherId()).orElse(null);
            if (t != null) {
                teacherName = t.getFullName();
                teacherShort = t.getShortCode();
            }
            classNames = lessonClassRepository.findByIdLessonId(l.getId()).stream()
                    .map(lc -> classGroupRepository.findById(lc.getId().getClassGroupId())
                            .map(ClassGroup::getName).orElse("Class " + lc.getId().getClassGroupId()))
                    .toList();
        }

        return new TimetableEntryResponse(
                e.getId(),
                e.getSchoolId(),
                e.getVersionId(),
                e.getLessonId(),
                e.getTimeSlotId(),
                e.getRoomId(),
                e.getIsPinned(),
                subjectCode,
                subjectName,
                teacherName,
                teacherShort,
                r != null ? r.getName() : null,
                s != null ? s.getDayOfWeek() : null,
                s != null ? s.getPeriodNo() : null,
                classNames
        );
    }
}
