package zeph_server.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CourseSaveResponse(
        @Schema(description = "저장된 코스 ID (좋아요/스크랩에 사용)")
        Long id
) {
}