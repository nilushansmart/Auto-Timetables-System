package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.LessonGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonGroupRepository extends JpaRepository<LessonGroup, Long> {
    Page<LessonGroup> findBySchoolId(Long schoolId, Pageable pageable);
    List<LessonGroup> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId);
    Optional<LessonGroup> findBySchoolIdAndId(Long schoolId, Long id);
}
