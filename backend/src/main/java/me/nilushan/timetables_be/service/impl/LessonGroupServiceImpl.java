package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.lesson.LessonGroupRequest;
import me.nilushan.timetables_be.dto.lesson.LessonGroupResponse;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.LessonGroup;
import me.nilushan.timetables_be.repository.AcademicYearRepository;
import me.nilushan.timetables_be.repository.LessonGroupRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.LessonGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonGroupServiceImpl implements LessonGroupService {

    private final LessonGroupRepository lessonGroupRepository;
    private final AcademicYearRepository academicYearRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    @Override
    @Transactional
    public LessonGroupResponse create(LessonGroupRequest request) {
        Long schoolId = getRequiredSchoolId();
        academicYearRepository.findBySchoolIdAndId(schoolId, request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", request.academicYearId()));

        LessonGroup group = LessonGroup.builder()
                .schoolId(schoolId)
                .academicYearId(request.academicYearId())
                .type(request.type())
                .name(request.name())
                .build();

        LessonGroup saved = lessonGroupRepository.save(group);
        return new LessonGroupResponse(saved.getId(), saved.getSchoolId(), saved.getAcademicYearId(), saved.getType(), saved.getName());
    }

    @Override
    @Transactional
    public LessonGroupResponse update(Long id, LessonGroupRequest request) {
        Long schoolId = getRequiredSchoolId();
        LessonGroup existing = lessonGroupRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("LessonGroup", id));

        academicYearRepository.findBySchoolIdAndId(schoolId, request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", request.academicYearId()));

        existing.setAcademicYearId(request.academicYearId());
        existing.setType(request.type());
        existing.setName(request.name());

        LessonGroup saved = lessonGroupRepository.save(existing);
        return new LessonGroupResponse(saved.getId(), saved.getSchoolId(), saved.getAcademicYearId(), saved.getType(), saved.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public LessonGroupResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        LessonGroup group = lessonGroupRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("LessonGroup", id));
        return new LessonGroupResponse(group.getId(), group.getSchoolId(), group.getAcademicYearId(), group.getType(), group.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonGroupResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return lessonGroupRepository.findBySchoolId(schoolId, pageable)
                .map(g -> new LessonGroupResponse(g.getId(), g.getSchoolId(), g.getAcademicYearId(), g.getType(), g.getName()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonGroupResponse> getByAcademicYearId(Long academicYearId) {
        Long schoolId = getRequiredSchoolId();
        return lessonGroupRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId).stream()
                .map(g -> new LessonGroupResponse(g.getId(), g.getSchoolId(), g.getAcademicYearId(), g.getType(), g.getName()))
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        LessonGroup group = lessonGroupRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("LessonGroup", id));
        lessonGroupRepository.delete(group);
    }
}
