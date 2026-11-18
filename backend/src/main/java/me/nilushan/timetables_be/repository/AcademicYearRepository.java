package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.AcademicYear;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Page<AcademicYear> findBySchoolId(Long schoolId, Pageable pageable);
    Optional<AcademicYear> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsBySchoolIdAndYear(Long schoolId, Short year);
    boolean existsBySchoolIdAndYearAndIdNot(Long schoolId, Short year, Long id);
}
