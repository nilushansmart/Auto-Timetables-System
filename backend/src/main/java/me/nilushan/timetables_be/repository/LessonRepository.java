package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Page<Lesson> findBySchoolId(Long schoolId, Pageable pageable);
    List<Lesson> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId);
    List<Lesson> findBySchoolIdAndTeacherId(Long schoolId, Long teacherId);
    List<Lesson> findBySchoolIdAndLessonGroupId(Long schoolId, Long lessonGroupId);
    Optional<Lesson> findBySchoolIdAndId(Long schoolId, Long id);
}
