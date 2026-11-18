package me.nilushan.timetables_be.model.entity;

import jakarta.persistence.*;
import lombok.*;
import me.nilushan.timetables_be.model.enums.JobStatus;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;

@Entity
@Table(name = "solve_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolveJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "term_id", nullable = false)
    private Long termId;

    @Column(name = "requested_by")
    private Long requestedBy;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, columnDefinition = "job_status")
    @Builder.Default
    private JobStatus status = JobStatus.QUEUED;

    @Column(name = "time_limit_sec", nullable = false)
    @Builder.Default
    private Integer timeLimitSec = 120;

    @Column(name = "best_hard_score")
    private Integer bestHardScore;

    @Column(name = "best_soft_score")
    private Integer bestSoftScore;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "queued_at", nullable = false)
    @Builder.Default
    private OffsetDateTime queuedAt = OffsetDateTime.now();

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;
}
