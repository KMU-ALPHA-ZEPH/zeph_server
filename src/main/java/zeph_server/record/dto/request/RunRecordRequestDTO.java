package zeph_server.record.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RunRecordRequestDTO {

    @NotNull
    private Long courseId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    @Positive
    private Double distanceKm;

    @NotNull
    @Positive
    private Integer durationSec;

    @NotNull
    @PositiveOrZero
    private Integer pausedSec;

    @NotEmpty
    @Valid
    private List<PointDTO> points;

    @AssertTrue(message = "일시정지 시간은 총 경과시간을 넘을 수 없습니다")
    public boolean isPausedSecValid() {
        if (durationSec == null || pausedSec == null) {
            return true;
        }
        return pausedSec <= durationSec;
    }

    @AssertTrue(message = "종료 시각은 시작 시각보다 뒤여야 합니다")
    public boolean isTimeRangeValid() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }

    @Getter
    public static class PointDTO {

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        private Double lat;

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        private Double lng;

        @NotNull
        private LocalDateTime recordedAt;
    }

}
