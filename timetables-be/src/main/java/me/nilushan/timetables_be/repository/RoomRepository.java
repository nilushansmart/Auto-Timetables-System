package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Room;
import me.nilushan.timetables_be.model.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findBySchoolId(Long schoolId, Pageable pageable);
    List<Room> findBySchoolIdOrderByTypeAscNameAsc(Long schoolId);
    List<Room> findBySchoolIdAndType(Long schoolId, RoomType type);
    Optional<Room> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsBySchoolIdAndName(Long schoolId, String name);
    boolean existsBySchoolIdAndNameAndIdNot(Long schoolId, String name, Long id);
}
