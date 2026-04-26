package zeph_server.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

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