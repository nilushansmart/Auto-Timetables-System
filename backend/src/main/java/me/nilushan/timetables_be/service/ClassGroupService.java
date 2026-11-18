package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.master.ClassGroupRequest;
import me.nilushan.timetables_be.dto.master.ClassGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassGroupService {
    ClassGroupResponse create(ClassGroupRequest request);
    ClassGroupResponse update(Long id, ClassGroupRequest request);
    ClassGroupResponse getById(Long id);
    Page<ClassGroupResponse> getAll(Pageable pageable);
    List<ClassGroupResponse> getByAcademicYearId(Long academicYearId);
    void delete(Long id);
}
