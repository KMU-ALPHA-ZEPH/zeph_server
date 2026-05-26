package zeph_server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponseDTO {

    private String code;
    private String message;
    private Integer status;
    private Instant timestamp;


    public static ErrorResponseDTO create(
            ErrorCode errorCode,
            Instant timestamp
    ){
        return ErrorResponseDTO.builder()
                .code(
                        errorCode.toString()
                )
                .message(
                        errorCode.getMessage()
                )
                .status(
                        errorCode.getHttpStatus().value()
                )
                .timestamp(timestamp)
                .build();
    }


    public static ErrorResponseDTO create(
            Exception e,
            Instant timestamp
    ){

        GlobalErrorCode errorCode =
                GlobalErrorCode.DEFAULT;

        return ErrorResponseDTO.builder()
                .code(errorCode.name())
                .message(
                        e.getClass().getSimpleName()
                                + ": "
                                + e.getMessage()
                )
                .status(
                        errorCode
                                .getHttpStatus()
                                .value()
                )
                .timestamp(timestamp)
                .build();
    }

}