package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.lesson.LessonRequest;
import me.nilushan.timetables_be.dto.lesson.LessonResponse;
import me.nilushan.timetables_be.exception.BadRequestException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.*;
import me.nilushan.timetables_be.repository.*;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.LessonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonClassRepository lessonClassRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final LessonGroupRepository lessonGroupRepository;
    private final RoomRepository roomRepository;
    private final ClassGroupRepository classGroupRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    private void validateDependencies(Long schoolId, LessonRequest request) {
        academicYearRepository.findBySchoolIdAndId(schoolId, request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", request.academicYearId()));

        subjectRepository.findBySchoolIdAndId(schoolId, request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", request.subjectId()));

        teacherRepository.findBySchoolIdAndId(schoolId, request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.teacherId()));

        if (request.lessonGroupId() != null) {
            lessonGroupRepository.findBySchoolIdAndId(schoolId, request.lessonGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("LessonGroup", request.lessonGroupId()));
        }

        if (request.fixedRoomId() != null) {
            roomRepository.findBySchoolIdAndId(schoolId, request.fixedRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", request.fixedRoomId()));
        }

        if (request.classGroupIds() == null || request.classGroupIds().isEmpty()) {
            throw new BadRequestException("At least one class group must be assigned to the lesson");
        }

        for (Long classGroupId : request.classGroupIds()) {
            classGroupRepository.findBySchoolIdAndId(schoolId, classGroupId)
                    .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", classGroupId));
        }
    }

    @Override
    @Transactional
    public LessonResponse create(LessonRequest request) {
        Long schoolId = getRequiredSchoolId();
        validateDependencies(schoolId, request);

        Lesson lesson = Lesson.builder()
                .schoolId(schoolId)
                .academicYearId(request.academicYearId())
                .subjectId(request.subjectId())
                .teacherId(request.teacherId())
                .lessonGroupId(request.lessonGroupId())
                .requiredRoomType(request.requiredRoomType())
                .fixedRoomId(request.fixedRoomId())
                .periodsPerWeek(request.periodsPerWeek())
                .blockSize(request.blockSize())
                .maxPerDay(request.maxPerDay())
                .build();

        Lesson saved = lessonRepository.save(lesson);

        for (Long classGroupId : request.classGroupIds()) {
            LessonClass lc = LessonClass.builder()
                    .id(new LessonClassId(saved.getId(), classGroupId))
                    .build();
            lessonClassRepository.save(lc);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public LessonResponse update(Long id, LessonRequest request) {
        Long schoolId = getRequiredSchoolId();
        Lesson existing = lessonRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", id));

        validateDependencies(schoolId, request);

        existing.setAcademicYearId(request.academicYearId());
        existing.setSubjectId(request.subjectId());
        existing.setTeacherId(request.teacherId());
        existing.setLessonGroupId(request.lessonGroupId());
        existing.setRequiredRoomType(request.requiredRoomType());
        existing.setFixedRoomId(request.fixedRoomId());
        existing.setPeriodsPerWeek(request.periodsPerWeek());
        existing.setBlockSize(request.blockSize());
        existing.setMaxPerDay(request.maxPerDay());

        Lesson saved = lessonRepository.save(existing);

        lessonClassRepository.deleteByIdLessonId(saved.getId());
        for (Long classGroupId : request.classGroupIds()) {
            LessonClass lc = LessonClass.builder()
                    .id(new LessonClassId(saved.getId(), classGroupId))
                    .build();
            lessonClassRepository.save(lc);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        Lesson lesson = lessonRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", id));
        return mapToResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return lessonRepository.findBySchoolId(schoolId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getByAcademicYearId(Long academicYearId) {
        Long schoolId = getRequiredSchoolId();
        return lessonRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        Lesson lesson = lessonRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", id));
        lessonClassRepository.deleteByIdLessonId(id);
        lessonRepository.delete(lesson);
    }

    private LessonResponse mapToResponse(Lesson l) {
        Subject s = subjectRepository.findById(l.getSubjectId()).orElse(null);
        Teacher t = teacherRepository.findById(l.getTeacherId()).orElse(null);
        Room r = l.getFixedRoomId() != null ? roomRepository.findById(l.getFixedRoomId()).orElse(null) : null;
        LessonGroup g = l.getLessonGroupId() != null ? lessonGroupRepository.findById(l.getLessonGroupId()).orElse(null) : null;
        List<Long> classGroupIds = lessonClassRepository.findByIdLessonId(l.getId()).stream()
                .map(lc -> lc.getId().getClassGroupId())
                .toList();

        return new LessonResponse(
                l.getId(),
                l.getSchoolId(),
                l.getAcademicYearId(),
                l.getSubjectId(),
                l.getTeacherId(),
                l.getLessonGroupId(),
                l.getRequiredRoomType(),
                l.getFixedRoomId(),
                l.getPeriodsPerWeek(),
                l.getBlockSize(),
                l.getMaxPerDay(),
                classGroupIds,
                s != null ? s.getCode() : null,
                s != null ? s.getNameEn() : null,
                t != null ? t.getFullName() : null,
                t != null ? t.getShortCode() : null,
                r != null ? r.getName() : null,
                g != null ? g.getName() : null
        );
    }
}
