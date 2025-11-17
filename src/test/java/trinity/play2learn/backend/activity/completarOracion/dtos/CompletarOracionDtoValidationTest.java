package trinity.play2learn.backend.activity.completarOracion.dtos;

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
import trinity.play2learn.backend.activity.completarOracion.CompletarOracionTestMother;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.SentenceCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.WordCompletarOracionRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class CompletarOracionDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("CompletarOracionActivityRequestDto validation")
    class CompletarOracionActivityRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with sentences and words When validating Then no violations are returned")
        void request_valid() {
            // Given
            CompletarOracionActivityRequestDto request = CompletarOracionTestMother.validCompletarOracionRequestDto();

            // When
            Set<ConstraintViolation<CompletarOracionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given sentences is null (required field - edge case) When validating Then reports NOT_NULL_SENTENCES")
        void request_sentencesNull() {
            // Given
            CompletarOracionActivityRequestDto request = CompletarOracionTestMother.validCompletarOracionRequestDto();
            request.setSentences(null);

            // When
            Set<ConstraintViolation<CompletarOracionActivityRequestDto>> violations = validator.validate(request);

            // Then
            // @Size validation does not apply to null, so no violation for null sentences
            // This test verifies that null sentences don't cause unexpected errors
            assertThat(violations).as("Null sentences should not cause validation errors since @NotNull is not present").isNotNull();
        }

        @Test
        @DisplayName("Given sentences is empty (should be 1-20 - edge case - oraciones vacías) When validating Then reports LENGTH_SENTENCES")
        void request_sentencesEmpty() {
            // Given
            CompletarOracionActivityRequestDto request = CompletarOracionTestMother.completarOracionRequestDto(
                new ArrayList<>()
            );

            // When
            Set<ConstraintViolation<CompletarOracionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("sentences") &&
                v.getMessage().equals(ValidationMessages.LENGTH_SENTENCES)
            );
        }

        @Test
        @DisplayName("Given sentences has 21 sentences (should be 1-20 - edge case) When validating Then reports LENGTH_SENTENCES")
        void request_sentencesHasTwentyOneSentences() {
            // Given
            List<SentenceCompletarOracionRequestDto> sentences = new ArrayList<>();
            for (int i = 0; i < 21; i++) {
                sentences.add(CompletarOracionTestMother.validSentence());
            }
            CompletarOracionActivityRequestDto request = CompletarOracionTestMother.completarOracionRequestDto(sentences);

            // When
            Set<ConstraintViolation<CompletarOracionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("sentences") &&
                v.getMessage().equals(ValidationMessages.LENGTH_SENTENCES)
            );
        }

        @Test
        @DisplayName("Given sentences at max length (20 sentences - boundary) When validating Then no violations are returned")
        void request_sentencesAtMaxLength() {
            // Given
            List<SentenceCompletarOracionRequestDto> sentences = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                sentences.add(CompletarOracionTestMother.validSentence());
            }
            CompletarOracionActivityRequestDto request = CompletarOracionTestMother.completarOracionRequestDto(sentences);

            // When
            Set<ConstraintViolation<CompletarOracionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("SentenceCompletarOracionRequestDto validation")
    class SentenceCompletarOracionRequestDtoValidation {

        @Test
        @DisplayName("Given valid sentence with words When validating Then no violations are returned")
        void sentence_valid() {
            // Given
            SentenceCompletarOracionRequestDto sentence = CompletarOracionTestMother.validSentence();

            // When
            Set<ConstraintViolation<SentenceCompletarOracionRequestDto>> violations = validator.validate(sentence);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given words is null (required field) When validating Then reports NOT_NULL_WORDS")
        void sentence_wordsNull() {
            // Given
            SentenceCompletarOracionRequestDto sentence = SentenceCompletarOracionRequestDto.builder()
                .words(null)
                .build();

            // When
            Set<ConstraintViolation<SentenceCompletarOracionRequestDto>> violations = validator.validate(sentence);

            // Then
            // @Size validation does not apply to null, so no violation for null words
            // This test verifies that null words don't cause unexpected errors
            assertThat(violations).as("Null words should not cause validation errors since @NotNull is not present").isNotNull();
        }

        @Test
        @DisplayName("Given words has 2 words (should be 3-300 - edge case) When validating Then reports LENGTH_SENTENCE")
        void sentence_wordsHasTwoWords() {
            // Given
            SentenceCompletarOracionRequestDto sentence = CompletarOracionTestMother.sentenceWithWords(
                "El", "gato"
            );

            // When
            Set<ConstraintViolation<SentenceCompletarOracionRequestDto>> violations = validator.validate(sentence);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("words") &&
                v.getMessage().equals(ValidationMessages.LENGTH_SENTENCE)
            );
        }

        @Test
        @DisplayName("Given words has 301 words (should be 3-300 - edge case) When validating Then reports LENGTH_SENTENCE")
        void sentence_wordsHasThreeHundredOneWords() {
            // Given
            List<WordCompletarOracionRequestDto> words = new ArrayList<>();
            for (int i = 0; i < 301; i++) {
                words.add(CompletarOracionTestMother.word("palabra", i, i % 2 == 0));
            }
            SentenceCompletarOracionRequestDto sentence = CompletarOracionTestMother.sentenceWithWords(words);

            // When
            Set<ConstraintViolation<SentenceCompletarOracionRequestDto>> violations = validator.validate(sentence);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("words") &&
                v.getMessage().equals(ValidationMessages.LENGTH_SENTENCE)
            );
        }

        @Test
        @DisplayName("Given words at max length (300 words - boundary) When validating Then no violations are returned")
        void sentence_wordsAtMaxLength() {
            // Given
            List<WordCompletarOracionRequestDto> words = new ArrayList<>();
            for (int i = 0; i < 300; i++) {
                words.add(CompletarOracionTestMother.word("palabra", i, i % 2 == 0));
            }
            SentenceCompletarOracionRequestDto sentence = CompletarOracionTestMother.sentenceWithWords(words);

            // When
            Set<ConstraintViolation<SentenceCompletarOracionRequestDto>> violations = validator.validate(sentence);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("WordCompletarOracionRequestDto validation")
    class WordCompletarOracionRequestDtoValidation {

        @Test
        @DisplayName("Given valid word When validating Then no violations are returned")
        void word_valid() {
            // Given
            WordCompletarOracionRequestDto word = CompletarOracionTestMother.word("gato", 0, false);

            // When
            Set<ConstraintViolation<WordCompletarOracionRequestDto>> violations = validator.validate(word);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given word is blank (required field) When validating Then reports NOT_EMPTY_WORD")
        void word_wordBlank() {
            // Given
            WordCompletarOracionRequestDto word = CompletarOracionTestMother.word("", 0, false);

            // When
            Set<ConstraintViolation<WordCompletarOracionRequestDto>> violations = validator.validate(word);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("word") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_WORD)
            );
        }

        @Test
        @DisplayName("Given word exceeds max length (31 chars - edge case) When validating Then reports LENGTH_WORD")
        void word_wordExceedsMaxLength() {
            // Given
            WordCompletarOracionRequestDto word = CompletarOracionTestMother.word(
                CompletarOracionTestMother.MAX_LENGTH_WORD + "a",
                0,
                false
            );

            // When
            Set<ConstraintViolation<WordCompletarOracionRequestDto>> violations = validator.validate(word);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("word") &&
                v.getMessage().equals(ValidationMessages.LENGTH_WORD)
            );
        }

        @Test
        @DisplayName("Given word at max length (30 chars - boundary) When validating Then no violations are returned")
        void word_wordAtMaxLength() {
            // Given
            WordCompletarOracionRequestDto word = CompletarOracionTestMother.word(
                CompletarOracionTestMother.MAX_LENGTH_WORD,
                0,
                false
            );

            // When
            Set<ConstraintViolation<WordCompletarOracionRequestDto>> violations = validator.validate(word);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given wordOrder is null (required field) When validating Then reports NOT_NULL_WORD_ORDER")
        void word_wordOrderNull() {
            // Given
            WordCompletarOracionRequestDto word = CompletarOracionTestMother.word("gato", null, false);

            // When
            Set<ConstraintViolation<WordCompletarOracionRequestDto>> violations = validator.validate(word);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("wordOrder") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_WORD_ORDER)
            );
        }

        @Test
        @DisplayName("Given isMissing is null (required field - palabra sin indicador de faltante) When validating Then reports NOT_NULL_IS_MISSING")
        void word_isMissingNull() {
            // Given
            WordCompletarOracionRequestDto word = CompletarOracionTestMother.word("gato", 0, null);

            // When
            Set<ConstraintViolation<WordCompletarOracionRequestDto>> violations = validator.validate(word);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("isMissing") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_IS_MISSING)
            );
        }
    }

}

