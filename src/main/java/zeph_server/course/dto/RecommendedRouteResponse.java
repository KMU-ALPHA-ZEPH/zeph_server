package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecommendedRouteResponse(

        Integer rank,

        Double score,

        @JsonProperty("total_length_m")
        Double totalLengthM,

        List<RouteNodeResponse> points
) {
}