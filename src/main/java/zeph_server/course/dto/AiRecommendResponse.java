package zeph_server.course.dto;

import java.util.List;

public record AiRecommendResponse(
        List<RouteNodeResponse> route
) {
}