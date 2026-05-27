package zeph_server.course.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import zeph_server.course.dto.RecommendCourseRequest;
import zeph_server.course.dto.RouteNodeResponse;

import java.util.List;

@FeignClient(name = "AiCourseClient", url = "${AI_SERVER_URL:http://localhost:8000}")
public interface AiCourseClient {

    @PostMapping("/recommend")
    List<RouteNodeResponse> requestRecommendCourse(
            @RequestBody RecommendCourseRequest requestDTO
    );
}
