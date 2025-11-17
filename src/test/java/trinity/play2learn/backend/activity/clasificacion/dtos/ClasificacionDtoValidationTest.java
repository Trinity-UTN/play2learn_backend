package trinity.play2learn.backend.activity.clasificacion.dtos;

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
import trinity.play2learn.backend.activity.clasificacion.ClasificacionTestMother;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.CategoryClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ConceptClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class ClasificacionDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("ClasificacionActivityRequestDto validation")
    class ClasificacionActivityRequestDtoValidation {

        @Test
        @DisplayName("Given valid request with 2 categories and concepts When validating Then no violations are returned")
        void request_valid() {
            // Given
            ClasificacionActivityRequestDto request = ClasificacionTestMother.validClasificacionRequestDto();

            // When
            Set<ConstraintViolation<ClasificacionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given categories is null (required field - edge case) When validating Then reports NOT_NULL_CATEGORIES")
        void request_categoriesNull() {
            // Given
            ClasificacionActivityRequestDto request = ClasificacionTestMother.validClasificacionRequestDto();
            request.setCategories(null);

            // When
            Set<ConstraintViolation<ClasificacionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("categories") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_CATEGORIES)
            );
        }

        @Test
        @DisplayName("Given categories has 1 category (should be 2-10 - edge case) When validating Then reports LENGTH_CATEGORIES")
        void request_categoriesHasOneCategory() {
            // Given
            ClasificacionActivityRequestDto request = ClasificacionTestMother.clasificacionRequestDto(
                List.of(ClasificacionTestMother.categoryWithConcepts("Categoría 1", 2))
            );

            // When
            Set<ConstraintViolation<ClasificacionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("categories") &&
                v.getMessage().equals(ValidationMessages.LENGTH_CATEGORIES)
            );
        }

        @Test
        @DisplayName("Given categories has 11 categories (should be 2-10 - edge case) When validating Then reports LENGTH_CATEGORIES")
        void request_categoriesHasElevenCategories() {
            // Given
            List<CategoryClasificacionRequestDto> categories = new ArrayList<>();
            for (int i = 1; i <= 11; i++) {
                categories.add(ClasificacionTestMother.categoryWithConcepts("Categoría " + i, 2));
            }
            ClasificacionActivityRequestDto request = ClasificacionTestMother.clasificacionRequestDto(categories);

            // When
            Set<ConstraintViolation<ClasificacionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("categories") &&
                v.getMessage().equals(ValidationMessages.LENGTH_CATEGORIES)
            );
        }

        @Test
        @DisplayName("Given categories at max length (10 categories - boundary) When validating Then no violations are returned")
        void request_categoriesAtMaxLength() {
            // Given
            List<CategoryClasificacionRequestDto> categories = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                categories.add(ClasificacionTestMother.categoryWithConcepts("Categoría " + i, 2));
            }
            ClasificacionActivityRequestDto request = ClasificacionTestMother.clasificacionRequestDto(categories);

            // When
            Set<ConstraintViolation<ClasificacionActivityRequestDto>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("CategoryClasificacionRequestDto validation")
    class CategoryClasificacionRequestDtoValidation {

        @Test
        @DisplayName("Given valid category with concepts When validating Then no violations are returned")
        void category_valid() {
            // Given
            CategoryClasificacionRequestDto category = ClasificacionTestMother.categoryWithConcepts("Categoría", 2);

            // When
            Set<ConstraintViolation<CategoryClasificacionRequestDto>> violations = validator.validate(category);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME")
        void category_nameBlank() {
            // Given
            CategoryClasificacionRequestDto category = CategoryClasificacionRequestDto.builder()
                .name("")
                .concepts(List.of(ClasificacionTestMother.concept("Concepto 1")))
                .build();

            // When
            Set<ConstraintViolation<CategoryClasificacionRequestDto>> violations = validator.validate(category);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_NAME)
            );
        }

        @Test
        @DisplayName("Given name exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_NAME_50")
        void category_nameExceedsMaxLength() {
            // Given
            CategoryClasificacionRequestDto category = CategoryClasificacionRequestDto.builder()
                .name(ClasificacionTestMother.MAX_LENGTH_CATEGORY_NAME + "a")
                .concepts(List.of(ClasificacionTestMother.concept("Concepto 1")))
                .build();

            // When
            Set<ConstraintViolation<CategoryClasificacionRequestDto>> violations = validator.validate(category);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_NAME_50)
            );
        }

        @Test
        @DisplayName("Given concepts is null (required field) When validating Then reports NOT_NULL_CONCEPTS")
        void category_conceptsNull() {
            // Given
            CategoryClasificacionRequestDto category = CategoryClasificacionRequestDto.builder()
                .name("Categoría")
                .concepts(null)
                .build();

            // When
            Set<ConstraintViolation<CategoryClasificacionRequestDto>> violations = validator.validate(category);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("concepts") &&
                v.getMessage().equals(ValidationMessages.NOT_NULL_CONCEPTS)
            );
        }

        @Test
        @DisplayName("Given concepts is empty (should be 1-10 - edge case - categoría sin conceptos) When validating Then reports LENGTH_CONCEPTS")
        void category_conceptsEmpty() {
            // Given
            CategoryClasificacionRequestDto category = ClasificacionTestMother.categoryWithoutConcepts("Categoría");

            // When
            Set<ConstraintViolation<CategoryClasificacionRequestDto>> violations = validator.validate(category);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("concepts") &&
                v.getMessage().equals(ValidationMessages.LENGTH_CONCEPTS)
            );
        }

        @Test
        @DisplayName("Given concepts has 11 concepts (should be 1-10 - edge case) When validating Then reports LENGTH_CONCEPTS")
        void category_conceptsHasElevenConcepts() {
            // Given
            List<ConceptClasificacionRequestDto> concepts = new ArrayList<>();
            for (int i = 1; i <= 11; i++) {
                concepts.add(ClasificacionTestMother.concept("Concepto " + i));
            }
            CategoryClasificacionRequestDto category = CategoryClasificacionRequestDto.builder()
                .name("Categoría")
                .concepts(concepts)
                .build();

            // When
            Set<ConstraintViolation<CategoryClasificacionRequestDto>> violations = validator.validate(category);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("concepts") &&
                v.getMessage().equals(ValidationMessages.LENGTH_CONCEPTS)
            );
        }

        @Test
        @DisplayName("Given concepts at max length (10 concepts - boundary) When validating Then no violations are returned")
        void category_conceptsAtMaxLength() {
            // Given
            CategoryClasificacionRequestDto category = ClasificacionTestMother.categoryWithConcepts("Categoría", 10);

            // When
            Set<ConstraintViolation<CategoryClasificacionRequestDto>> violations = validator.validate(category);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("ConceptClasificacionRequestDto validation")
    class ConceptClasificacionRequestDtoValidation {

        @Test
        @DisplayName("Given valid concept When validating Then no violations are returned")
        void concept_valid() {
            // Given
            ConceptClasificacionRequestDto concept = ClasificacionTestMother.concept("Concepto");

            // When
            Set<ConstraintViolation<ConceptClasificacionRequestDto>> violations = validator.validate(concept);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME")
        void concept_nameBlank() {
            // Given
            ConceptClasificacionRequestDto concept = ClasificacionTestMother.concept("");

            // When
            Set<ConstraintViolation<ConceptClasificacionRequestDto>> violations = validator.validate(concept);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.NOT_EMPTY_NAME)
            );
        }

        @Test
        @DisplayName("Given name exceeds max length (101 chars - edge case) When validating Then reports MAX_LENGTH_NAME_100")
        void concept_nameExceedsMaxLength() {
            // Given
            ConceptClasificacionRequestDto concept = ClasificacionTestMother.concept(
                ClasificacionTestMother.MAX_LENGTH_CONCEPT_NAME + "a"
            );

            // When
            Set<ConstraintViolation<ConceptClasificacionRequestDto>> violations = validator.validate(concept);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("name") &&
                v.getMessage().equals(ValidationMessages.MAX_LENGTH_NAME_100)
            );
        }

        @Test
        @DisplayName("Given name at max length (100 chars - boundary) When validating Then no violations are returned")
        void concept_nameAtMaxLength() {
            // Given
            ConceptClasificacionRequestDto concept = ClasificacionTestMother.concept(
                ClasificacionTestMother.MAX_LENGTH_CONCEPT_NAME
            );

            // When
            Set<ConstraintViolation<ConceptClasificacionRequestDto>> violations = validator.validate(concept);

            // Then
            assertThat(violations).isEmpty();
        }
    }

}

