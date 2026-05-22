package zeph_server.course.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import zeph_server.course.dto.CourseDetailResponse;
import zeph_server.course.dto.CourseResponse;
import zeph_server.course.dto.RecommendCourseRequest;
import zeph_server.course.service.CourseService;
import zeph_server.global.security.CustomUserDetails;

import java.util.List;

@Tag(name = "courses", description = "코스 API")
@RestController
@RequestMapping("v0/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    // 코스 추천받을 때 type도 들어올거임 그니까 type을 3개 만드는 게 아니라 걍 type 받아서 코스 만드는걸로
    @Operation(summary = "AI 기반 코스 추천", description = "AI를 이용해 코스를 생성한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 코스 생성 성공"),
            @ApiResponse(responseCode = "400", description = "추천에 필요한 필수 파라미터 누락 / 지원하지 않는 추천 타입"),
            @ApiResponse(responseCode = "500", description = "AI와의 통신 실패 or 알고리즘 내부 오류")
    })
    @PostMapping("/recommend")
    public ResponseEntity<?> RecommendCourse(
            @RequestBody RecommendCourseRequest requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        courseService.recommendCourse(requestDTO);
        return ResponseEntity.ok().build();
    }

    // 인기 경로에서 사용 (인기 경로 목록 조회)
    @Operation(summary = "코스 목록 조회", description = "모든 코스 목록 조회 (지역 / 타입)으로 구분")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공 - 데이터 없어도 빈 리스트 보냄"),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 쿼리 파라미터 전달"),
            @ApiResponse(responseCode = "500", description = "DB 조회 과정에서 인덱스 or 연결 오류")
    })
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(courseService.getAllCourses(region, type));
    }

    @Operation(summary = "세부 코스 조회", description = "코스 정보 표시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 코스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "데이터 복원(JSON) 중 서버 오류 발생")
    })
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }
}

