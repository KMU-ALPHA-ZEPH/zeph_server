package zeph_server.courseLike.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeph_server.course.domain.Course;
import zeph_server.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "course_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "course_id"})
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @CreatedDate
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;
}