package zeph_server.scrap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zeph_server.global.security.CustomUserDetails;
import zeph_server.scrap.dto.CreateScrapRequest;
import zeph_server.scrap.dto.ScrapPreviewResponse;
import zeph_server.scrap.service.ScrapService;

import java.util.List;

@Tag(name = "scraps", description = "스크랩 API")
@RestController
@RequestMapping("v0/scraps")
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;

    @Operation(summary = "코스 생성(저장)", description = "추천받은 코스 중 하나를 선택해 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코스 생성(저장) 성공"),
            @ApiResponse(responseCode = "400", description = "필수 필드 누락"),
            @ApiResponse(responseCode = "500", description = "JSON 변환 중 오류 발생")
    })
    @PostMapping
    public ResponseEntity<?> createScrap(
            @RequestBody CreateScrapRequest requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        scrapService.createScrap(requestDTO, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "폴더별 스크랩 조회", description = "특정 폴더 안의 스크랩 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 - 스크랩 없으면 빈 리스트"),
            @ApiResponse(responseCode = "400", description = "groupId 또는 userId 형식 오류"),
            @ApiResponse(responseCode = "403", description = "본인의 폴더가 아님"),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{groupId}")
    public ResponseEntity<List<ScrapPreviewResponse>> getScrapsByGroupId(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(scrapService.getScrapsByGroup(userId, groupId));
    }

}
