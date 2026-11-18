package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medium")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medium {

    @Id
    @Column(name = "id", nullable = false)
    private Short id;

    @Column(name = "code", length = 5, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 30, nullable = false)
    private String name;
}
