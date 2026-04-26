package zeph_server.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RunRecordDetailResponseDTO {

    private Long runId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double distanceKm;
    private Integer durationSec;
    private Double avgPace;

    private List<PointDto> path;

    @Getter
    @AllArgsConstructor
    public static class PointDto {
        private Double lat;
        private Double lng;
    }
}

