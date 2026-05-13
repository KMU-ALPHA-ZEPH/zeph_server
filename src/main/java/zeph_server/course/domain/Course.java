package zeph_server.course.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeph_server.course.dto.common.PathData;

import java.sql.SQLType;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "path_data", columnDefinition = "jsonb")
    private String pathData;

    @Column(name = "distance_km", nullable = false)
    private Float distanceKm;

    @Column(name = "round_trip")
    private Boolean roundTrip;

    @Column
    private String region;

    @Column(name = "prefer_lighting")
    private Boolean preferLighting;

    @Column(name = "prefer_convenience")
    private Boolean preferConvenience;

    // 일단 500자 안쪽, 500자 이상 필요 시 (columnDefinition = "TEXT") 필요
    @Column(name = "slpoe_preference")
    private String slopePreference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
