package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "staff_no", length = 30)
    private String staffNo;

    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    @Column(name = "short_code", length = 10)
    private String shortCode;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "max_periods_week")
    private Short maxPeriodsWeek;

    @Column(name = "max_periods_day")
    private Short maxPeriodsDay;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
