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
public class BasketSubjectId implements Serializable {

    @Column(name = "basket_id", nullable = false)
    private Long basketId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
}
