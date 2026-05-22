package zeph_server.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
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