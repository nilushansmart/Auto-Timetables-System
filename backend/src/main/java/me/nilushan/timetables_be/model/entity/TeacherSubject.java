package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher_subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubject {

    @EmbeddedId
    private TeacherSubjectId id;
}
