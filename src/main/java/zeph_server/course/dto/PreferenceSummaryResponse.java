package zeph_server.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PreferenceSummaryResponse(

        String headline,

        List<String> bullets,

        PreferenceBadgesResponse badges

) {

}