package zeph_server.global.dto;

public record AuthResponseDto(
        Long kakaoid,
        String name,
        String email,
        String token
) {}
