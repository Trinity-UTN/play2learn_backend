package trinity.play2learn.backend.activity.activity.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class ActivityCreatedDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("ActivityRequestDto validation (via AhorcadoRequestDto)")
    class ActivityRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with all required fields When validating Then no violations are returned")
        void request_valid() {
            // Given
            AhorcadoRequestDto request = validRequest();

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given valid request with description at max length (1000 chars) When validating Then no violations are returned")
        void request_descriptionAtMaxLength() {
            // Given
            String maxLengthDescription = "a".repeat(1000);
            AhorcadoRequestDto request = validRequest();
            request.setDescription(maxLengthDescription);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given description exceeds max length (1001 chars) When validating Then reports MAX_LENGTH_DESCRIPTION_1000")
        void request_descriptionExceedsMaxLength() {
            // Given
            String longDescription = "a".repeat(1001);
            AhorcadoRequestDto request = validRequest();
            request.setDescription(longDescription);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.MAX_LENGTH_DESCRIPTION_1000, "description",
                "Description must not exceed 1000 characters");
        }

        @Test
        @DisplayName("Given description is null (optional field) When validating Then no violations are returned")
        void request_descriptionNull() {
            // Given
            AhorcadoRequestDto request = validRequest();
            request.setDescription(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given startDate is null (required field) When validating Then reports NOT_NULL_START_DATE")
        void request_startDateNull() {
            // Given
            AhorcadoRequestDto request = validRequest();
            request.setStartDate(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_NULL_START_DATE, "startDate",
                "Start date is required and cannot be null");
        }

        @Test
        @DisplayName("Given endDate is null (required field) When validating Then reports NOT_NULL_END_DATE")
        void request_endDateNull() {
            // Given
            AhorcadoRequestDto request = validRequest();
            request.setEndDate(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_NULL_END_DATE, "endDate",
                "End date is required and cannot be null");
        }

        @Test
        @DisplayName("Given difficulty is null (required field) When validating Then reports NOT_NULL_DIFFICULTY")
        void request_difficultyNull() {
            // Given
            AhorcadoRequestDto request = validRequest();
            request.setDifficulty(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_NULL_DIFFICULTY, "difficulty",
                "Difficulty is required and cannot be null");
        }

        @Test
        @DisplayName("Given subjectId is null (required field) When validating Then reports NOT_NULL_SUBJECT")
        void request_subjectIdNull() {
            // Given
            AhorcadoRequestDto request = validRequest();
            request.setSubjectId(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_NULL_SUBJECT, "subjectId",
                "Subject ID is required and cannot be null");
        }

        @Test
        @DisplayName("Given all optional fields are null When validating Then no violations are returned")
        void request_optionalFieldsNull() {
            // Given
            AhorcadoRequestDto request = validRequest();
            request.setDescription(null);
            request.setInitialBalance(null);
            request.setTypeReward(null);

            // When
            Set<ConstraintViolation<AhorcadoRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    private AhorcadoRequestDto validRequest() {
        LocalDateTime now = LocalDateTime.now();
        AhorcadoRequestDto dto = new AhorcadoRequestDto(
            "palabra", // word
            Errors.TRES // errorsPermited
        );
        dto.setDescription("Descripción de prueba");
        dto.setStartDate(now);
        dto.setEndDate(now.plusDays(7));
        dto.setDifficulty(Difficulty.FACIL);
        dto.setMaxTime(30);
        dto.setSubjectId(1L);
        dto.setAttempts(3);
        dto.setInitialBalance(100.0);
        return dto;
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

