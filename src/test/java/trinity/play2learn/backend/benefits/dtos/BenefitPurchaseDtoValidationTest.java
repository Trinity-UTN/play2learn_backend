package trinity.play2learn.backend.benefits.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class BenefitPurchaseDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("BenefitPurchaseRequestDto validation")
    class BenefitPurchaseRequestDtoValidation {

        @Test
        @DisplayName("When request is valid Then no violations are returned")
        void whenValidRequest_thenNoViolations() {
            // Given - DTO válido con benefitId
            BenefitPurchaseRequestDto request = validRequest().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitPurchaseRequestDto>> violations = validator.validate(request);

            // Then - No debe haber violaciones
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When benefitId is null Then reports NOT_NULL_BENEFIT_ID")
        void whenBenefitIdIsNull_thenReportsNotNullBenefitId() {
            // Given - DTO con benefitId null viola @NotNull
            // Motivo: el ID del beneficio es requerido para identificar qué beneficio se está comprando
            BenefitPurchaseRequestDto request = invalidRequestWithNullBenefitId().build();

            // When - Validar el DTO
            Set<ConstraintViolation<BenefitPurchaseRequestDto>> violations = validator.validate(request);

            // Then - Debe reportar violación de NOT_NULL_BENEFIT_ID
            assertViolation(violations, ValidationMessages.NOT_NULL_BENEFIT_ID, "benefitId");
        }
    }

    // Helpers para crear DTOs válidos
    private BenefitPurchaseRequestDto.BenefitPurchaseRequestDtoBuilder validRequest() {
        return BenefitTestMother.benefitPurchaseRequestBuilder(BenefitTestMother.DEFAULT_BENEFIT_ID);
    }

    // Helpers para crear DTOs inválidos reutilizables
    private BenefitPurchaseRequestDto.BenefitPurchaseRequestDtoBuilder invalidRequestWithNullBenefitId() {
        return validRequest().benefitId(null);
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

