package zeph_server.course.dto.common;

public record Point(
        Long id,
        Double lat,
        Double lng,
        SegmentInfo segmentToNext
) {
}
