package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.master.SubjectRequest;
import me.nilushan.timetables_be.dto.master.SubjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubjectService {
    SubjectResponse create(SubjectRequest request);
    SubjectResponse update(Long id, SubjectRequest request);
    SubjectResponse getById(Long id);
    Page<SubjectResponse> getAll(Pageable pageable);
    List<SubjectResponse> getAllList();
    void delete(Long id);
}
