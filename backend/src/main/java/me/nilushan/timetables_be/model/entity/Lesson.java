package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;
import me.nilushan.timetables_be.model.enums.RoomType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "lesson_group_id")
    private Long lessonGroupId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "required_room_type", columnDefinition = "room_type")
    private RoomType requiredRoomType;

    @Column(name = "fixed_room_id")
    private Long fixedRoomId;

    @Column(name = "periods_per_week", nullable = false)
    private Short periodsPerWeek;

    @Column(name = "block_size", nullable = false)
    @Builder.Default
    private Short blockSize = 1;

    @Column(name = "max_per_day", nullable = false)
    @Builder.Default
    private Short maxPerDay = 2;
}
