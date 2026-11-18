package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "substitution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Substitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "absence_id", nullable = false)
    private Long absenceId;

    @Column(name = "timetable_entry_id", nullable = false)
    private Long timetableEntryId;

    @Column(name = "substitute_teacher_id", nullable = false)
    private Long substituteTeacherId;
}
