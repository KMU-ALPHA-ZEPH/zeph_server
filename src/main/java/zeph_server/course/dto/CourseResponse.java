package zeph_server.course.dto;

import zeph_server.course.domain.Course;
import zeph_server.course.dto.common.PathData;
import zeph_server.util.PathSampler;

import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(
        Long id,
        String name,
        String description,
        String type,
        Float distanceKm,
        Boolean roundTrip,
        String region,
        Long likeCount,
        Boolean isLiked,
        Long scrapId,
        List<PointDto> coursePath,
        LocalDateTime createdAt
) {
    public static CourseResponse create(Course course, Long likeCount, Boolean isLiked, Long scrapId) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getType(),
                course.getDistanceKm(),
                course.getRoundTrip(),
                course.getRegion(),
                likeCount,
                isLiked,
                scrapId,
                toCoursePath(course.getPathData()),
                course.getCreatedAt()
        );
    }

    // 목록 카드 썸네일용 경로 (50포인트로 다운샘플)
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