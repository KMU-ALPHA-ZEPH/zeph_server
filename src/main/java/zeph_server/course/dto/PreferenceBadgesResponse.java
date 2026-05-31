package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PreferenceBadgesResponse(

        @JsonProperty("course_type")
        String courseType,

        String lighting,

        String convenience,

        String slope,

        String trip,

        String distance

) {

}