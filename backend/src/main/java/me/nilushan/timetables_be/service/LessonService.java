package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.lesson.LessonRequest;
import me.nilushan.timetables_be.dto.lesson.LessonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LessonService {
    LessonResponse create(LessonRequest request);
    LessonResponse update(Long id, LessonRequest request);
    LessonResponse getById(Long id);
    Page<LessonResponse> getAll(Pageable pageable);
    List<LessonResponse> getByAcademicYearId(Long academicYearId);
    void delete(Long id);
}
