package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.TeacherAvailability;
import me.nilushan.timetables_be.model.entity.TeacherAvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailability, TeacherAvailabilityId> {
    List<TeacherAvailability> findByIdTeacherId(Long teacherId);
    Optional<TeacherAvailability> findByIdTeacherIdAndIdTimeSlotId(Long teacherId, Long timeSlotId);
    void deleteByIdTeacherId(Long teacherId);
}
