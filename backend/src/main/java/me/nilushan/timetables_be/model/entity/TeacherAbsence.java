package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "teacher_absence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherAbsence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "absent_date", nullable = false)
    private LocalDate absentDate;

    @Column(name = "reason", length = 200)
    private String reason;
}
