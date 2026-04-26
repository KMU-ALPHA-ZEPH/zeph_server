package zeph_server.course.dto;

import zeph_server.course.domain.Course;
import zeph_server.course.dto.common.PathData;

import java.time.LocalDateTime;

public record CourseDetailResponse(
        Long id,
        String type,
        Float distanceKm,
        PathData pathData

) {
    public static CourseDetailResponse create(Course course, PathData pathData) {
        return new CourseDetailResponse(
                course.getId(),
                course.getType(),
                course.getDistanceKm(),
                pathData

        );
    }
}
