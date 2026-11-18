package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.academic.AcademicYearRequest;
import me.nilushan.timetables_be.dto.academic.AcademicYearResponse;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.AcademicYear;
import me.nilushan.timetables_be.repository.AcademicYearRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.AcademicYearService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcademicYearServiceImpl implements AcademicYearService {

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
    public AcademicYearResponse create(AcademicYearRequest request) {
        Long schoolId = getRequiredSchoolId();
        if (academicYearRepository.existsBySchoolIdAndYear(schoolId, request.year())) {
            throw new ConflictException("Academic year " + request.year() + " already exists for this school");
        }

        AcademicYear entity = AcademicYear.builder()
                .schoolId(schoolId)
                .year(request.year())
                .build();

        AcademicYear saved = academicYearRepository.save(entity);
        return new AcademicYearResponse(saved.getId(), saved.getSchoolId(), saved.getYear());
    }

    @Override
    @Transactional
    public AcademicYearResponse update(Long id, AcademicYearRequest request) {
        Long schoolId = getRequiredSchoolId();
        AcademicYear existing = academicYearRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));

        if (academicYearRepository.existsBySchoolIdAndYearAndIdNot(schoolId, request.year(), id)) {
            throw new ConflictException("Academic year " + request.year() + " already exists for this school");
        }

        existing.setYear(request.year());
        AcademicYear saved = academicYearRepository.save(existing);
        return new AcademicYearResponse(saved.getId(), saved.getSchoolId(), saved.getYear());
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicYearResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        AcademicYear entity = academicYearRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));
        return new AcademicYearResponse(entity.getId(), entity.getSchoolId(), entity.getYear());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademicYearResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return academicYearRepository.findBySchoolId(schoolId, pageable)
                .map(e -> new AcademicYearResponse(e.getId(), e.getSchoolId(), e.getYear()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        AcademicYear entity = academicYearRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));
        academicYearRepository.delete(entity);
    }
}
