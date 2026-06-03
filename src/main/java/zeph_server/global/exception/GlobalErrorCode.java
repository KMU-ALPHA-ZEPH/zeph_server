package zeph_server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    COURSE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "코스를 찾을 수 없습니다"
    ),

    RECORD_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "러닝 기록을 찾을 수 없습니다"
    ),

    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다"
    ),

    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "접근 권한이 없습니다"
    ),

    FILE_SIZE_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "이미지 용량은 10MB 이하만 업로드할 수 있습니다."
    ),

    DEFAULT(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 오류가 발생했습니다"
    );

    private final HttpStatus httpStatus;
    private final String message;
}