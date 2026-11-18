package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.TimetableVersion;
import me.nilushan.timetables_be.model.enums.VersionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableVersionRepository extends JpaRepository<TimetableVersion, Long> {
    Page<TimetableVersion> findBySchoolId(Long schoolId, Pageable pageable);
    List<TimetableVersion> findBySchoolIdAndTermId(Long schoolId, Long termId);
    Optional<TimetableVersion> findBySchoolIdAndId(Long schoolId, Long id);
    Optional<TimetableVersion> findBySchoolIdAndTermIdAndStatus(Long schoolId, Long termId, VersionStatus status);
}
