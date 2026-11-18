package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.academic.TermRequest;
import me.nilushan.timetables_be.dto.academic.TermResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TermService {
    TermResponse create(TermRequest request);
    TermResponse update(Long id, TermRequest request);
    TermResponse getById(Long id);
    Page<TermResponse> getAll(Pageable pageable);
    List<TermResponse> getByAcademicYearId(Long academicYearId);
    void delete(Long id);
}
