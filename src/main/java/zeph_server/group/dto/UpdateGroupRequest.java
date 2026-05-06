package zeph_server.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateGroupRequest(
        @NotBlank(message = "폴더 이름은 필수")
        @Size(max = 20, message = "폴더 이름은 20자 이하")
        String name,
        String description
) {
}
