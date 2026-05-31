package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiRecommendResponse(
        @JsonProperty("course_type")
        String courseType,

        @JsonProperty("preference_summary")
        PreferenceSummaryResponse preferenceSummary,

        List<RouteNodeResponse> routes

) {

}