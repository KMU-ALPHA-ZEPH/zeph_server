package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RouteNodeResponse(
        Long id,
        Double lat,
        Double lng,
        @JsonProperty("segment_to_next")
        SegmentToNextResponse segmentToNext
) {
}