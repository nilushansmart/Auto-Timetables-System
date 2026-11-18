package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Term;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    Page<Term> findBySchoolId(Long schoolId, Pageable pageable);
    List<Term> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId);
    Optional<Term> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsByAcademicYearIdAndTermNo(Long academicYearId, Short termNo);
    boolean existsByAcademicYearIdAndTermNoAndIdNot(Long academicYearId, Short termNo, Long id);
}
