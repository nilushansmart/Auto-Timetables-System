package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.teacher.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeacherService {
    TeacherResponse create(TeacherRequest request);
    TeacherResponse update(Long id, TeacherRequest request);
    TeacherResponse getById(Long id);
    Page<TeacherResponse> getAll(Pageable pageable);
    void delete(Long id);

    TeacherSubjectResponse addSubject(Long teacherId, TeacherSubjectRequest request);
    void removeSubject(Long teacherId, Long subjectId, Short mediumId);

    TeacherAvailabilityResponse setAvailability(Long teacherId, TeacherAvailabilityRequest request);
    void removeAvailability(Long teacherId, Long timeSlotId);
}
