package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SegmentToNextResponse(
        @JsonProperty("length_m") Double lengthM,
        @JsonProperty("avg_brightness") Double avgBrightness,
        @JsonProperty("slope_type") String slopeType,
        @JsonProperty("near_park") Boolean nearPark,
        @JsonProperty("trafficlight_count") Integer trafficlightCount,
        @JsonProperty("traffic_volume_score") Double trafficVolumeScore
) {
}
