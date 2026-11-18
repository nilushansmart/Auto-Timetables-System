package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.LessonClass;
import me.nilushan.timetables_be.model.entity.LessonClassId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonClassRepository extends JpaRepository<LessonClass, LessonClassId> {
    List<LessonClass> findByIdLessonId(Long lessonId);
    List<LessonClass> findByIdClassGroupId(Long classGroupId);
    void deleteByIdLessonId(Long lessonId);
}
