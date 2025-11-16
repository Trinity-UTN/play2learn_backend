package trinity.play2learn.backend.benefits.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class BenefitDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("BenefitRequestDto validation")
    class BenefitRequestDtoValidation {

        @Test
        @DisplayName("When request is valid Then no violations are returned")
        void whenValidRequest_thenNoViolations() {
            // Given - DTO válido con todos los campos requeridos
            BenefitRequestDto request = validRequest().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - No debe haber violaciones
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When name is null Then reports NOT_EMPTY_NAME")
        void whenNameIsNull_thenReportsNotEmptyName() {
            // Given - DTO con nombre null viola @NotBlank
            // Motivo: el nombre es requerido para identificar el beneficio en la UI
            BenefitRequestDto request = invalidRequestWithNullName().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_EMPTY_NAME
            assertViolation(violations, ValidationMessages.NOT_EMPTY_NAME, "name");
        }

        @Test
        @DisplayName("When name is blank Then reports NOT_EMPTY_NAME")
        void whenNameIsBlank_thenReportsNotEmptyName() {
            // Given - DTO con nombre vacío ("") viola @NotBlank (no permite null ni string vacío)
            // Motivo: el nombre no puede estar vacío para mostrar en la lista de beneficios
            BenefitRequestDto request = invalidRequestWithBlankName().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_EMPTY_NAME
            assertViolation(violations, ValidationMessages.NOT_EMPTY_NAME, "name");
        }

        @Test
        @DisplayName("When name equals max length (100) Then no violations")
        void whenNameEqualsMaxLength_thenNoViolations() {
            // Given - DTO con nombre de exactamente 100 caracteres (límite máximo permitido)
            BenefitRequestDto request = validRequest()
                .name(nameOfLength(100))
                .build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - No debe haber violaciones (100 es el límite permitido)
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When name exceeds max length Then reports MAX_LENGTH_NAME_100")
        void whenNameExceedsMaxLength_thenReportsMaxLengthName() {
            // Given - DTO con nombre de más de 100 caracteres viola @Size(max=100)
            // Motivo: el nombre debe ser conciso para mostrar en UI sin truncado
            BenefitRequestDto request = invalidRequestWithNameTooLong().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de MAX_LENGTH_NAME_100
            assertViolation(violations, ValidationMessages.MAX_LENGTH_NAME_100, "name");
        }

        @Test
        @DisplayName("When description is null Then reports NOT_EMPTY_DESCRIPTION")
        void whenDescriptionIsNull_thenReportsNotEmptyDescription() {
            // Given - DTO con descripción null viola @NotBlank
            // Motivo: la descripción es requerida para explicar el beneficio a los estudiantes
            BenefitRequestDto request = invalidRequestWithNullDescription().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_EMPTY_DESCRIPTION
            assertViolation(violations, ValidationMessages.NOT_EMPTY_DESCRIPTION, "description");
        }

        @Test
        @DisplayName("When description is blank Then reports NOT_EMPTY_DESCRIPTION")
        void whenDescriptionIsBlank_thenReportsNotEmptyDescription() {
            // Given - DTO con descripción vacía ("") viola @NotBlank
            // Motivo: la descripción no puede estar vacía para informar sobre el beneficio
            BenefitRequestDto request = invalidRequestWithBlankDescription().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_EMPTY_DESCRIPTION
            assertViolation(violations, ValidationMessages.NOT_EMPTY_DESCRIPTION, "description");
        }

        @Test
        @DisplayName("When description equals max length (1000) Then no violations")
        void whenDescriptionEqualsMaxLength_thenNoViolations() {
            // Given - DTO con descripción de exactamente 1000 caracteres (límite máximo permitido)
            BenefitRequestDto request = validRequest()
                .description(descriptionOfLength(1000))
                .build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - No debe haber violaciones (1000 es el límite permitido)
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When description exceeds max length Then reports MAX_LENGTH_DESCRIPTION_1000")
        void whenDescriptionExceedsMaxLength_thenReportsMaxLengthDescription() {
            // Given - DTO con descripción de más de 1000 caracteres viola @Size(max=1000)
            // Motivo: limitar tamaño para evitar descripciones excesivamente largas en BD y UI
            BenefitRequestDto request = invalidRequestWithDescriptionTooLong().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de MAX_LENGTH_DESCRIPTION_1000
            assertViolation(violations, ValidationMessages.MAX_LENGTH_DESCRIPTION_1000, "description");
        }

        @Test
        @DisplayName("When cost is null Then reports NOT_NULL_COST")
        void whenCostIsNull_thenReportsNotNullCost() {
            // Given - DTO con costo null viola @NotNull
            // Motivo: el costo es requerido para calcular si el estudiante puede comprar el beneficio
            BenefitRequestDto request = invalidRequestWithNullCost().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_NULL_COST
            assertViolation(violations, ValidationMessages.NOT_NULL_COST, "cost");
        }

        @Test
        @DisplayName("When cost equals minimum (1) Then no violations")
        void whenCostEqualsMinimum_thenNoViolations() {
            // Given - DTO con costo de exactamente 1 (mínimo permitido por @Min(1))
            BenefitRequestDto request = validRequest()
                .cost(1L)
                .build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - No debe haber violaciones (1 es el mínimo permitido)
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When cost is less than 1 Then reports MIN_COST")
        void whenCostIsLessThanOne_thenReportsMinCost() {
            // Given - DTO con costo menor a 1 viola @Min(1)
            // Motivo: el beneficio no puede ser gratuito (0) ni tener costo negativo
            BenefitRequestDto request = invalidRequestWithInvalidCost().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de MIN_COST
            assertViolation(violations, ValidationMessages.MIN_COST, "cost");
        }

        @Test
        @DisplayName("When endAt is null Then reports NOT_NULL_END_AT")
        void whenEndAtIsNull_thenReportsNotNullEndAt() {
            // Given - DTO con fecha de finalización null viola @NotNull
            // Motivo: se requiere fecha límite para determinar cuándo expira el beneficio
            BenefitRequestDto request = invalidRequestWithNullEndAt().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_NULL_END_AT
            assertViolation(violations, ValidationMessages.NOT_NULL_END_AT, "endAt");
        }

        @Test
        @DisplayName("When endAt is in the past Then reports FUTURE_END_AT")
        void whenEndAtIsInPast_thenReportsFutureEndAt() {
            // Given - DTO con fecha de finalización en el pasado viola @Future
            // Motivo: un beneficio no puede crearse ya expirado, debe tener fecha futura válida
            BenefitRequestDto request = invalidRequestWithPastEndAt().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de FUTURE_END_AT
            assertViolation(violations, ValidationMessages.FUTURE_END_AT, "endAt");
        }

        @Test
        @DisplayName("When subjectId is null Then reports NOT_NULL_SUBJECT")
        void whenSubjectIdIsNull_thenReportsNotNullSubject() {
            // Given - DTO con subjectId null viola @NotNull
            // Motivo: el beneficio debe estar asociado a una materia para filtrar por contexto
            BenefitRequestDto request = invalidRequestWithNullSubjectId().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_NULL_SUBJECT
            assertViolation(violations, ValidationMessages.NOT_NULL_SUBJECT, "subjectId");
        }

        @Test
        @DisplayName("When icon is null Then reports NOT_NULL_ICON")
        void whenIconIsNull_thenReportsNotNullIcon() {
            // Given - DTO con icon null viola @NotNull
            // Motivo: el icono es requerido para mostrar el beneficio visualmente en la UI
            BenefitRequestDto request = invalidRequestWithNullIcon().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_NULL_ICON
            assertViolation(violations, ValidationMessages.NOT_NULL_ICON, "icon");
        }

        @Test
        @DisplayName("When category is null Then reports NOT_NULL_CATEGORY")
        void whenCategoryIsNull_thenReportsNotNullCategory() {
            // Given - DTO con category null viola @NotNull
            // Motivo: la categoría permite agrupar y filtrar beneficios (EXTRAS, REWARDS, etc.)
            BenefitRequestDto request = invalidRequestWithNullCategory().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_NULL_CATEGORY
            assertViolation(violations, ValidationMessages.NOT_NULL_CATEGORY, "category");
        }

        @Test
        @DisplayName("When color is null Then reports NOT_NULL_COLOR")
        void whenColorIsNull_thenReportsNotNullColor() {
            // Given - DTO con color null viola @NotNull
            // Motivo: el color es requerido para diferenciar visualmente los beneficios en la UI
            BenefitRequestDto request = invalidRequestWithNullColor().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_NULL_COLOR
            assertViolation(violations, ValidationMessages.NOT_NULL_COLOR, "color");
        }

        @Test
        @DisplayName("When purchaseLimit and purchaseLimitPerStudent are null Then no violations (optional fields)")
        void whenOptionalFieldsAreNull_thenNoViolations() {
            // Given - DTO con campos opcionales null (no tienen @NotNull/@NotBlank)
            // Motivo: permiten beneficios ilimitados cuando son null (sin restricción de compras)
            BenefitRequestDto request = validRequestWithOptionalFieldsNull().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - No debe haber violaciones (campos opcionales)
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When multiple fields are invalid Then reports all violations")
        void whenMultipleFieldsInvalid_thenReportsAllViolations() {
            // Given - DTO con múltiples campos inválidos para validar acumulación de errores
            // Motivo: verificar que todas las validaciones se ejecutan y se reportan simultáneamente
            BenefitRequestDto request = invalidRequestWithMultipleViolations().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar todas las violaciones
            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                    ValidationMessages.NOT_EMPTY_NAME,
                    ValidationMessages.NOT_EMPTY_DESCRIPTION,
                    ValidationMessages.NOT_NULL_COST,
                    ValidationMessages.NOT_NULL_SUBJECT
                );
        }

        // Helpers para crear DTOs válidos
        private BenefitRequestDto.BenefitRequestDtoBuilder validRequest() {
            return BenefitTestMother.benefitRequestBuilder(BenefitTestMother.DEFAULT_SUBJECT_ID);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder validRequestWithOptionalFieldsNull() {
            return validRequest()
                .purchaseLimit(null)
                .purchaseLimitPerStudent(null);
        }

        // Helpers para crear DTOs inválidos reutilizables
        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullName() {
            return validRequest().name(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithBlankName() {
            return validRequest().name("");
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNameTooLong() {
            return validRequest().name(nameOfLength(101));
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullDescription() {
            return validRequest().description(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithBlankDescription() {
            return validRequest().description("");
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithDescriptionTooLong() {
            return validRequest().description(descriptionOfLength(1001));
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullCost() {
            return validRequest().cost(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithInvalidCost() {
            return validRequest().cost(0L);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullEndAt() {
            return validRequest().endAt(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithPastEndAt() {
            return validRequest().endAt(LocalDateTime.now().minusDays(1));
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullSubjectId() {
            return validRequest().subjectId(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullIcon() {
            return validRequest().icon(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullCategory() {
            return validRequest().category(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithNullColor() {
            return validRequest().color(null);
        }

        private BenefitRequestDto.BenefitRequestDtoBuilder invalidRequestWithMultipleViolations() {
            return validRequest()
                .name(null)
                .description(null)
                .cost(null)
                .subjectId(null);
        }

        // Helpers utilitarios para generar strings de longitud específica
        private String nameOfLength(int length) {
            return "a".repeat(length);
        }

        private String descriptionOfLength(int length) {
            return "a".repeat(length);
        }
    }

    private <T> void assertViolation(Set<ConstraintViolation<T>> violations, String expectedMessage, String expectedPath) {
        assertThat(violations)
            .hasSize(1)
            .first()
            .satisfies(violation -> {
                assertThat(violation.getMessage()).isEqualTo(expectedMessage);
                assertThat(violation.getPropertyPath().toString()).isEqualTo(expectedPath);
            });
    }
}

