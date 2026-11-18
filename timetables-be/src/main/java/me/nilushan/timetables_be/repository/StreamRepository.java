package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    Page<Stream> findBySchoolId(Long schoolId, Pageable pageable);
    List<Stream> findBySchoolIdOrderByNameAsc(Long schoolId);
    Optional<Stream> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsBySchoolIdAndName(Long schoolId, String name);
    boolean existsBySchoolIdAndNameAndIdNot(Long schoolId, String name, Long id);
}
