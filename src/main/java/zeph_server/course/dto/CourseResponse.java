package zeph_server.course.dto;

import zeph_server.course.domain.Course;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        String type,
        Boolean roundTrip,
        String region,
        LocalDateTime createdAt
) {
    public static CourseResponse create(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getType(),
                course.getRoundTrip(),
                course.getRegion(),
                course.getCreatedAt()
        );
    }
}
