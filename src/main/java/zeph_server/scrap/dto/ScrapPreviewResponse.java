package zeph_server.scrap.dto;

import zeph_server.course.domain.Course;
import zeph_server.group.domain.Group;
import zeph_server.scrap.domain.Scrap;

import java.time.LocalDateTime;

public record ScrapPreviewResponse(
        Long scrapId,
        Long courseId,
        String name,
        String type,
        Float distanceKm,
        String region,
        Long groupId,
        String groupName,
        LocalDateTime savedAt
) {
    public static ScrapPreviewResponse from(Scrap scrap) {
        Course course = scrap.getCourse();
        Group group = scrap.getGroup();
        return new ScrapPreviewResponse(
                scrap.getId(),
                course.getId(),
                course.getName(),
                course.getType(),
                course.getDistanceKm(),
                course.getRegion(),
                group != null ? group.getId() : null,
                group != null ? group.getName() : null,
                scrap.getSavedAt()
        );
    }
}
