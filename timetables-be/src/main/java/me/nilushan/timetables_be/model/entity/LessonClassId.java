package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class LessonClassId implements Serializable {

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "class_group_id", nullable = false)
    private Long classGroupId;
}
