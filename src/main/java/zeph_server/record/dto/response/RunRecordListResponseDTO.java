package zeph_server.record.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RunRecordListResponseDTO {

    private Long runId;
    private LocalDateTime date;
    private Double distanceKm;
    private Integer durationSec;
    private Double avgPace;
}
