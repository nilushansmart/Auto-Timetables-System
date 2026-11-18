package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "basket_subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketSubject {

    @EmbeddedId
    private BasketSubjectId id;
}
