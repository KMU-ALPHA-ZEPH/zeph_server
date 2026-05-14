package zeph_server.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RunRecordListResponseDTO {

    private Long runId;
    private LocalDateTime date;
    private String courseName;
    private Double distanceKm;
    private Integer durationSec;
    private Double avgPace;
    private List<PointDto> coursePath;
    private List<PointDto> actualPath;

    @Getter
    @AllArgsConstructor
    public static class PointDto {
        private Double lat;
        private Double lng;
    }
}
