package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.TimeSlot;
import me.nilushan.timetables_be.model.enums.SlotType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    Page<TimeSlot> findBySchoolId(Long schoolId, Pageable pageable);
    List<TimeSlot> findBySchoolIdOrderByDayOfWeekAscPeriodNoAsc(Long schoolId);
    List<TimeSlot> findBySchoolIdAndDayOfWeekOrderByPeriodNoAsc(Long schoolId, Short dayOfWeek);
    List<TimeSlot> findBySchoolIdAndSlotType(Long schoolId, SlotType slotType);
    Optional<TimeSlot> findBySchoolIdAndId(Long schoolId, Long id);
    boolean existsBySchoolIdAndDayOfWeekAndPeriodNo(Long schoolId, Short dayOfWeek, Short periodNo);
    boolean existsBySchoolIdAndDayOfWeekAndPeriodNoAndIdNot(Long schoolId, Short dayOfWeek, Short periodNo, Long id);
}
