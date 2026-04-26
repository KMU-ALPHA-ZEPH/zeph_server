package zeph_server.record.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeph_server.record.dto.response.RunRecordDetailResponseDTO;
import zeph_server.record.dto.response.RunRecordListResponseDTO;
import zeph_server.record.dto.response.RunStatsResponseDTO;
import zeph_server.record.service.RunRecordService;

import java.util.List;

@RestController
@RequestMapping("v0/records")
@RequiredArgsConstructor
public class RunRecordController {

    private final RunRecordService runRecordService;

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

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long recordId
    ) {
        runRecordService.deleteRecord(recordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<RunStatsResponseDTO> getStats(
            @RequestParam Long userId
    ) {
        RunStatsResponseDTO response =
                runRecordService.getStats(userId);

        return ResponseEntity.ok(response);
    }
}
