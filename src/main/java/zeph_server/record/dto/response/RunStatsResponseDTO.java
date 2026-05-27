package zeph_server.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RunStatsResponseDTO {

   private Integer runCount;
   private Double avgPace;
   private Integer totalDurationSec;
   private Double totalDistanceKm;
   private List<BreakdownDto> breakdown;

   @Getter
   @AllArgsConstructor
   public static class BreakdownDto {
      private Integer index;
      private String label;
      private Double distanceKm;
   }
}
