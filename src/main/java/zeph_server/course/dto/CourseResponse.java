package zeph_server.course.dto;

import zeph_server.course.domain.Course;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        String name,
        String type,
        Boolean roundTrip,
        String region,
        Long likeCount,
        Boolean isLiked,
        LocalDateTime createdAt
) {
    public static CourseResponse create(Course course, Long likeCount, Boolean isLiked) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getType(),
                course.getRoundTrip(),
                course.getRegion(),
                likeCount,
                isLiked,
                course.getCreatedAt()
        );
    }
}
