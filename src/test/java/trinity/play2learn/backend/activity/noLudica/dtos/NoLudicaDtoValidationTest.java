package trinity.play2learn.backend.activity.noLudica.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.noLudica.NoLudicaTestMother;
import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.models.TipoEntrega;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class NoLudicaDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("NoLudicaRequestDto validation")
    class NoLudicaRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with exercise and tipoEntrega When validating Then no violations are returned")
        void request_valid() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.validNoLudicaRequestDto();

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given exercise is null (required field - edge case - contenido vacío) When validating Then reports NOT_NULL_EXCERSICE")
        void request_exerciseNull() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.noLudicaRequestDtoWithExercise(null);

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("excercise") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_EXCERSICE)
            );
        }

        @Test
        @DisplayName("Given exercise exceeds max length (301 chars - edge case - contenido demasiado largo) When validating Then reports MAX_LENGTH_EXCERSICE")
        void request_exerciseExceedsMaxLength() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.noLudicaRequestDtoWithExercise(
                NoLudicaTestMother.MAX_LENGTH_EXERCISE + "a"
            );

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("excercise") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_EXCERSICE)
            );
        }

        @Test
        @DisplayName("Given exercise at max length (300 chars - boundary) When validating Then no violations are returned")
        void request_exerciseAtMaxLength() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.noLudicaRequestDtoWithExercise(
                NoLudicaTestMother.MAX_LENGTH_EXERCISE
            );

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given tipoEntrega is null (required field - edge case - tipo de entrega vacío) When validating Then reports NOT_NULL_TIPO_ENTREGA")
        void request_tipoEntregaNull() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.validNoLudicaRequestDto();
            request.setTipoEntrega(null);

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("tipoEntrega") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_TIPO_ENTREGA)
            );
        }

        @Test
        @DisplayName("Given tipoEntrega is ENTREGA When validating Then no violations are returned")
        void request_tipoEntregaEntrega() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.noLudicaRequestDtoWithTipoEntrega(
                TipoEntrega.ENTREGA
            );

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given tipoEntrega is ENLACE When validating Then no violations are returned")
        void request_tipoEntregaEnlace() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.noLudicaRequestDtoWithTipoEntrega(
                TipoEntrega.ENLACE
            );

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given tipoEntrega is TEXTO When validating Then no violations are returned")
        void request_tipoEntregaTexto() {
            // Given
            NoLudicaRequestDto request = NoLudicaTestMother.noLudicaRequestDtoWithTipoEntrega(
                TipoEntrega.TEXTO
            );

            // When
            Set<ConstraintViolation<NoLudicaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

}

