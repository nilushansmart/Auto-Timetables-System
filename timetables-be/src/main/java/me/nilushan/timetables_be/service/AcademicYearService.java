package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.academic.AcademicYearRequest;
import me.nilushan.timetables_be.dto.academic.AcademicYearResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AcademicYearService {
    AcademicYearResponse create(AcademicYearRequest request);
    AcademicYearResponse update(Long id, AcademicYearRequest request);
    AcademicYearResponse getById(Long id);
    Page<AcademicYearResponse> getAll(Pageable pageable);
    void delete(Long id);
}
