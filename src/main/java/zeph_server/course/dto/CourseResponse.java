package zeph_server.course.dto;

import zeph_server.course.domain.Course;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        String name,
        String description,
        String type,
        Float distanceKm,
        Boolean roundTrip,
        String region,
        Long likeCount,
        Boolean isLiked,
        Long scrapId,
        LocalDateTime createdAt
) {
    public static CourseResponse create(Course course, Long likeCount, Boolean isLiked, Long scrapId) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getType(),
                course.getDistanceKm(),
                course.getRoundTrip(),
                course.getRegion(),
                likeCount,
                isLiked,
                scrapId,
                course.getCreatedAt()
        );
    }
}
