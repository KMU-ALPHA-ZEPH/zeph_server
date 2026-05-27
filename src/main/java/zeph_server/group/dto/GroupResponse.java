package zeph_server.group.dto;

import zeph_server.group.domain.Group;

import java.time.LocalDateTime;

public record GroupResponse(
        Long id,
        String name,
        Long courseCount,
        LocalDateTime createdAt

) {
    public static GroupResponse from(Group group, Long courseCount) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                courseCount,
                group.getCreatedAt()
        );
    }
}
