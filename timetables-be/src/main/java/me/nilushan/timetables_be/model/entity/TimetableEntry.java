package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "timetable_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "version_id", nullable = false)
    private Long versionId;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "time_slot_id", nullable = false)
    private Long timeSlotId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private Boolean isPinned = false;
}
