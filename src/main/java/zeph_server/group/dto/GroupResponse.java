package zeph_server.group.dto;

import zeph_server.group.domain.Group;

import java.time.LocalDateTime;

public record GroupResponse(
        Long id,
        String name,
        String description,
        String imageKey,
        String imageUrl,
        Long courseCount,
        LocalDateTime createdAt

) {
    public static GroupResponse from(Group group, Long courseCount, String imageUrl) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getImageKey(),
                imageUrl,
                courseCount,
                group.getCreatedAt()
        );
    }
}
