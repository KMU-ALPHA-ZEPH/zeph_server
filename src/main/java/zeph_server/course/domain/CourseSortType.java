package zeph_server.course.domain;

import zeph_server.global.exception.CustomException;
import zeph_server.global.exception.GlobalErrorCode;

public enum CourseSortType {
    DISTANCE_ASC,   // 낮은 거리순
    DISTANCE_DESC,  // 높은 거리순
    NEAREST,        // 현재 위치에서 가까운순
    POPULAR;        // 좋아요 수 많은순

    public static CourseSortType from(String value) {
        if (value == null || value.isBlank()) {
            return POPULAR;
        }
        try {
            return CourseSortType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(GlobalErrorCode.INVALID_REQUEST);
        }
    }
}