package zeph_server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDTO>
    handleCustomException(
            CustomException e
    ){

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
    ){

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO>
    handleException(
            Exception e
    ){

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
