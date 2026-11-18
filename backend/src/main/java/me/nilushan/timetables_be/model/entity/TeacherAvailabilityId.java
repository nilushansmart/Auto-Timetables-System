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
public class TeacherAvailabilityId implements Serializable {

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "time_slot_id", nullable = false)
    private Long timeSlotId;
}
