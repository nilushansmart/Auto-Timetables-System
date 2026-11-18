package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.ClassGroupRequest;
import me.nilushan.timetables_be.dto.master.ClassGroupResponse;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.ClassGroup;
import me.nilushan.timetables_be.repository.*;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.ClassGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassGroupServiceImpl implements ClassGroupService {

    private final ClassGroupRepository classGroupRepository;
    private final AcademicYearRepository academicYearRepository;
    private final GradeRepository gradeRepository;
    private final MediumRepository mediumRepository;
    private final StreamRepository streamRepository;
    private final TeacherRepository teacherRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    private void validateDependencies(Long schoolId, ClassGroupRequest request) {
        academicYearRepository.findBySchoolIdAndId(schoolId, request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", request.academicYearId()));

        gradeRepository.findBySchoolIdAndId(schoolId, request.gradeId())
                .orElseThrow(() -> new ResourceNotFoundException("Grade", request.gradeId()));

        mediumRepository.findById(request.mediumId())
                .orElseThrow(() -> new ResourceNotFoundException("Medium", request.mediumId()));

        if (request.streamId() != null) {
            streamRepository.findBySchoolIdAndId(schoolId, request.streamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stream", request.streamId()));
        }

        if (request.classTeacherId() != null) {
            teacherRepository.findBySchoolIdAndId(schoolId, request.classTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.classTeacherId()));
        }
    }

    @Override
    @Transactional
    public ClassGroupResponse create(ClassGroupRequest request) {
        Long schoolId = getRequiredSchoolId();
        validateDependencies(schoolId, request);

        if (classGroupRepository.existsByAcademicYearIdAndName(request.academicYearId(), request.name())) {
            throw new ConflictException("Class with name '" + request.name() + "' already exists for this academic year");
        }

        ClassGroup entity = ClassGroup.builder()
                .schoolId(schoolId)
                .academicYearId(request.academicYearId())
                .gradeId(request.gradeId())
                .mediumId(request.mediumId())
                .streamId(request.streamId())
                .name(request.name())
                .classTeacherId(request.classTeacherId())
                .studentCount(request.studentCount())
                .build();

        ClassGroup saved = classGroupRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ClassGroupResponse update(Long id, ClassGroupRequest request) {
        Long schoolId = getRequiredSchoolId();
        ClassGroup existing = classGroupRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", id));

        validateDependencies(schoolId, request);

        if (classGroupRepository.existsByAcademicYearIdAndNameAndIdNot(request.academicYearId(), request.name(), id)) {
            throw new ConflictException("Class with name '" + request.name() + "' already exists for this academic year");
        }

        existing.setAcademicYearId(request.academicYearId());
        existing.setGradeId(request.gradeId());
        existing.setMediumId(request.mediumId());
        existing.setStreamId(request.streamId());
        existing.setName(request.name());
        existing.setClassTeacherId(request.classTeacherId());
        existing.setStudentCount(request.studentCount());

        ClassGroup saved = classGroupRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassGroupResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        ClassGroup entity = classGroupRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassGroupResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return classGroupRepository.findBySchoolId(schoolId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassGroupResponse> getByAcademicYearId(Long academicYearId) {
        Long schoolId = getRequiredSchoolId();
        return classGroupRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        ClassGroup entity = classGroupRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", id));
        classGroupRepository.delete(entity);
    }

    private ClassGroupResponse mapToResponse(ClassGroup e) {
        return new ClassGroupResponse(
                e.getId(),
                e.getSchoolId(),
                e.getAcademicYearId(),
                e.getGradeId(),
                e.getMediumId(),
                e.getStreamId(),
                e.getName(),
                e.getClassTeacherId(),
                e.getStudentCount()
        );
    }
}
