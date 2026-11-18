package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.master.GradeRequest;
import me.nilushan.timetables_be.dto.master.GradeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GradeService {
    GradeResponse create(GradeRequest request);
    GradeResponse update(Long id, GradeRequest request);
    GradeResponse getById(Long id);
    Page<GradeResponse> getAll(Pageable pageable);
    List<GradeResponse> getAllList();
    void delete(Long id);
}
