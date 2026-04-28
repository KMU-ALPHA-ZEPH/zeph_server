package zeph_server.course.dto.common;

public record Point(
        Long id,
        Double lat,
        Double lng
        // 다음 점과의 사이에 대한 정보 (평균 밝기, 오르막길 내리막길 평지 여부, 공원 근처 여부 ( JSON 파일 )
) {
}
