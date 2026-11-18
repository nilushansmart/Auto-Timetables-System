package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;
import me.nilushan.timetables_be.model.enums.GroupType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "lesson_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type", nullable = false, columnDefinition = "group_type")
    private GroupType type;

    @Column(name = "name", length = 100, nullable = false)
    private String name;
}
