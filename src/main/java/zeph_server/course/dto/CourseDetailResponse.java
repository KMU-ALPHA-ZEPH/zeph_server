package zeph_server.course.dto;

import zeph_server.course.domain.Course;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.dto.common.Point;

public record CourseDetailResponse(
        Long id,
        String name,
        String description,
        String type,
        Float distanceKm,
        Double startLat,
        Double startLng,
        PathData pathData,
        Long likeCount,
        Boolean isLiked
) {
    public static CourseDetailResponse create(
            Course course,
            Long likeCount,
            Boolean isLiked
    ) {

        Double startLat = null;
        Double startLng = null;

        if (course.getPathData() != null &&
                course.getPathData().points() != null &&
                !course.getPathData().points().isEmpty()) {

            Point first = course.getPathData().points().get(0);

            startLat = first.lat();
            startLng = first.lng();
        }

        return new CourseDetailResponse(
                course.getId(),
                course.getName(),
                course.getType(),
                course.getDescription(),
                course.getDistanceKm(),
                startLat,
                startLng,
                course.getPathData(),
                likeCount,
                isLiked
        );
    }
}