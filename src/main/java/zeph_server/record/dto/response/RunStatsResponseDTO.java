package zeph_server.record.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunStatsResponseDTO {

   private Integer runCount;
   private Double avgPace;
   private Integer totalDurationSec;
}
