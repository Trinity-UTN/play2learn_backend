package trinity.play2learn.backend.activity.ahorcado.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.activity.ahorcado.AhorcadoTestMother;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class AhorcadoDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("AhorcadoRequestDto validation")
    class AhorcadoRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with default word and TRES errors When validating Then no violations are returned")
        void request_valid() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.validAhorcadoRequestDto();

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given word is empty (edge case) When validating Then reports NOT_EMPTY_WORD")
        void request_wordEmpty() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.validAhorcadoRequestDto();
            request.setWord("");

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            // Empty string triggers both @NotEmpty and @Pattern violations
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("word") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_WORD)
            );
        }

        @Test
        @DisplayName("Given word is null (edge case) When validating Then reports NOT_EMPTY_WORD")
        void request_wordNull() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.validAhorcadoRequestDto();
            request.setWord(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_EMPTY_WORD, "word",
                "Word cannot be null");
        }

        @Test
        @DisplayName("Given word exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_WORD")
        void request_wordExceedsMaxLength() {
            // Given
            String longWord = "a".repeat(51);
            AhorcadoRequestDto request = AhorcadoTestMother.ahorcadoRequestDto(longWord, Errors.TRES);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.MAX_LENGTH_WORD, "word",
                "Word must not exceed 50 characters");
        }

        @Test
        @DisplayName("Given word at max length (50 chars - boundary) When validating Then no violations are returned")
        void request_wordAtMaxLength() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.ahorcadoRequestDto(
                AhorcadoTestMother.MAX_LENGTH_WORD,
                Errors.TRES
            );

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given word contains invalid characters (numbers - edge case) When validating Then reports PATTERN_WORD")
        void request_wordContainsNumbers() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.ahorcadoRequestDto(
                AhorcadoTestMother.WORD_WITH_NUMBERS,
                Errors.TRES
            );

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.PATTERN_WORD, "word",
                "Word must contain only letters, spaces and Spanish characters");
        }

        @Test
        @DisplayName("Given word contains invalid characters (special chars - edge case) When validating Then reports PATTERN_WORD")
        void request_wordContainsSpecialChars() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.ahorcadoRequestDto(
                AhorcadoTestMother.WORD_WITH_SPECIAL_CHARS,
                Errors.TRES
            );

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.PATTERN_WORD, "word",
                "Word must contain only letters, spaces and Spanish characters");
        }

        @Test
        @DisplayName("Given word contains valid Spanish characters (ñ, áéíóú) When validating Then no violations are returned")
        void request_wordWithSpanishCharacters() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.ahorcadoRequestDto(
                AhorcadoTestMother.VALID_SPANISH_WORD,
                Errors.TRES
            );

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given errorsPermited is null (required field) When validating Then reports NOT_NULL_ERRORS_PERMITED")
        void request_errorsPermitedNull() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.validAhorcadoRequestDto();
            request.setErrorsPermited(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_NULL_ERRORS_PERMITED, "errorsPermited",
                "Errors permitted is required and cannot be null");
        }

        @Test
        @DisplayName("Given errorsPermited is TRES When validating Then no violations are returned")
        void request_errorsPermitedTres() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.ahorcadoRequestDtoWithErrors(Errors.TRES);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given errorsPermited is CINCO When validating Then no violations are returned")
        void request_errorsPermitedCinco() {
            // Given
            AhorcadoRequestDto request = AhorcadoTestMother.ahorcadoRequestDtoWithErrors(Errors.CINCO);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    private <T> void assertViolation(Set<ConstraintViolation<T>> violations, String expectedMessage, String expectedPath) {
        assertViolation(violations, expectedMessage, expectedPath, null);
    }

    private <T> void assertViolation(Set<ConstraintViolation<T>> violations, String expectedMessage, String expectedPath, String reason) {
        assertThat(violations)
            .hasSize(1)
            .first()
            .satisfies(violation -> {
                assertThat(violation.getMessage()).isEqualTo(expectedMessage);
                assertThat(violation.getPropertyPath().toString()).isEqualTo(expectedPath);
            });
    }
}

