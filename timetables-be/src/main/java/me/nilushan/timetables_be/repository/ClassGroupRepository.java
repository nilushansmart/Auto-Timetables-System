package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.ClassGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {
    Page<ClassGroup> findBySchoolId(Long schoolId, Pageable pageable);
    List<ClassGroup> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId);
    Optional<ClassGroup> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsByAcademicYearIdAndName(Long academicYearId, String name);
    boolean existsByAcademicYearIdAndNameAndIdNot(Long academicYearId, String name, Long id);
}
