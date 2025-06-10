package trinity.play2learn.backend.configs.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, List.of(message));
    }

    public NotFoundException(String message, List<String> errors) {
        super(message, HttpStatus.NOT_FOUND, errors);
    }
}