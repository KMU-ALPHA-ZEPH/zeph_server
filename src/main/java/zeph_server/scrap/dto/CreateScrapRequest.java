package zeph_server.scrap.dto;

import zeph_server.course.dto.CreateCourseRequest;

public record CreateScrapRequest(
        CreateCourseRequest courseData,
        Long courseId,
        Long groupId
) {
}
