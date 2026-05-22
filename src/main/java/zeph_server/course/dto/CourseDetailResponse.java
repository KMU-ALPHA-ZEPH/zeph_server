package zeph_server.course.dto;

import zeph_server.course.domain.Course;
import zeph_server.course.dto.common.PathData;


public record CourseDetailResponse(
        Long id,
        String type,
        Float distanceKm,
        PathData pathData

) {
    public static CourseDetailResponse create(Course course) {
        return new CourseDetailResponse(
                course.getId(),
                course.getType(),
                course.getDistanceKm(),
                course.getPathData()

        );
    }
}
