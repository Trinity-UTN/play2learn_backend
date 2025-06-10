package trinity.play2learn.backend.configs.exceptions;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import trinity.play2learn.backend.configs.response.BaseResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex) {
        BaseResponse<Object> response = BaseResponse.<Object>builder()
                .data(null)
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .timestamp(getCurrentTimestamp())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    private String getCurrentTimestamp() {
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(Instant.now());
    }
}
