package zeph_server.record.controller;

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

@RestController
@RequestMapping("v0/records")
@RequiredArgsConstructor
public class RunRecordController {

    private final RunRecordService runRecordService;

    @PostMapping
    public ResponseEntity<RunRecordCreateResponseDTO> createRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RunRecordRequestDTO requestDTO
    ) {
        RunRecordCreateResponseDTO response =
                runRecordService.saveRunRecord(userDetails.getUser().getId(), requestDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RunRecordListResponseDTO>> getRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<RunRecordListResponseDTO> response =
                runRecordService.getRecords(userDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<RunRecordDetailResponseDTO> getRecordDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId
    ) {
        RunRecordDetailResponseDTO response =
                runRecordService.getRecordDetail(userDetails.getUser().getId(), recordId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{recordId}")
    public ResponseEntity<Void> updateMemo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId,
            @RequestBody UpdateMemoRequestDTO requestDTO
    ) {
        runRecordService.updateMemo(userDetails.getUser().getId(), recordId, requestDTO.getMemo());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId
    ) {
        runRecordService.deleteRecord(userDetails.getUser().getId(), recordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<RunStatsResponseDTO> getStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false, defaultValue = "ALL") Period period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        RunStatsResponseDTO response =
                runRecordService.getStats(userDetails.getUser().getId(), type, period, date);

        return ResponseEntity.ok(response);
    }
}
