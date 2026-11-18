package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {

    List<TimetableEntry> findByVersionId(Long versionId);

    List<TimetableEntry> findByVersionIdAndTimeSlotId(Long versionId, Long timeSlotId);

    Optional<TimetableEntry> findByVersionIdAndLessonIdAndTimeSlotId(Long versionId, Long lessonId, Long timeSlotId);

    List<TimetableEntry> findByVersionIdAndRoomId(Long versionId, Long roomId);

    @Query("SELECT e FROM TimetableEntry e, Lesson l WHERE e.lessonId = l.id AND e.versionId = :versionId AND l.teacherId = :teacherId")
    List<TimetableEntry> findByVersionIdAndTeacherId(@Param("versionId") Long versionId, @Param("teacherId") Long teacherId);

    @Query("SELECT e FROM TimetableEntry e, LessonClass lc WHERE e.lessonId = lc.id.lessonId AND e.versionId = :versionId AND lc.id.classGroupId = :classGroupId")
    List<TimetableEntry> findByVersionIdAndClassGroupId(@Param("versionId") Long versionId, @Param("classGroupId") Long classGroupId);

    @Query("SELECT e FROM TimetableEntry e WHERE e.versionId = :versionId AND e.timeSlotId = :timeSlotId AND e.roomId = :roomId")
    List<TimetableEntry> findByVersionIdAndTimeSlotIdAndRoomId(@Param("versionId") Long versionId, @Param("timeSlotId") Long timeSlotId, @Param("roomId") Long roomId);

    @Query("SELECT e FROM TimetableEntry e, Lesson l WHERE e.lessonId = l.id AND e.versionId = :versionId AND e.timeSlotId = :timeSlotId AND l.teacherId = :teacherId")
    List<TimetableEntry> findByVersionIdAndTimeSlotIdAndTeacherId(@Param("versionId") Long versionId, @Param("timeSlotId") Long timeSlotId, @Param("teacherId") Long teacherId);

    @Query("SELECT e FROM TimetableEntry e, LessonClass lc WHERE e.lessonId = lc.id.lessonId AND e.versionId = :versionId AND e.timeSlotId = :timeSlotId AND lc.id.classGroupId IN :classGroupIds")
    List<TimetableEntry> findByVersionIdAndTimeSlotIdAndClassGroupIds(@Param("versionId") Long versionId, @Param("timeSlotId") Long timeSlotId, @Param("classGroupIds") List<Long> classGroupIds);
}
