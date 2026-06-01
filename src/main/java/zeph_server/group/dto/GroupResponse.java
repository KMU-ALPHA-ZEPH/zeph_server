package zeph_server.group.dto;

import zeph_server.group.domain.Group;

import java.time.LocalDateTime;

public record GroupResponse(
        Long id,
        String name,
        String description,
        Long courseCount,
        LocalDateTime createdAt

) {
    public static GroupResponse from(Group group, Long courseCount) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                courseCount,
                group.getCreatedAt()
        );
    }
}
