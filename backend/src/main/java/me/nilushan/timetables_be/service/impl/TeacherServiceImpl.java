package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.teacher.*;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.*;
import me.nilushan.timetables_be.repository.*;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final SubjectRepository subjectRepository;
    private final MediumRepository mediumRepository;
    private final TimeSlotRepository timeSlotRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    private void validateUniqueCodes(Long schoolId, String staffNo, String shortCode, Long excludeId) {
        if (StringUtils.hasText(staffNo)) {
            boolean exists = (excludeId == null)
                    ? teacherRepository.existsBySchoolIdAndStaffNo(schoolId, staffNo)
                    : teacherRepository.existsBySchoolIdAndStaffNoAndIdNot(schoolId, staffNo, excludeId);
            if (exists) {
                throw new ConflictException("Teacher with staff number '" + staffNo + "' already exists");
            }
        }

        if (StringUtils.hasText(shortCode)) {
            boolean exists = (excludeId == null)
                    ? teacherRepository.existsBySchoolIdAndShortCode(schoolId, shortCode)
                    : teacherRepository.existsBySchoolIdAndShortCodeAndIdNot(schoolId, shortCode, excludeId);
            if (exists) {
                throw new ConflictException("Teacher with short code '" + shortCode + "' already exists");
            }
        }
    }

    @Override
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        Long schoolId = getRequiredSchoolId();
        validateUniqueCodes(schoolId, request.staffNo(), request.shortCode(), null);

        Teacher teacher = Teacher.builder()
                .schoolId(schoolId)
                .staffNo(request.staffNo())
                .fullName(request.fullName())
                .shortCode(request.shortCode())
                .email(request.email())
                .phone(request.phone())
                .maxPeriodsWeek(request.maxPeriodsWeek())
                .maxPeriodsDay(request.maxPeriodsDay())
                .active(request.active() == null || request.active())
                .build();

        Teacher saved = teacherRepository.save(teacher);
        return mapToResponse(saved, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Long schoolId = getRequiredSchoolId();
        Teacher existing = teacherRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));

        validateUniqueCodes(schoolId, request.staffNo(), request.shortCode(), id);

        existing.setStaffNo(request.staffNo());
        existing.setFullName(request.fullName());
        existing.setShortCode(request.shortCode());
        existing.setEmail(request.email());
        existing.setPhone(request.phone());
        existing.setMaxPeriodsWeek(request.maxPeriodsWeek());
        existing.setMaxPeriodsDay(request.maxPeriodsDay());
        if (request.active() != null) {
            existing.setActive(request.active());
        }

        Teacher saved = teacherRepository.save(existing);
        return getById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        Teacher teacher = teacherRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));

        List<TeacherSubjectResponse> subjects = loadTeacherSubjects(teacher.getId());
        List<TeacherAvailabilityResponse> availability = loadTeacherAvailability(teacher.getId());

        return mapToResponse(teacher, subjects, availability);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return teacherRepository.findBySchoolId(schoolId, pageable)
                .map(t -> mapToResponse(t, loadTeacherSubjects(t.getId()), loadTeacherAvailability(t.getId())));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        Teacher teacher = teacherRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
        teacherSubjectRepository.deleteByIdTeacherId(id);
        teacherAvailabilityRepository.deleteByIdTeacherId(id);
        teacherRepository.delete(teacher);
    }

    @Override
    @Transactional
    public TeacherSubjectResponse addSubject(Long teacherId, TeacherSubjectRequest request) {
        Long schoolId = getRequiredSchoolId();
        Teacher teacher = teacherRepository.findBySchoolIdAndId(schoolId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", teacherId));

        Subject subject = subjectRepository.findBySchoolIdAndId(schoolId, request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", request.subjectId()));

        Medium medium = mediumRepository.findById(request.mediumId())
                .orElseThrow(() -> new ResourceNotFoundException("Medium", request.mediumId()));

        TeacherSubjectId id = new TeacherSubjectId(teacher.getId(), subject.getId(), medium.getId());
        TeacherSubject ts = TeacherSubject.builder().id(id).build();
        teacherSubjectRepository.save(ts);

        return new TeacherSubjectResponse(
                teacher.getId(),
                subject.getId(),
                medium.getId(),
                subject.getCode(),
                subject.getNameEn(),
                medium.getCode(),
                medium.getName()
        );
    }

    @Override
    @Transactional
    public void removeSubject(Long teacherId, Long subjectId, Short mediumId) {
        Long schoolId = getRequiredSchoolId();
        teacherRepository.findBySchoolIdAndId(schoolId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", teacherId));

        TeacherSubjectId id = new TeacherSubjectId(teacherId, subjectId, mediumId);
        teacherSubjectRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TeacherAvailabilityResponse setAvailability(Long teacherId, TeacherAvailabilityRequest request) {
        Long schoolId = getRequiredSchoolId();
        Teacher teacher = teacherRepository.findBySchoolIdAndId(schoolId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", teacherId));

        TimeSlot timeSlot = timeSlotRepository.findBySchoolIdAndId(schoolId, request.timeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", request.timeSlotId()));

        TeacherAvailabilityId id = new TeacherAvailabilityId(teacher.getId(), timeSlot.getId());
        TeacherAvailability entity = TeacherAvailability.builder()
                .id(id)
                .kind(request.kind())
                .build();

        TeacherAvailability saved = teacherAvailabilityRepository.save(entity);
        return new TeacherAvailabilityResponse(
                teacher.getId(),
                timeSlot.getId(),
                saved.getKind(),
                timeSlot.getDayOfWeek(),
                timeSlot.getPeriodNo()
        );
    }

    @Override
    @Transactional
    public void removeAvailability(Long teacherId, Long timeSlotId) {
        Long schoolId = getRequiredSchoolId();
        teacherRepository.findBySchoolIdAndId(schoolId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", teacherId));

        TeacherAvailabilityId id = new TeacherAvailabilityId(teacherId, timeSlotId);
        teacherAvailabilityRepository.deleteById(id);
    }

    private List<TeacherSubjectResponse> loadTeacherSubjects(Long teacherId) {
        return teacherSubjectRepository.findByIdTeacherId(teacherId).stream()
                .map(ts -> {
                    Subject s = subjectRepository.findById(ts.getId().getSubjectId()).orElse(null);
                    Medium m = mediumRepository.findById(ts.getId().getMediumId()).orElse(null);
                    return new TeacherSubjectResponse(
                            teacherId,
                            ts.getId().getSubjectId(),
                            ts.getId().getMediumId(),
                            s != null ? s.getCode() : null,
                            s != null ? s.getNameEn() : null,
                            m != null ? m.getCode() : null,
                            m != null ? m.getName() : null
                    );
                })
                .toList();
    }

    private List<TeacherAvailabilityResponse> loadTeacherAvailability(Long teacherId) {
        return teacherAvailabilityRepository.findByIdTeacherId(teacherId).stream()
                .map(ta -> {
                    TimeSlot ts = timeSlotRepository.findById(ta.getId().getTimeSlotId()).orElse(null);
                    return new TeacherAvailabilityResponse(
                            teacherId,
                            ta.getId().getTimeSlotId(),
                            ta.getKind(),
                            ts != null ? ts.getDayOfWeek() : null,
                            ts != null ? ts.getPeriodNo() : null
                    );
                })
                .toList();
    }

    private TeacherResponse mapToResponse(Teacher t, List<TeacherSubjectResponse> subjects, List<TeacherAvailabilityResponse> availability) {
        return new TeacherResponse(
                t.getId(),
                t.getSchoolId(),
                t.getStaffNo(),
                t.getFullName(),
                t.getShortCode(),
                t.getEmail(),
                t.getPhone(),
                t.getMaxPeriodsWeek(),
                t.getMaxPeriodsDay(),
                t.getActive(),
                subjects,
                availability
        );
    }
}
