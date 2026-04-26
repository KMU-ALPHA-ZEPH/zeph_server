package zeph_server.record.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunStatsResponseDTO {

    private Double totalDistance;
    private Double monthlyDistance;
}
