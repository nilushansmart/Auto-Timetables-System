package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Page<Grade> findBySchoolId(Long schoolId, Pageable pageable);
    List<Grade> findBySchoolIdOrderByGradeNoAsc(Long schoolId);
    Optional<Grade> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsBySchoolIdAndGradeNo(Long schoolId, Short gradeNo);
    boolean existsBySchoolIdAndGradeNoAndIdNot(Long schoolId, Short gradeNo, Long id);
}
