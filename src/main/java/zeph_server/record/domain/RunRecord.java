package zeph_server.record.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import zeph_server.course.domain.Course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "run_records")
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

    @Builder.Default
    @Column(nullable = false, length = 500)
    private String memo = "";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "runRecord",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RunRecordPoint> points =
            new ArrayList<>();

    @Transient
    public Double getAvgPace() {
        return durationSec != null && distanceKm != null && distanceKm > 0
                ? durationSec / distanceKm
                : null;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
