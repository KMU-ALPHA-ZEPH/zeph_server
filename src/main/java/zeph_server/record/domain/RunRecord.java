package zeph_server.record.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import zeph_server.course.domain.Course;

import java.time.LocalDateTime;

@Entity
@Table(name = "run_record")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RunRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Integer durationSec;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Transient
    public Double getAvgPace() {
        return durationSec != null && distanceKm != null && distanceKm > 0
                ? durationSec / distanceKm
                : null;
    }
}
