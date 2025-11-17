package trinity.play2learn.backend.activity.memorama.dtos;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.memorama.MemoramaTestMother;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class MemoramaDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("MemoramaRequestDto validation")
    class MemoramaRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with couples When validating Then no violations are returned")
        void request_valid() {
            // Given
            MemoramaRequestDto request = MemoramaTestMother.validMemoramaRequestDto();

            // When
            Set<ConstraintViolation<MemoramaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given couples is null (required field - edge case) When validating Then reports NOT_EMPTY_COUPLES")
        void request_couplesNull() {
            // Given
            MemoramaRequestDto request = MemoramaTestMother.validMemoramaRequestDto();
            request.setCouples(null);

            // When
            Set<ConstraintViolation<MemoramaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("couples")
            );
        }

        @Test
        @DisplayName("Given couples is empty (should be 4-8 - edge case - parejas vacías) When validating Then reports MIN_COUPLES")
        void request_couplesEmpty() {
            // Given
            MemoramaRequestDto request = MemoramaTestMother.memoramaRequestDto(new ArrayList<>());

            // When
            Set<ConstraintViolation<MemoramaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("couples") &&
                v.getMessage().equals(ValidationMessages.MIN_COUPLES)
            );
        }

        @Test
        @DisplayName("Given couples has 3 couples (should be 4-8 - edge case - parejas incompletas) When validating Then reports LENGTH_COUPLES")
        void request_couplesHasThreeCouples() {
            // Given
            MemoramaRequestDto request = MemoramaTestMother.memoramaRequestDto(
                MemoramaTestMother.couples(3)
            );

            // When
            Set<ConstraintViolation<MemoramaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("couples") &&
                v.getMessage().equals(ValidationMessages.LENGTH_COUPLES)
            );
        }

        @Test
        @DisplayName("Given couples has 9 couples (should be 4-8 - edge case) When validating Then reports LENGTH_COUPLES")
        void request_couplesHasNineCouples() {
            // Given
            MemoramaRequestDto request = MemoramaTestMother.memoramaRequestDto(
                MemoramaTestMother.couples(9)
            );

            // When
            Set<ConstraintViolation<MemoramaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("couples") &&
                v.getMessage().equals(ValidationMessages.LENGTH_COUPLES)
            );
        }

        @Test
        @DisplayName("Given couples at max length (8 couples - boundary) When validating Then no violations are returned")
        void request_couplesAtMaxLength() {
            // Given
            MemoramaRequestDto request = MemoramaTestMother.memoramaRequestDto(
                MemoramaTestMother.couples(8)
            );

            // When
            Set<ConstraintViolation<MemoramaRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("CouplesMemoramaRequestDto validation")
    class CouplesMemoramaRequestDtoValidation {

        @Test
        @DisplayName("Given valid couple with concept and image When validating Then no violations are returned")
        void couple_valid() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.couple("Perro");

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given concept is blank (required field) When validating Then reports NOT_NULL_CONCEPT")
        void couple_conceptBlank() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.couple("");

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("concept") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_CONCEPT)
            );
        }

        @Test
        @DisplayName("Given concept exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_CONCEPT")
        void couple_conceptExceedsMaxLength() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.couple(
                MemoramaTestMother.MAX_LENGTH_CONCEPT + "a"
            );

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("concept") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_CONCEPT)
            );
        }

        @Test
        @DisplayName("Given concept at max length (50 chars - boundary) When validating Then no violations are returned")
        void couple_conceptAtMaxLength() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.couple(
                MemoramaTestMother.MAX_LENGTH_CONCEPT
            );

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given concept contains numbers (invalid pattern - concepto con números) When validating Then reports PATTERN_CONCEPT")
        void couple_conceptContainsNumbers() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.couple("Perro123");

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("concept") &&
                v.getMessage().equals(ValidationMessages.PATTERN_CONCEPT)
            );
        }

        @Test
        @DisplayName("Given concept contains special chars (invalid pattern - concepto con caracteres especiales) When validating Then reports PATTERN_CONCEPT")
        void couple_conceptContainsSpecialChars() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.couple("Perro@#$");

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("concept") &&
                v.getMessage().equals(ValidationMessages.PATTERN_CONCEPT)
            );
        }

        @Test
        @DisplayName("Given concept with accents (valid pattern) When validating Then no violations are returned")
        void couple_conceptWithAccents() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.couple("Niño");

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given image is null (required field - pareja sin imagen) When validating Then reports NOT_NULL_IMAGE")
        void couple_imageNull() {
            // Given
            CouplesMemoramaRequestDto couple = MemoramaTestMother.coupleWithoutImage("Perro");

            // When
            Set<ConstraintViolation<CouplesMemoramaRequestDto>> violations = validator.validate(couple);

            // Then
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("image") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_IMAGE)
            );
        }
    }

}

