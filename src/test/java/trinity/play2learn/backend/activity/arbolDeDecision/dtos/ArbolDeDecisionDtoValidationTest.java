package trinity.play2learn.backend.activity.arbolDeDecision.dtos;

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
import trinity.play2learn.backend.activity.arbolDeDecision.ArbolDeDecisionTestMother;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ConsecuenceArbolDecisionRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.DecisionArbolDecisionRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class ArbolDeDecisionDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("ArbolDeDecisionActivityRequestDto validation")
    class ArbolDeDecisionActivityRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with introduction and 2 decisions When validating Then no violations are returned")
        void request_valid() {
            // Given
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given introduction is blank (edge case) When validating Then reports NOT_BLANK")
        void request_introductionBlank() {
            // Given
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();
            request.setIntroduction("");

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("introduction") &&
                (v.getMessage().contains("no puede estar vacio") || v.getMessage().contains("no debe estar vacío"))
            );
        }

        @Test
        @DisplayName("Given introduction is null (edge case) When validating Then reports NOT_BLANK")
        void request_introductionNull() {
            // Given
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();
            request.setIntroduction(null);

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("introduction") &&
                (v.getMessage().contains("no puede estar vacio") || v.getMessage().contains("no debe estar vacío"))
            );
        }

        @Test
        @DisplayName("Given introduction exceeds max length (501 chars - edge case) When validating Then reports INTRODUCTION_500_MAX_LENGHT")
        void request_introductionExceedsMaxLength() {
            // Given
            String longIntroduction = "a".repeat(501);
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();
            request.setIntroduction(longIntroduction);

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("introduction") &&
                v.getMessage().equals(ValidationMessages.INTRODUCTION_500_MAX_LENGHT)
            );
        }

        @Test
        @DisplayName("Given introduction at max length (500 chars - boundary) When validating Then no violations are returned")
        void request_introductionAtMaxLength() {
            // Given
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.arbolDeDecisionRequestDto(
                ArbolDeDecisionTestMother.MAX_LENGTH_INTRODUCTION,
                ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto().getDecisionTree()
            );

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given decisionTree is null (required field - edge case) When validating Then reports NOT_NULL_DECISION_TREE")
        void request_decisionTreeNull() {
            // Given
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();
            request.setDecisionTree(null);

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("decisionTree") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_DECISION_TREE)
            );
        }

        @Test
        @DisplayName("Given decisionTree has 1 decision (should be 2 - edge case) When validating Then reports OPTIONS_SIZE")
        void request_decisionTreeHasOneDecision() {
            // Given
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.arbolDeDecisionRequestDto(
                ArbolDeDecisionTestMother.DEFAULT_INTRODUCTION,
                List.of(ArbolDeDecisionTestMother.decisionWithConsequence("Decisión 1", "Aprobado", true))
            );

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("decisionTree") &&
                v.getMessage().equals(ValidationMessages.OPTIONS_SIZE)
            );
        }

        @Test
        @DisplayName("Given decisionTree has 3 decisions (should be 2 - edge case) When validating Then reports OPTIONS_SIZE")
        void request_decisionTreeHasThreeDecisions() {
            // Given
            ArbolDeDecisionActivityRequestDto request = ArbolDeDecisionTestMother.arbolDeDecisionRequestDto(
                ArbolDeDecisionTestMother.DEFAULT_INTRODUCTION,
                List.of(
                    ArbolDeDecisionTestMother.decisionWithConsequence("Decisión 1", "Aprobado", true),
                    ArbolDeDecisionTestMother.decisionWithConsequence("Decisión 2", "Desaprobado", false),
                    ArbolDeDecisionTestMother.decisionWithConsequence("Decisión 3", "Neutro", false)
                )
            );

            // When
            Set<ConstraintViolation<ArbolDeDecisionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("decisionTree") &&
                v.getMessage().equals(ValidationMessages.OPTIONS_SIZE)
            );
        }
    }

    @Nested
    @DisplayName("DecisionArbolDecisionRequestDto validation")
    class DecisionArbolDecisionRequestDtoValidation {

        @Test
        @DisplayName("Given valid decision with consequence When validating Then no violations are returned")
        void decision_validWithConsequence() {
            // Given
            DecisionArbolDecisionRequestDto decision = ArbolDeDecisionTestMother.decisionWithConsequence(
                "Decisión",
                "Aprobado",
                true
            );

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given valid decision with 2 options When validating Then no violations are returned")
        void decision_validWithOptions() {
            // Given
            DecisionArbolDecisionRequestDto decision = ArbolDeDecisionTestMother.decisionWithOptions(
                "Decisión",
                ArbolDeDecisionTestMother.DEFAULT_CONTEXT
            );

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME")
        void decision_nameBlank() {
            // Given
            DecisionArbolDecisionRequestDto decision = ArbolDeDecisionTestMother.decisionWithConsequence("", "Aprobado", true);

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_NAME)
            );
        }

        @Test
        @DisplayName("Given name exceeds max length (201 chars - edge case) When validating Then reports NAME_200_MAX_LENGHT")
        void decision_nameExceedsMaxLength() {
            // Given
            DecisionArbolDecisionRequestDto decision = ArbolDeDecisionTestMother.decisionWithConsequence(
                ArbolDeDecisionTestMother.MAX_LENGTH_DECISION_NAME + "a",
                "Aprobado",
                true
            );

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.NAME_200_MAX_LENGHT)
            );
        }

        @Test
        @DisplayName("Given context exceeds max length (501 chars - edge case) When validating Then reports CONTEXT_500_MAX_LENGHT")
        void decision_contextExceedsMaxLength() {
            // Given
            DecisionArbolDecisionRequestDto decision = DecisionArbolDecisionRequestDto.builder()
                .name("Decisión")
                .context(ArbolDeDecisionTestMother.MAX_LENGTH_CONTEXT + "a")
                .consecuence(ArbolDeDecisionTestMother.consequence("Aprobado", true))
                .options(new ArrayList<>())
                .build();

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("context") &&
                v.getMessage().equals(ValidationMessages.CONTEXT_500_MAX_LENGHT)
            );
        }

        @Test
        @DisplayName("Given decision has 1 option (should be 0 or 2 - edge case) When validating Then reports OPTIONS_SIZE")
        void decision_hasOneOption() {
            // Given
            DecisionArbolDecisionRequestDto decision = DecisionArbolDecisionRequestDto.builder()
                .name("Decisión")
                .context(ArbolDeDecisionTestMother.DEFAULT_CONTEXT)
                .options(List.of(ArbolDeDecisionTestMother.decisionWithConsequence("Opción 1", "Aprobado", true)))
                .build();

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("optionsSizeValid") &&
                v.getMessage().equals(ValidationMessages.OPTIONS_SIZE)
            );
        }

        @Test
        @DisplayName("Given decision has 3 options (should be 0 or 2 - edge case) When validating Then reports OPTIONS_SIZE")
        void decision_hasThreeOptions() {
            // Given
            DecisionArbolDecisionRequestDto decision = DecisionArbolDecisionRequestDto.builder()
                .name("Decisión")
                .context(ArbolDeDecisionTestMother.DEFAULT_CONTEXT)
                .options(List.of(
                    ArbolDeDecisionTestMother.decisionWithConsequence("Opción 1", "Aprobado", true),
                    ArbolDeDecisionTestMother.decisionWithConsequence("Opción 2", "Desaprobado", false),
                    ArbolDeDecisionTestMother.decisionWithConsequence("Opción 3", "Neutro", false)
                ))
                .build();

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("optionsSizeValid") &&
                v.getMessage().equals(ValidationMessages.OPTIONS_SIZE)
            );
        }

        @Test
        @DisplayName("Given decision has no options and no consequence (edge case - árbol vacío) When validating Then reports OPTIONS_AND_CONSECUENCE_NULL")
        void decision_hasNoOptionsAndNoConsequence() {
            // Given
            DecisionArbolDecisionRequestDto decision = ArbolDeDecisionTestMother.decisionWithoutOptionsOrConsequence(
                "Decisión"
            );

            // When
            Set<ConstraintViolation<DecisionArbolDecisionRequestDto>> violations = validator.validate(decision);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                (v.getPropertyPath().toString().equals("hasOptionsOrConsecuence") || v.getPropertyPath().toString().equals("optionsOrConsecuence")) &&
                (v.getMessage().equals(ValidationMessages.OPTIONS_AND_CONSECUENCE_NULL) || v.getMessage().contains("debe tener opciones o una consecuencia"))
            );
        }
    }

    @Nested
    @DisplayName("ConsecuenceArbolDecisionRequestDto validation")
    class ConsecuenceArbolDecisionRequestDtoValidation {

        @Test
        @DisplayName("Given valid consequence When validating Then no violations are returned")
        void consequence_valid() {
            // Given
            ConsecuenceArbolDecisionRequestDto consequence = ArbolDeDecisionTestMother.consequence("Aprobado", true);

            // When
            Set<ConstraintViolation<ConsecuenceArbolDecisionRequestDto>> violations = validator.validate(consequence);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME")
        void consequence_nameBlank() {
            // Given
            ConsecuenceArbolDecisionRequestDto consequence = ArbolDeDecisionTestMother.consequence("", true);

            // When
            Set<ConstraintViolation<ConsecuenceArbolDecisionRequestDto>> violations = validator.validate(consequence);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_NAME)
            );
        }

        @Test
        @DisplayName("Given name exceeds max length (201 chars - edge case) When validating Then reports NAME_200_MAX_LENGHT")
        void consequence_nameExceedsMaxLength() {
            // Given
            ConsecuenceArbolDecisionRequestDto consequence = ArbolDeDecisionTestMother.consequence(
                ArbolDeDecisionTestMother.MAX_LENGTH_DECISION_NAME + "a",
                true
            );

            // When
            Set<ConstraintViolation<ConsecuenceArbolDecisionRequestDto>> violations = validator.validate(consequence);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.NAME_200_MAX_LENGHT)
            );
        }
    }

}

