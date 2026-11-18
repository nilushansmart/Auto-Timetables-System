package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;
import me.nilushan.timetables_be.model.enums.RoomType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type", nullable = false, columnDefinition = "room_type")
    @Builder.Default
    private RoomType type = RoomType.CLASSROOM;

    @Column(name = "capacity")
    private Short capacity;
}
