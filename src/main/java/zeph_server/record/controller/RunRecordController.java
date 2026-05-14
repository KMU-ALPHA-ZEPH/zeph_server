package zeph_server.record.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @RequestBody RunRecordRequestDTO requestDTO
    ) {
        RunRecordCreateResponseDTO response = runRecordService.saveRunRecord(requestDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RunRecordListResponseDTO>> getRecords(
            @RequestParam Long userId
    ) {
        List<RunRecordListResponseDTO> response =
                runRecordService.getRecords(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<RunRecordDetailResponseDTO> getRecordDetail(
            @PathVariable Long recordId
    ) {
        RunRecordDetailResponseDTO response =
                runRecordService.getRecordDetail(recordId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{recordId}")
    public ResponseEntity<Void> updateMemo(
            @PathVariable Long recordId,
            @RequestBody UpdateMemoRequestDTO requestDTO
    ) {
        runRecordService.updateMemo(recordId, requestDTO.getMemo());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long recordId
    ) {
        runRecordService.deleteRecord(recordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<RunStatsResponseDTO> getStats(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false, defaultValue = "ALL") Period period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        RunStatsResponseDTO response =
                runRecordService.getStats(userId, type, period, date);

        return ResponseEntity.ok(response);
    }
}
