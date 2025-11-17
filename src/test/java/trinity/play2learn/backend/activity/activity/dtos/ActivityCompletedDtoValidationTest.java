package trinity.play2learn.backend.activity.activity.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class ActivityCompletedDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("ActivityCompletedRequestDto validation")
    class ActivityCompletedRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with approved state When validating Then no violations are returned")
        void request_validApproved() {
            // Given
            ActivityCompletedRequestDto request = validRequest(ActivityCompletedState.APPROVED);

            // When
            Set<ConstraintViolation<ActivityCompletedRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given valid request with disapproved state When validating Then no violations are returned")
        void request_validDisapproved() {
            // Given
            ActivityCompletedRequestDto request = validRequest(ActivityCompletedState.DISAPPROVED);

            // When
            Set<ConstraintViolation<ActivityCompletedRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given valid request with pending state When validating Then no violations are returned")
        void request_validPending() {
            // Given
            ActivityCompletedRequestDto request = validRequest(ActivityCompletedState.PENDING);

            // When
            Set<ConstraintViolation<ActivityCompletedRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given activityId is null (required field) When validating Then reports NOT_NULL_ACTIVITY_ID")
        void request_activityIdNull() {
            // Given
            ActivityCompletedRequestDto request = invalidRequestWithNullActivityId();

            // When
            Set<ConstraintViolation<ActivityCompletedRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_NULL_ACTIVITY_ID, "activityId",
                "Activity ID is required and cannot be null");
        }

        @Test
        @DisplayName("Given state is null (required field) When validating Then reports NOT_NULL_STATE")
        void request_stateNull() {
            // Given
            ActivityCompletedRequestDto request = invalidRequestWithNullState();

            // When
            Set<ConstraintViolation<ActivityCompletedRequestDto>> violations = validator.validate(request);

            // Then
            assertViolation(violations, ValidationMessages.NOT_NULL_STATE, "state",
                "State is required and cannot be null");
        }

        @Test
        @DisplayName("Given both required fields are null When validating Then reports violations for both fields")
        void request_bothRequiredFieldsNull() {
            // Given
            ActivityCompletedRequestDto request = ActivityCompletedRequestDto.builder()
                .activityId(null)
                .state(null)
                .build();

            // When
            Set<ConstraintViolation<ActivityCompletedRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("activityId");
                    assertThat(violation.getMessage()).isEqualTo(ValidationMessages.NOT_NULL_ACTIVITY_ID);
                })
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("state");
                    assertThat(violation.getMessage()).isEqualTo(ValidationMessages.NOT_NULL_STATE);
                });
        }
    }

    private ActivityCompletedRequestDto validRequest(ActivityCompletedState state) {
        return ActivityCompletedRequestDto.builder()
            .activityId(1L)
            .state(state)
            .build();
    }

    private ActivityCompletedRequestDto invalidRequestWithNullActivityId() {
        return ActivityCompletedRequestDto.builder()
            .activityId(null)
            .state(ActivityCompletedState.APPROVED)
            .build();
    }

    private ActivityCompletedRequestDto invalidRequestWithNullState() {
        return ActivityCompletedRequestDto.builder()
            .activityId(1L)
            .state(null)
            .build();
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

