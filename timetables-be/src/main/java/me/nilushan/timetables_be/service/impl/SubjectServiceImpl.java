package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.SubjectRequest;
import me.nilushan.timetables_be.dto.master.SubjectResponse;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.Subject;
import me.nilushan.timetables_be.repository.SubjectRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.SubjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    @Override
    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        Long schoolId = getRequiredSchoolId();
        if (subjectRepository.existsBySchoolIdAndCode(schoolId, request.code())) {
            throw new ConflictException("Subject with code '" + request.code() + "' already exists");
        }

        Subject subject = Subject.builder()
                .schoolId(schoolId)
                .code(request.code())
                .nameEn(request.nameEn())
                .nameSi(request.nameSi())
                .nameTa(request.nameTa())
                .isReligion(Boolean.TRUE.equals(request.isReligion()))
                .isPractical(Boolean.TRUE.equals(request.isPractical()))
                .build();

        Subject saved = subjectRepository.save(subject);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        Long schoolId = getRequiredSchoolId();
        Subject existing = subjectRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        if (subjectRepository.existsBySchoolIdAndCodeAndIdNot(schoolId, request.code(), id)) {
            throw new ConflictException("Subject with code '" + request.code() + "' already exists");
        }

        existing.setCode(request.code());
        existing.setNameEn(request.nameEn());
        existing.setNameSi(request.nameSi());
        existing.setNameTa(request.nameTa());
        existing.setIsReligion(Boolean.TRUE.equals(request.isReligion()));
        existing.setIsPractical(Boolean.TRUE.equals(request.isPractical()));

        Subject saved = subjectRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        Subject subject = subjectRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
        return mapToResponse(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return subjectRepository.findBySchoolId(schoolId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllList() {
        Long schoolId = getRequiredSchoolId();
        return subjectRepository.findBySchoolIdOrderByCodeAsc(schoolId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        Subject subject = subjectRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
        subjectRepository.delete(subject);
    }

    private SubjectResponse mapToResponse(Subject s) {
        return new SubjectResponse(
                s.getId(),
                s.getSchoolId(),
                s.getCode(),
                s.getNameEn(),
                s.getNameSi(),
                s.getNameTa(),
                s.getIsReligion(),
                s.getIsPractical()
        );
    }
}
