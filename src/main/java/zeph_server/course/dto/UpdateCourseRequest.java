package zeph_server.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCourseRequest(
        @NotBlank(message = "코스 이름은 필수")
        @Size(max = 20, message = "코스 이름은 20자 이하")
        Long id,
        String name,
        String description
) {
}
