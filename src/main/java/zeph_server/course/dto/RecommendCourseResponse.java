package zeph_server.course.dto;

import zeph_server.course.dto.common.PathData;

public record RecommendCourseResponse(
        Float requestedDistanceKm,
        Double totalDistanceKm,
        String type,
        Boolean roundTrip,
        Double startLat,
        Double startLng,
        PathData pathData
) {
}