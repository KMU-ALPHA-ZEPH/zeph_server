package zeph_server.course.dto;

import zeph_server.course.domain.Course;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        Float startLat,
        Float startLng,
        String type,
        Boolean roundTrip,
        LocalDateTime createdAt
) {
    public static CourseResponse create(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getStartLat(),
                course.getStartLng(),
                course.getType(),
                course.getRoundTrip(),
                course.getCreatedAt()
        );
    }
}
