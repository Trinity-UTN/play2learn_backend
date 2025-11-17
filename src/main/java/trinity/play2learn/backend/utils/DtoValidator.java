package trinity.play2learn.backend.utils;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

public class DtoValidator {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errorMsg = violations.stream()
                .map(v -> formatViolation(v))
                .collect(Collectors.joining("\n"));
            throw new BadRequestException(errorMsg);
        }
    }

    private static <T> String formatViolation(ConstraintViolation<T> v) {
        return v.getPropertyPath() + ": " + v.getMessage();
    }
}