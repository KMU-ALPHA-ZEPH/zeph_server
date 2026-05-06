package zeph_server.group.dto;

import jakarta.validation.constraints.NotBlank;

public record AddGroupRequest(
        @NotBlank(message = "폴더 이름은 필수입니다")
        String name
) {
}
