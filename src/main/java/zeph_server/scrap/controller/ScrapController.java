package zeph_server.scrap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeph_server.scrap.dto.CreateScrapRequest;
import zeph_server.scrap.dto.ScrapPreviewResponse;
import zeph_server.scrap.service.ScrapService;

import java.util.List;

@Tag(name = "groups", description = "그룹 API")
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
            @RequestBody CreateScrapRequest requestDTO
    ) {
        scrapService.createScrap(requestDTO); // 파라미터로 userId 받아오기
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<ScrapPreviewResponse>> getScrapByGroupId(
            @PathVariable Long groupId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(scrapService.getScrapByGroup(userId, groupId));
    }

}
