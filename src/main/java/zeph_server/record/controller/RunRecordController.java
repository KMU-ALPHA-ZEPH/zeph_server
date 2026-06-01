package zeph_server.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zeph_server.global.security.CustomUserDetails;
import zeph_server.record.domain.Period;
import zeph_server.record.dto.request.RunRecordRequestDTO;
import zeph_server.record.dto.request.UpdateMemoRequestDTO;
import zeph_server.record.dto.response.RunRecordCreateResponseDTO;
import zeph_server.record.dto.response.RunRecordDetailResponseDTO;
import zeph_server.record.dto.response.RunRecordListResponseDTO;
import zeph_server.record.dto.response.RunStatsResponseDTO;
import zeph_server.record.service.RunRecordService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "records", description = "러닝 기록 API")
@RestController
@RequestMapping("v0/records")
@RequiredArgsConstructor
public class RunRecordController {

    private final RunRecordService runRecordService;

    @Operation(summary = "러닝 기록 등록", description = "러닝 기록을 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "필수 필드 누락 / 좌표 범위 초과 / 거리·시간 값 오류(0 이하, pausedSec>durationSec, endTime≤startTime)"),
            @ApiResponse(responseCode = "401", description = "토큰 누락 또는 만료"),
            @ApiResponse(responseCode = "404", description = "courseId에 해당하는 코스 없음")
    })
    @PostMapping
    public ResponseEntity<RunRecordCreateResponseDTO> createRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RunRecordRequestDTO requestDTO
    ) {
        RunRecordCreateResponseDTO response =
                runRecordService.saveRunRecord(userDetails.getUser().getId(), requestDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "러닝 기록 목록 조회", description = "사용자의 모든 러닝 기록을 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 (데이터 없으면 빈 리스트)"),
            @ApiResponse(responseCode = "401", description = "토큰 누락 또는 만료")
    })
    @GetMapping
    public ResponseEntity<List<RunRecordListResponseDTO>> getRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<RunRecordListResponseDTO> response =
                runRecordService.getRecords(userDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "러닝 기록 상세 조회", description = "단건 러닝 기록의 상세 정보와 코스 경로/실주행 경로를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 누락 또는 만료"),
            @ApiResponse(responseCode = "403", description = "본인 소유가 아닌 기록 접근"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 기록 없음")
    })
    @GetMapping("/{recordId}")
    public ResponseEntity<RunRecordDetailResponseDTO> getRecordDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "조회할 기록 ID", example = "42")
            @PathVariable Long recordId
    ) {
        RunRecordDetailResponseDTO response =
                runRecordService.getRecordDetail(userDetails.getUser().getId(), recordId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "러닝 기록 메모 수정", description = "단건 러닝 기록의 메모(최대 500자)를 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "메모 길이 초과"),
            @ApiResponse(responseCode = "401", description = "토큰 누락 또는 만료"),
            @ApiResponse(responseCode = "403", description = "본인 소유가 아닌 기록 접근"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 기록 없음")
    })
    @PatchMapping("/{recordId}")
    public ResponseEntity<Void> updateMemo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "메모를 수정할 기록 ID", example = "42")
            @PathVariable Long recordId,
            @Valid @RequestBody UpdateMemoRequestDTO requestDTO
    ) {
        runRecordService.updateMemo(userDetails.getUser().getId(), recordId, requestDTO.getMemo());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "러닝 기록 삭제", description = "러닝 기록을 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 누락 또는 만료"),
            @ApiResponse(responseCode = "403", description = "본인 소유가 아닌 기록 접근"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 기록 없음")
    })
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 기록 ID", example = "42")
            @PathVariable Long recordId
    ) {
        runRecordService.deleteRecord(userDetails.getUser().getId(), recordId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "러닝 통계 조회", description = "기간(WEEK/MONTH/YEAR/ALL)과 코스 타입(또는 ALL)으로 러닝 통계 집계")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 period / date 형식"),
            @ApiResponse(responseCode = "401", description = "토큰 누락 또는 만료")
    })
    @GetMapping("/stats")
    public ResponseEntity<RunStatsResponseDTO> getStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "코스 타입 필터 (예: TRAIL, ROAD). 미지정 시 ALL", example = "ALL")
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @Parameter(description = "집계 기간 (WEEK/MONTH/YEAR/ALL)", example = "MONTH")
            @RequestParam(required = false, defaultValue = "ALL") Period period,
            @Parameter(description = "기준 날짜 (yyyy-MM-dd). 생략 시 오늘", example = "2026-05-22")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        RunStatsResponseDTO response =
                runRecordService.getStats(userDetails.getUser().getId(), type, period, date);

        return ResponseEntity.ok(response);
    }
}
