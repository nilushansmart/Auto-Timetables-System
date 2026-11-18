package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "lesson_class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonClass {

    @EmbeddedId
    private LessonClassId id;

    @Column(name = "student_count")
    private Short studentCount;
}
