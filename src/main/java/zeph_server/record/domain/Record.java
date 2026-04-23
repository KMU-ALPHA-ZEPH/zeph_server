package zeph_server.record.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "run_records")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Record {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Route route;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double distanceKm;
    private Integer durationSec;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
