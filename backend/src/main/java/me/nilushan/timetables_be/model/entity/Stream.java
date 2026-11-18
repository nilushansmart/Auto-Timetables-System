package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stream")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;
}
