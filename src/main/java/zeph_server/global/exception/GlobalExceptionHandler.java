package zeph_server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException e
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponseDTO.create(
                                GlobalErrorCode.FILE_SIZE_EXCEEDED,
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDTO>
    handleCustomException(
            CustomException e
    ) {

        Instant now = Instant.now();

        ErrorCode errorCode =
                e.getErrorCode();

        return ResponseEntity
                .status(
                        errorCode.getHttpStatus()
                )
                .body(
                        ErrorResponseDTO.create(
                                errorCode,
                                now
                        )
                );
    }

    // 404 - 리소스 없음
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
    }

    // 409 - 중복
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> handleDuplicate(DuplicateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", e.getMessage()));
    }

    // 403 - 권한 없음
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO>
    handleValidationException(
            MethodArgumentNotValidException e
    ) {

        Instant now = Instant.now();

        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_REQUEST;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(
                        ErrorResponseDTO.builder()
                                .code(errorCode.name())
                                .message(detail)
                                .status(errorCode.getHttpStatus().value())
                                .timestamp(now)
                                .build()
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        return ResponseEntity
                .status(statusCode)
                .body(Map.of("message", e.getReason() == null ? "" : e.getReason()));
    }

    // 400 - 잘못된 타입의 쿼리 파라미터/경로 변수
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO>
    handleTypeMismatch(
            MethodArgumentTypeMismatchException e
    ) {

        Instant now = Instant.now();

        String requiredType = e.getRequiredType() == null
                ? "올바른 형식"
                : e.getRequiredType().getSimpleName();
        String detail = e.getName() + ": '" + e.getValue()
                + "' 값을 " + requiredType + " 으로 변환할 수 없습니다";

        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_REQUEST;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(
                        ErrorResponseDTO.builder()
                                .code(errorCode.name())
                                .message(detail)
                                .status(errorCode.getHttpStatus().value())
                                .timestamp(now)
                                .build()
                );
    }

    // 400 - 읽을 수 없는 요청 바디 (깨진 JSON, 필드 타입 불일치 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO>
    handleNotReadable(
            HttpMessageNotReadableException e
    ) {

        Instant now = Instant.now();

        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_REQUEST;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(
                        ErrorResponseDTO.builder()
                                .code(errorCode.name())
                                .message("요청 본문을 읽을 수 없습니다")
                                .status(errorCode.getHttpStatus().value())
                                .timestamp(now)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO>
    handleException(
            Exception e
    ) {

        Instant now = Instant.now();

        return ResponseEntity
                .status(
                        GlobalErrorCode.DEFAULT
                                .getHttpStatus()
                )
                .body(
                        ErrorResponseDTO.create(
                                e,
                                now
                        )
                );
    }
}
