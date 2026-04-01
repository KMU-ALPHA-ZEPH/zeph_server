package zeph_server.course.dto;

import zeph_server.course.dto.common.PathData;

import java.time.LocalDateTime;

public record CreateCourseRequest(
        Long id,
        String type,
        Float distanceKm,
        PathData pathData,
        LocalDateTime createdAt

) {
}
