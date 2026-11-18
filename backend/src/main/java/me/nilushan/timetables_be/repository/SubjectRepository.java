package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Page<Subject> findBySchoolId(Long schoolId, Pageable pageable);
    List<Subject> findBySchoolIdOrderByCodeAsc(Long schoolId);
    Optional<Subject> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsBySchoolIdAndCode(Long schoolId, String code);
    boolean existsBySchoolIdAndCodeAndIdNot(Long schoolId, String code, Long id);
}
