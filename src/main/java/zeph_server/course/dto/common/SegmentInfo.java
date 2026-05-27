package zeph_server.course.dto.common;

public record SegmentInfo(
        Double lengthM,
        Double avgBrightness,
        String slopeType,
        Boolean nearPark,
        Integer trafficlightCount,
        Double trafficVolumeScore
) {
}