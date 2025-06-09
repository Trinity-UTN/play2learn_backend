package trinity.play2learn.backend.configs.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, List.of(message));
    }

    public ForbiddenException(String message, List<String> errors) {
        super(message, HttpStatus.FORBIDDEN, errors);
    }
}