package zeph_server.record.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
    @Builder.Default
    private Integer pausedSec = 0;

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
       if (durationSec == null || distanceKm == null || distanceKm <= 0) {
           return null;
       }
       int movingSec = durationSec - (pausedSec == null ? 0 : pausedSec);
       return (double) movingSec / distanceKm;
    }

    public void updateMemo(String memo) {
        this.memo = (memo == null) ? "" : memo;
    }
}
