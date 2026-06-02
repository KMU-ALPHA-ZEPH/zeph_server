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
    private Long courseId;
    private String courseName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double distanceKm;
    private Integer durationSec;
    private Double avgPace;
    private String memo;

    Boolean scrapped;
    Long scrapId;
    Boolean liked;

    private List<PointDto> coursePath;
    private List<PointDto> actualPath;

    @Getter
    @AllArgsConstructor
    public static class PointDto {
        private Double lat;
        private Double lng;
    }
}

