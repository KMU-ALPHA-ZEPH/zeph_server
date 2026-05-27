package zeph_server.record.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @PositiveOrZero
    private Double distanceKm;

    @NotNull
    @PositiveOrZero
    private Integer durationSec;

    @NotNull
    @PositiveOrZero
    private Integer pausedSec;

    @NotEmpty
    @Valid
    private List<PointDTO> points;

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
