package zeph_server.course.dto;

import zeph_server.course.dto.common.PathData;

public record CreateCourseRequest(
        String name,
        String description,
        String type,
        Float distanceKm,
        PathData pathData,
        Boolean roundTrip,
        Boolean preferLighting,
        Boolean preferConvenience,
        String slopePreference
) {
}
