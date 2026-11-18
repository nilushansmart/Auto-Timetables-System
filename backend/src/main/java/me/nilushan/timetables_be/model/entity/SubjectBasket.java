package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject_basket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectBasket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "grade_id", nullable = false)
    private Long gradeId;

    @Column(name = "basket_no", nullable = false)
    private Short basketNo;
}
