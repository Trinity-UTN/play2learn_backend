package trinity.play2learn.backend.configs.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ResponseFactory {

    private static String getCurrentTimestamp() {
        return DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }

    // 🔄 200 OK
    public static <T> ResponseEntity<BaseResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(
                BaseResponse.<T>builder()
                        .data(data)
                        .message(message)
                        .errors(null)
                        .timestamp(getCurrentTimestamp())
                        .build()
        );
    }

    // 📄 200 OK paginada
    public static <T> ResponseEntity<BaseResponse<PaginatedData<T>>> paginated(PaginatedData<T> data, String message) {
        return ResponseEntity.ok(
                BaseResponse.<PaginatedData<T>>builder()
                        .data(data)
                        .message(message)
                        .errors(null)
                        .timestamp(getCurrentTimestamp())
                        .build()
        );
    }

    // ✅ 201 CREATED
    public static <T> ResponseEntity<BaseResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<T>builder()
                        .data(data)
                        .message(message)
                        .errors(null)
                        .timestamp(getCurrentTimestamp())
                        .build());
    }

    // 🙌 202 ACCEPTED
    public static <T> ResponseEntity<BaseResponse<T>> accepted(T data, String message) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(BaseResponse.<T>builder()
                        .data(data)
                        .message(message)
                        .errors(null)
                        .timestamp(getCurrentTimestamp())
                        .build());
    }

    // 😶 204 NO CONTENT (sin body pero con convención)
    public static ResponseEntity<BaseResponse<Void>> noContent(String message) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(BaseResponse.<Void>builder()
                        .data(null)
                        .message(message)
                        .errors(null)
                        .timestamp(getCurrentTimestamp())
                        .build());
    }
}