package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    @Column(name = "grade_id", nullable = false)
    private Long gradeId;

    @Column(name = "medium_id", nullable = false)
    private Short mediumId;

    @Column(name = "stream_id")
    private Long streamId;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "class_teacher_id")
    private Long classTeacherId;

    @Column(name = "student_count")
    private Short studentCount;
}
