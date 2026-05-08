package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendCourseRequest(
        Float distanceKm,
        String type,
        Boolean roundTrip,
        Double startLat,
        Double startLng,
        // 조명 선호도
        Boolean preferLighting,
        // 편의시설 선호도
        Boolean preferConvenience,
        // 경사도 선호도
        String slopePreference,
        String prompt
) {

}
