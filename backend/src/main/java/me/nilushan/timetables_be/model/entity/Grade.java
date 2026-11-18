package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;
import me.nilushan.timetables_be.model.enums.GradeLevel;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "grade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "grade_no", nullable = false)
    private Short gradeNo;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "level", nullable = false, columnDefinition = "grade_level")
    private GradeLevel level;
}
