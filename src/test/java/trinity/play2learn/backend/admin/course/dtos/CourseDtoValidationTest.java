package trinity.play2learn.backend.admin.course.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@DisplayName("Course DTO validation")
class CourseDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Nested
    @DisplayName("CourseRequestDto")
    class CourseRequestDtoValidation {

        @Test
        @DisplayName("Es válido cuando nombre y año cumplen restricciones")
        void shouldBeValidWhenFieldsAreCorrect() {
            CourseRequestDto dto = CourseRequestDto.builder()
                .name("Matemática")
                .year_id(2025L)
                .build();

            Set<ConstraintViolation<CourseRequestDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Produce violaciones cuando el nombre está vacío")
        void shouldFailWhenNameIsEmpty() {
            CourseRequestDto dto = CourseRequestDto.builder()
                .name("")
                .year_id(2025L)
                .build();

            Set<ConstraintViolation<CourseRequestDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.NOT_EMPTY_NAME);
        }

        @Test
        @DisplayName("Produce violaciones cuando el nombre supera 50 caracteres")
        void shouldFailWhenNameTooLong() {
            CourseRequestDto dto = CourseRequestDto.builder()
                .name("a".repeat(51))
                .year_id(2025L)
                .build();

            Set<ConstraintViolation<CourseRequestDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(ValidationMessages.MAX_LENGTH_NAME_50);
        }

        @Test
        @DisplayName("Produce violaciones cuando el nombre contiene caracteres no permitidos")
        void shouldFailWhenNameHasInvalidCharacters() {
            CourseRequestDto dto = CourseRequestDto.builder()
                .name("Curso-101")
                .year_id(2025L)
                .build();

            Set<ConstraintViolation<CourseRequestDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(ValidationMessages.PATTERN_NAME);
        }

        @Test
        @DisplayName("Produce violaciones cuando el año es nulo")
        void shouldFailWhenYearIsNull() {
            CourseRequestDto dto = CourseRequestDto.builder()
                .name("Matemática")
                .year_id(null)
                .build();

            Set<ConstraintViolation<CourseRequestDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(ValidationMessages.NOT_NULL_YEAR);
        }
    }

    @Nested
    @DisplayName("CourseUpdateDto")
    class CourseUpdateDtoValidation {

        @Test
        @DisplayName("Es válido cuando el nombre cumple restricciones")
        void shouldBeValidWhenNameIsCorrect() {
            CourseUpdateDto dto = CourseUpdateDto.builder()
                .name("Lengua")
                .build();

            Set<ConstraintViolation<CourseUpdateDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Produce violaciones cuando el nombre es nulo o vacío")
        void shouldFailWhenNameBlank() {
            CourseUpdateDto dto = CourseUpdateDto.builder()
                .name("")
                .build();

            Set<ConstraintViolation<CourseUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.NOT_EMPTY_NAME);
        }

        @Test
        @DisplayName("Produce violaciones cuando el nombre supera 50 caracteres")
        void shouldFailWhenNameTooLong() {
            CourseUpdateDto dto = CourseUpdateDto.builder()
                .name("a".repeat(51))
                .build();

            Set<ConstraintViolation<CourseUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(ValidationMessages.MAX_LENGTH_NAME_50);
        }

        @Test
        @DisplayName("Produce violaciones cuando el nombre contiene caracteres no permitidos")
        void shouldFailWhenNameInvalidCharacters() {
            CourseUpdateDto dto = CourseUpdateDto.builder()
                .name("Curso#1")
                .build();

            Set<ConstraintViolation<CourseUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(ValidationMessages.PATTERN_NAME);
        }
    }
}

