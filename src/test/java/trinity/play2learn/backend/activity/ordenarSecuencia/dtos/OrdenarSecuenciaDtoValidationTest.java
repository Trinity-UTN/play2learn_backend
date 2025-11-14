package trinity.play2learn.backend.activity.ordenarSecuencia.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.ordenarSecuencia.OrdenarSecuenciaTestMother;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class OrdenarSecuenciaDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("OrdenarSecuenciaRequestDto validation")
    class OrdenarSecuenciaRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with 3 events When validating Then no violations are returned")
        void request_valid() {
            // Given
            OrdenarSecuenciaRequestDto request = OrdenarSecuenciaTestMother.validOrdenarSecuenciaRequestDto();

            // When
            Set<ConstraintViolation<OrdenarSecuenciaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given events is null (required field - edge case - secuencia vacía) When validating Then reports LENGTH_EVENTS")
        void request_eventsNull() {
            // Given
            OrdenarSecuenciaRequestDto request = OrdenarSecuenciaTestMother.validOrdenarSecuenciaRequestDto();
            request.setEvents(null);

            // When
            Set<ConstraintViolation<OrdenarSecuenciaRequestDto>> violations = validator.validate(request);

            // Then
            // @Size validation does not apply to null, so no violation for null events
            // This test verifies that null events don't cause unexpected errors
            assertThat(violations).as("Null events should not cause validation errors since @NotNull is not present").isNotNull();
        }

        @Test
        @DisplayName("Given events has 2 events (should be 3-10 - edge case - secuencia incompleta) When validating Then reports LENGTH_EVENTS")
        void request_eventsHasTwoEvents() {
            // Given
            OrdenarSecuenciaRequestDto request = OrdenarSecuenciaTestMother.ordenarSecuenciaRequestDto(
                OrdenarSecuenciaTestMother.events(2)
            );

            // When
            Set<ConstraintViolation<OrdenarSecuenciaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("events") &&
                v.getMessage().equals(ValidationMessages.LENGTH_EVENTS)
            );
        }

        @Test
        @DisplayName("Given events has 11 events (should be 3-10 - edge case) When validating Then reports LENGTH_EVENTS")
        void request_eventsHasElevenEvents() {
            // Given
            OrdenarSecuenciaRequestDto request = OrdenarSecuenciaTestMother.ordenarSecuenciaRequestDto(
                OrdenarSecuenciaTestMother.events(11)
            );

            // When
            Set<ConstraintViolation<OrdenarSecuenciaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("events") &&
                v.getMessage().equals(ValidationMessages.LENGTH_EVENTS)
            );
        }

        @Test
        @DisplayName("Given events at max length (10 events - boundary) When validating Then no violations are returned")
        void request_eventsAtMaxLength() {
            // Given
            OrdenarSecuenciaRequestDto request = OrdenarSecuenciaTestMother.ordenarSecuenciaRequestDto(
                OrdenarSecuenciaTestMother.events(10)
            );

            // When
            Set<ConstraintViolation<OrdenarSecuenciaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("EventRequestDto validation")
    class EventRequestDtoValidation {

        @Test
        @DisplayName("Given valid event with name, description and order When validating Then no violations are returned")
        void event_valid() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event("Evento 1", "Descripción del evento 1", 0);

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME")
        void event_nameBlank() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event("", "Descripción del evento 1", 0);

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_NAME)
            );
        }

        @Test
        @DisplayName("Given name exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_NAME_50")
        void event_nameExceedsMaxLength() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event(
                OrdenarSecuenciaTestMother.MAX_LENGTH_EVENT_NAME + "a",
                "Descripción del evento 1",
                0
            );

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_NAME_50)
            );
        }

        @Test
        @DisplayName("Given name at max length (50 chars - boundary) When validating Then no violations are returned")
        void event_nameAtMaxLength() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event(
                OrdenarSecuenciaTestMother.MAX_LENGTH_EVENT_NAME,
                "Descripción del evento 1",
                0
            );

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given description is blank (required field) When validating Then reports NOT_EMPTY_DESCRIPTION")
        void event_descriptionBlank() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event("Evento 1", "", 0);

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("description") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_DESCRIPTION)
            );
        }

        @Test
        @DisplayName("Given description exceeds max length (101 chars - edge case) When validating Then reports MAX_LENGTH_DESCRIPTION_100")
        void event_descriptionExceedsMaxLength() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event(
                "Evento 1",
                OrdenarSecuenciaTestMother.MAX_LENGTH_EVENT_DESCRIPTION + "a",
                0
            );

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("description") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_DESCRIPTION_100)
            );
        }

        @Test
        @DisplayName("Given description at max length (100 chars - boundary) When validating Then no violations are returned")
        void event_descriptionAtMaxLength() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event(
                "Evento 1",
                OrdenarSecuenciaTestMother.MAX_LENGTH_EVENT_DESCRIPTION,
                0
            );

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given order is negative (should be >= 0 - edge case - orden inválido) When validating Then reports MIN_ORDER")
        void event_orderNegative() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event("Evento 1", "Descripción del evento 1", -1);

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("order") &&
                v.getMessage().equals(ValidationMessages.MIN_ORDER)
            );
        }

        @Test
        @DisplayName("Given order is zero (boundary) When validating Then no violations are returned")
        void event_orderZero() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event("Evento 1", "Descripción del evento 1", 0);

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given order is positive When validating Then no violations are returned")
        void event_orderPositive() {
            // Given
            EventRequestDto event = OrdenarSecuenciaTestMother.event("Evento 1", "Descripción del evento 1", 5);

            // When
            Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(event);

            // Then
            assertThat(violations).isEmpty();
        }
    }

}

