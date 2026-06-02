package zeph_server.scrap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zeph_server.global.security.CustomUserDetails;
import zeph_server.scrap.dto.CreateScrapRequest;
import zeph_server.scrap.dto.ScrapPreviewResponse;
import zeph_server.scrap.dto.UpdateScrapGroupRequest;
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

    @Operation(
            summary = "스크랩 폴더 변경",
            description = "스크랩을 다른 폴더로 이동하거나 groupId를 null로 보내 미분류 상태로 변경한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 변경 성공"),
            @ApiResponse(responseCode = "403", description = "본인 스크랩이 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "스크랩 또는 폴더를 찾을 수 없음")
    })
    @PatchMapping("/{scrapId}")
    public ResponseEntity<Void> updateScrapGroup(
            @PathVariable Long scrapId,
            @RequestBody UpdateScrapGroupRequest requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        scrapService.updateScrapGroup(scrapId, requestDTO.groupId(), userId);
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
