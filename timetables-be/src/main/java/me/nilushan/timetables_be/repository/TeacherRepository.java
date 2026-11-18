package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Page<Teacher> findBySchoolId(Long schoolId, Pageable pageable);
    Optional<Teacher> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsBySchoolIdAndStaffNo(Long schoolId, String staffNo);
    boolean existsBySchoolIdAndShortCode(Long schoolId, String shortCode);
    boolean existsBySchoolIdAndStaffNoAndIdNot(Long schoolId, String staffNo, Long id);
    boolean existsBySchoolIdAndShortCodeAndIdNot(Long schoolId, String shortCode, Long id);
}
