package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendCourseRequest(
        Float distanceKm,
        Boolean roundTrip,
        Double startLat,
        Double startLng,
        String preference
) {

}
