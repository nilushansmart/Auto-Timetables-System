package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.GradeRequest;
import me.nilushan.timetables_be.dto.master.GradeResponse;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.Grade;
import me.nilushan.timetables_be.repository.GradeRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.GradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    @Override
    @Transactional
    public GradeResponse create(GradeRequest request) {
        Long schoolId = getRequiredSchoolId();
        if (gradeRepository.existsBySchoolIdAndGradeNo(schoolId, request.gradeNo())) {
            throw new ConflictException("Grade " + request.gradeNo() + " already exists for this school");
        }

        Grade grade = Grade.builder()
                .schoolId(schoolId)
                .gradeNo(request.gradeNo())
                .level(request.level())
                .build();

        Grade saved = gradeRepository.save(grade);
        return new GradeResponse(saved.getId(), saved.getSchoolId(), saved.getGradeNo(), saved.getLevel());
    }

    @Override
    @Transactional
    public GradeResponse update(Long id, GradeRequest request) {
        Long schoolId = getRequiredSchoolId();
        Grade existing = gradeRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));

        if (gradeRepository.existsBySchoolIdAndGradeNoAndIdNot(schoolId, request.gradeNo(), id)) {
            throw new ConflictException("Grade " + request.gradeNo() + " already exists for this school");
        }

        existing.setGradeNo(request.gradeNo());
        existing.setLevel(request.level());

        Grade saved = gradeRepository.save(existing);
        return new GradeResponse(saved.getId(), saved.getSchoolId(), saved.getGradeNo(), saved.getLevel());
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        Grade grade = gradeRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));
        return new GradeResponse(grade.getId(), grade.getSchoolId(), grade.getGradeNo(), grade.getLevel());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return gradeRepository.findBySchoolId(schoolId, pageable)
                .map(g -> new GradeResponse(g.getId(), g.getSchoolId(), g.getGradeNo(), g.getLevel()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getAllList() {
        Long schoolId = getRequiredSchoolId();
        return gradeRepository.findBySchoolIdOrderByGradeNoAsc(schoolId).stream()
                .map(g -> new GradeResponse(g.getId(), g.getSchoolId(), g.getGradeNo(), g.getLevel()))
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        Grade grade = gradeRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));
        gradeRepository.delete(grade);
    }
}
