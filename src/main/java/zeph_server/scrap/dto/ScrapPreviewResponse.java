package zeph_server.scrap.dto;

import zeph_server.course.domain.Course;
import zeph_server.course.dto.common.PathData;
import zeph_server.group.domain.Group;
import zeph_server.scrap.domain.Scrap;
import zeph_server.util.PathSampler;

import java.time.LocalDateTime;
import java.util.List;

public record ScrapPreviewResponse(
        Long scrapId,
        Long courseId,
        String name,
        String description,
        String type,
        Float distanceKm,
        String region,
        Long groupId,
        String groupName,
        LocalDateTime savedAt,
        List<PointDto> coursePath
) {
    public static ScrapPreviewResponse from(Scrap scrap) {
        Course course = scrap.getCourse();
        Group group = scrap.getGroup();
        return new ScrapPreviewResponse(
                scrap.getId(),
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getType(),
                course.getDistanceKm(),
                course.getRegion(),
                group != null ? group.getId() : null,
                group != null ? group.getName() : null,
                scrap.getSavedAt(),
                toCoursePath(course.getPathData())
        );
    }

    // 목록 카드 미리보기용 경로 (50포인트로 다운샘플)
    private static List<PointDto> toCoursePath(PathData pathData) {
        if (pathData == null || pathData.points() == null || pathData.points().isEmpty()) {
            return List.of();
        }
        return PathSampler.downsample(pathData.points(), 50).stream()
                .map(p -> new PointDto(p.lat(), p.lng()))
                .toList();
    }

    public record PointDto(Double lat, Double lng) {
    }
}