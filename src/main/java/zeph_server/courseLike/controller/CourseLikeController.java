package zeph_server.courseLike.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zeph_server.courseLike.service.CourseLikeService;
import zeph_server.global.security.CustomUserDetails;

@Tag(name = "courseLikes", description = "코스 좋아요 API")
@RestController
@RequestMapping("v0/courses/{courseId}/likes")
@RequiredArgsConstructor
public class CourseLikeController {

    private final CourseLikeService courseLikeService;

    @Operation(summary = "좋아요 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 좋아요한 코스"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<?> addLike(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        courseLikeService.addLike(courseId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "좋아요한 적 없는 코스"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping
    public ResponseEntity<?> removeLike(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        courseLikeService.removeLike(courseId, userId);
        return ResponseEntity.ok().build();
    }
}