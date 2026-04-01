package zeph_server.course.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeph_server.course.dto.common.PathData;

import java.time.LocalDateTime;

@Entity
@Table(name = "course")
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
    private String type;

    @Column(name = "path_data", columnDefinition = "jsonb")
    private String pathData;

    @Column(name = "distance_km", nullable = false)
    private Float distanceKm;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
