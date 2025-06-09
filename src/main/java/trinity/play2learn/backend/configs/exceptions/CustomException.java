package trinity.play2learn.backend.configs.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public abstract class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final List<String> errors;

    public CustomException(String message, HttpStatus status, List<String> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }
}
