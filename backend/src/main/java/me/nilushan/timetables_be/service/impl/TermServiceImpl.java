package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.academic.TermRequest;
import me.nilushan.timetables_be.dto.academic.TermResponse;
import me.nilushan.timetables_be.exception.BadRequestException;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.Term;
import me.nilushan.timetables_be.repository.AcademicYearRepository;
import me.nilushan.timetables_be.repository.TermRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.TermService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermRepository termRepository;
    private final AcademicYearRepository academicYearRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    private void validateDates(TermRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new BadRequestException("Term end date must be strictly after start date");
        }
    }

    @Override
    @Transactional
    public TermResponse create(TermRequest request) {
        Long schoolId = getRequiredSchoolId();
        validateDates(request);

        academicYearRepository.findBySchoolIdAndId(schoolId, request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", request.academicYearId()));

        if (termRepository.existsByAcademicYearIdAndTermNo(request.academicYearId(), request.termNo())) {
            throw new ConflictException("Term " + request.termNo() + " already exists for this academic year");
        }

        Term term = Term.builder()
                .schoolId(schoolId)
                .academicYearId(request.academicYearId())
                .termNo(request.termNo())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        Term saved = termRepository.save(term);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TermResponse update(Long id, TermRequest request) {
        Long schoolId = getRequiredSchoolId();
        validateDates(request);

        Term existing = termRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Term", id));

        academicYearRepository.findBySchoolIdAndId(schoolId, request.academicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", request.academicYearId()));

        if (termRepository.existsByAcademicYearIdAndTermNoAndIdNot(request.academicYearId(), request.termNo(), id)) {
            throw new ConflictException("Term " + request.termNo() + " already exists for this academic year");
        }

        existing.setAcademicYearId(request.academicYearId());
        existing.setTermNo(request.termNo());
        existing.setStartDate(request.startDate());
        existing.setEndDate(request.endDate());

        Term saved = termRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TermResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        Term term = termRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Term", id));
        return mapToResponse(term);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TermResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return termRepository.findBySchoolId(schoolId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TermResponse> getByAcademicYearId(Long academicYearId) {
        Long schoolId = getRequiredSchoolId();
        return termRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        Term term = termRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Term", id));
        termRepository.delete(term);
    }

    private TermResponse mapToResponse(Term term) {
        return new TermResponse(
                term.getId(),
                term.getSchoolId(),
                term.getAcademicYearId(),
                term.getTermNo(),
                term.getStartDate(),
                term.getEndDate()
        );
    }
}
