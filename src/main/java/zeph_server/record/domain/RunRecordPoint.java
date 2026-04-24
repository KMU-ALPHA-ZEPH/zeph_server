package zeph_server.record.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "run_record_points", indexes = @Index(name = "idx_run_record_id", columnList = "run_record_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RunRecordPoint {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_record_id", nullable = false)
    private Long runRecordId;

    @Column(nullable = false)
    private Integer seq;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Column(nullable = false)
    private LocalDateTime recordedAt;
}
