package trinity.play2learn.backend.activity.memorama.dtos;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

public class ValidateMemoramaDto {
    public static <T> void validateDto(T dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMsg = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("\n"));
            throw new BadRequestException(errorMsg);
        }
    }
}
