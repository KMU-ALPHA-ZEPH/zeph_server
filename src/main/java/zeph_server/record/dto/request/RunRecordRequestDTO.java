package zeph_server.record.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RunRecordRequestDTO {

    private Long courseId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double distanceKm;
    private Integer durationSec;
    private Integer pausedSec;

    private List<PointDTO> points;

    @Getter
    public static class PointDTO {
        private Double lat;
        private Double lng;
        private LocalDateTime recordedAt;
    }

}
