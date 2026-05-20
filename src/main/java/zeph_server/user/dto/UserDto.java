package zeph_server.user.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        Long kakaoId,
        String email,
        String name,
        String profile_image_url,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
}
