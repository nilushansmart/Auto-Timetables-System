package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.TeacherSubject;
import me.nilushan.timetables_be.model.entity.TeacherSubjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, TeacherSubjectId> {
    List<TeacherSubject> findByIdTeacherId(Long teacherId);
    void deleteByIdTeacherId(Long teacherId);
}
