package zeph_server.course.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import zeph_server.course.dto.*;
import zeph_server.course.service.CourseService;
import zeph_server.global.security.CustomUserDetails;
import zeph_server.group.dto.UpdateGroupRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "courses", description = "코스 API")
@RestController
@RequestMapping("v0/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @Operation(summary = "AI 기반 코스 추천", description = "AI를 이용해 코스를 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 코스 생성 성공"),
            @ApiResponse(responseCode = "400", description = "추천에 필요한 필수 파라미터 누락 / 지원하지 않는 추천 타입"),
            @ApiResponse(responseCode = "500", description = "AI와의 통신 실패 or 알고리즘 내부 오류")
    })
    @PostMapping("")
    public ResponseEntity<RecommendCourseResponse> recommendCourse(
            @RequestBody RecommendCourseRequest requestDTO
//            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        RecommendCourseResponse response =
                courseService.recommendCourse(requestDTO);

        return ResponseEntity.ok(response);
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
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(courseService.getAllCourses(region, type, userId));
    }

    @Operation(summary = "세부 코스 조회", description = "코스 정보 표시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 코스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "데이터 복원(JSON) 중 서버 오류 발생")
    })
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseById(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(courseService.getCourseById(courseId, userId));
    }

    @Operation(summary = "코스 이름 수정")
    @PatchMapping("/{courseId}")
    public ResponseEntity<?> updateCourseById(
            @PathVariable Long courseId,
            @RequestBody @Valid UpdateCourseRequest requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        courseService.updateCourse(courseId, requestDTO, userId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "코스 GPX 다운로드", description = "코스 경로를 GPX 파일로 내려준다. 클라이언트가 다운로드/공유에 사용한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "GPX 생성 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 코스를 찾을 수 없음 / 경로 데이터 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{courseId}/gpx")
    public ResponseEntity<String> downloadCourseGpx(@PathVariable Long courseId) {
        String gpx = courseService.exportCourseGpx(courseId);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "gpx+xml", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"course_" + courseId + ".gpx\"")
                .body(gpx);
    }
}

