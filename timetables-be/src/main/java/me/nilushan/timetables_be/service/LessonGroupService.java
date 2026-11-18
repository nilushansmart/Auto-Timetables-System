package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.lesson.LessonGroupRequest;
import me.nilushan.timetables_be.dto.lesson.LessonGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LessonGroupService {
    LessonGroupResponse create(LessonGroupRequest request);
    LessonGroupResponse update(Long id, LessonGroupRequest request);
    LessonGroupResponse getById(Long id);
    Page<LessonGroupResponse> getAll(Pageable pageable);
    List<LessonGroupResponse> getByAcademicYearId(Long academicYearId);
    void delete(Long id);
}
