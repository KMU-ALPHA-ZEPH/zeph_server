package zeph_server.course.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeph_server.course.dto.common.PathData;

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
    private PathData pathData;

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
    @Column(name = "slope_preference")
    private String slopePreference;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void update(@NotBlank(message = "코스 이름은 필수") @Size(max = 20, message = "코스 이름은 20자 이하") String name) {
        this.name = name;
    }
}
