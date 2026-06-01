package zeph_server.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import zeph_server.global.exception.CustomException;
import zeph_server.global.exception.GlobalErrorCode;


 // 코스 목록 조회 필터/정렬 조건
 // 쿼리 파라미터가 많아 단일 조건 객체로 묶어 @ModelAttribute 로 바인딩

public record CourseSearchCondition(
        @Schema(description = "지역명 문자열 필터", example = "서울특별시 강남구")
        String region,

        @Schema(description = "코스 타입 필터", example = "walk")
        String type,

        @Schema(description = "정렬 기준", allowableValues = {"DISTANCE_ASC", "DISTANCE_DESC", "NEAREST", "POPULAR"})
        String sort,

        @Schema(description = "기준 위치 위도 (반경 필터/가까운순 공통)", example = "37.5")
        Double lat,

        @Schema(description = "기준 위치 경도 (반경 필터/가까운순 공통)", example = "127.0")
        Double lng,

        @Schema(description = "기준 위치로부터 반경(km) 이내 코스만 (lat·lng 필수)", example = "5")
        Double radiusKm,

        @Schema(description = "코스 경로 길이 하한(km)", example = "3")
        Double minDistanceKm,

        @Schema(description = "코스 경로 길이 상한(km)", example = "10")
        Double maxDistanceKm,

        @Schema(description = "true 시 로그인 유저가 좋아요한 코스만 조회", example = "false")
        Boolean liked
) {
    public boolean isLiked() {
        return Boolean.TRUE.equals(liked);
    }

    public boolean hasRegion() {
        return region != null && !region.isBlank();
    }

    public boolean hasType() {
        return type != null && !type.isBlank();
    }

    public boolean hasSort() {
        return sort != null && !sort.isBlank();
    }

    // 모순되거나 위치가 누락된 조건이면 400
    public void validate() {
        if (minDistanceKm != null && maxDistanceKm != null && minDistanceKm > maxDistanceKm) {
            throw new CustomException(GlobalErrorCode.INVALID_REQUEST);
        }
        if (radiusKm != null && (lat == null || lng == null)) {
            throw new CustomException(GlobalErrorCode.INVALID_REQUEST);
        }
    }
}
