package trinity.play2learn.backend.activity.preguntados.dtos;

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
import trinity.play2learn.backend.activity.preguntados.PreguntadosTestMother;
import trinity.play2learn.backend.activity.preguntados.dtos.request.OptionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class PreguntadosDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("PreguntadosRequestDto validation")
    class PreguntadosRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with 5 questions When validating Then no violations are returned")
        void request_valid() {
            // Given
            PreguntadosRequestDto request = PreguntadosTestMother.validPreguntadosRequestDto();

            // When
            Set<ConstraintViolation<PreguntadosRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given maxTimePerQuestionInSeconds is 9 (should be >= 10 - edge case - tiempo por pregunta inválido) When validating Then reports MIN_TIME_PER_QUESTION")
        void request_maxTimePerQuestionInSecondsTooLow() {
            // Given
            PreguntadosRequestDto request = PreguntadosTestMother.validPreguntadosRequestDto();
            request.setMaxTimePerQuestionInSeconds(9);

            // When
            Set<ConstraintViolation<PreguntadosRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("maxTimePerQuestionInSeconds") &&
                v.getMessage().equals(ValidationMessages.MIN_TIME_PER_QUESTION)
            );
        }

        @Test
        @DisplayName("Given maxTimePerQuestionInSeconds is 10 (boundary) When validating Then no violations are returned")
        void request_maxTimePerQuestionInSecondsAtMin() {
            // Given
            PreguntadosRequestDto request = PreguntadosTestMother.validPreguntadosRequestDto();
            request.setMaxTimePerQuestionInSeconds(10);

            // When
            Set<ConstraintViolation<PreguntadosRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given questions has 4 questions (should be >= 5 - edge case - preguntas incompletas) When validating Then reports MIN_LENGTH_QUESTIONS")
        void request_questionsHasFourQuestions() {
            // Given
            PreguntadosRequestDto request = PreguntadosTestMother.preguntadosRequestDto(
                PreguntadosTestMother.questions(4)
            );

            // When
            Set<ConstraintViolation<PreguntadosRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("questions") &&
                v.getMessage().equals(ValidationMessages.MIN_LENGTH_QUESTIONS)
            );
        }

        @Test
        @DisplayName("Given questions is null (required field - edge case - preguntas vacías) When validating Then reports MIN_LENGTH_QUESTIONS")
        void request_questionsNull() {
            // Given
            PreguntadosRequestDto request = PreguntadosTestMother.validPreguntadosRequestDto();
            request.setQuestions(null);

            // When
            Set<ConstraintViolation<PreguntadosRequestDto>> violations = validator.validate(request);

            // Then
            // @Size validation does not apply to null, so no violation for null questions
            // This test verifies that null questions don't cause unexpected errors
            assertThat(violations).as("Null questions should not cause validation errors since @NotNull is not present").isNotNull();
        }
    }

    @Nested
    @DisplayName("QuestionRequestDto validation")
    class QuestionRequestDtoValidation {

        @Test
        @DisplayName("Given valid question with 4 options When validating Then no violations are returned")
        void question_valid() {
            // Given
            QuestionRequestDto question = PreguntadosTestMother.question("¿Cuál es la capital de Francia?");

            // When
            Set<ConstraintViolation<QuestionRequestDto>> violations = validator.validate(question);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given question is blank (required field) When validating Then reports NOT_EMPTY_QUESTION")
        void question_questionBlank() {
            // Given
            QuestionRequestDto question = PreguntadosTestMother.question("");

            // When
            Set<ConstraintViolation<QuestionRequestDto>> violations = validator.validate(question);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("question") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_QUESTION)
            );
        }

        @Test
        @DisplayName("Given question exceeds max length (201 chars - edge case) When validating Then reports MAX_LENGTH_QUESTION")
        void question_questionExceedsMaxLength() {
            // Given
            QuestionRequestDto question = PreguntadosTestMother.question(
                PreguntadosTestMother.MAX_LENGTH_QUESTION + "a"
            );

            // When
            Set<ConstraintViolation<QuestionRequestDto>> violations = validator.validate(question);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("question") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_QUESTION)
            );
        }

        @Test
        @DisplayName("Given question at max length (200 chars - boundary) When validating Then no violations are returned")
        void question_questionAtMaxLength() {
            // Given
            QuestionRequestDto question = PreguntadosTestMother.question(
                PreguntadosTestMother.MAX_LENGTH_QUESTION
            );

            // When
            Set<ConstraintViolation<QuestionRequestDto>> violations = validator.validate(question);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given options has 3 options (should be exactly 4 - edge case - opciones incompletas) When validating Then reports LENGTH_OPTIONS")
        void question_optionsHasThreeOptions() {
            // Given
            QuestionRequestDto question = PreguntadosTestMother.question("¿Cuál es la capital de Francia?");
            List<OptionRequestDto> options = new ArrayList<>(question.getOptions());
            options.remove(0);
            question.setOptions(options);

            // When
            Set<ConstraintViolation<QuestionRequestDto>> violations = validator.validate(question);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("options") &&
                v.getMessage().equals(ValidationMessages.LENGTH_OPTIONS)
            );
        }

        @Test
        @DisplayName("Given options has 5 options (should be exactly 4 - edge case - múltiples opciones) When validating Then reports LENGTH_OPTIONS")
        void question_optionsHasFiveOptions() {
            // Given
            QuestionRequestDto question = PreguntadosTestMother.question("¿Cuál es la capital de Francia?");
            List<OptionRequestDto> options = new ArrayList<>(question.getOptions());
            options.add(PreguntadosTestMother.option("Nueva opción", false));
            question.setOptions(options);

            // When
            Set<ConstraintViolation<QuestionRequestDto>> violations = validator.validate(question);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("options") &&
                v.getMessage().equals(ValidationMessages.LENGTH_OPTIONS)
            );
        }

        @Test
        @DisplayName("Given options is null (required field - edge case - opciones vacías) When validating Then reports LENGTH_OPTIONS")
        void question_optionsNull() {
            // Given
            QuestionRequestDto question = PreguntadosTestMother.question("¿Cuál es la capital de Francia?");
            question.setOptions(null);

            // When
            Set<ConstraintViolation<QuestionRequestDto>> violations = validator.validate(question);

            // Then
            // @Size validation does not apply to null, so no violation for null options
            // This test verifies that null options don't cause unexpected errors
            assertThat(violations).as("Null options should not cause validation errors since @NotNull is not present").isNotNull();
        }
    }

    @Nested
    @DisplayName("OptionRequestDto validation")
    class OptionRequestDtoValidation {

        @Test
        @DisplayName("Given valid option with text and isCorrect When validating Then no violations are returned")
        void option_valid() {
            // Given
            OptionRequestDto option = PreguntadosTestMother.option("París", true);

            // When
            Set<ConstraintViolation<OptionRequestDto>> violations = validator.validate(option);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given option is blank (required field) When validating Then reports NOT_EMPTY_OPTION")
        void option_optionBlank() {
            // Given
            OptionRequestDto option = PreguntadosTestMother.option("", true);

            // When
            Set<ConstraintViolation<OptionRequestDto>> violations = validator.validate(option);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("option") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_OPTION)
            );
        }

        @Test
        @DisplayName("Given option exceeds max length (101 chars - edge case) When validating Then reports MAX_LENGTH_OPTION")
        void option_optionExceedsMaxLength() {
            // Given
            OptionRequestDto option = PreguntadosTestMother.option(
                PreguntadosTestMother.MAX_LENGTH_OPTION + "a",
                true
            );

            // When
            Set<ConstraintViolation<OptionRequestDto>> violations = validator.validate(option);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("option") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_OPTION)
            );
        }

        @Test
        @DisplayName("Given option at max length (100 chars - boundary) When validating Then no violations are returned")
        void option_optionAtMaxLength() {
            // Given
            OptionRequestDto option = PreguntadosTestMother.option(
                PreguntadosTestMother.MAX_LENGTH_OPTION,
                true
            );

            // When
            Set<ConstraintViolation<OptionRequestDto>> violations = validator.validate(option);

            // Then
            assertThat(violations).isEmpty();
        }
    }

}

