package trinity.play2learn.backend.admin.teacher.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

class TeacherDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("TeacherRequestDto validation")
    class TeacherRequestDtoValidation {

        @Test
        @DisplayName("Should pass validation with all valid fields")
        void shouldPassWithValidRequest() {
            TeacherRequestDto request = validRequest();

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail when name is null")
        void shouldFailWhenNameNull() {
            TeacherRequestDto request = requestWith(validRequest(), null, "Sosa", "teacher@example.com", "12345678");

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.NOT_EMPTY_NAME);
        }

        @Test
        @DisplayName("Should fail when lastname exceeds max length")
        void shouldFailWhenLastnameTooLong() {
            TeacherRequestDto request = requestWith(validRequest(), "Laura", repeat("a", 51), "teacher@example.com", "12345678");

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.MAX_LENGTH_LASTNAME);
        }

        @Test
        @DisplayName("Should fail when email is invalid")
        void shouldFailWhenEmailInvalid() {
            TeacherRequestDto request = requestWith(validRequest(), "Laura", "Sosa", "teacher", "12345678");

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.PATTERN_EMAIL);
        }

        @Test
        @DisplayName("Should fail when email exceeds max length")
        void shouldFailWhenEmailTooLong() {
            String longEmail = repeat("a", 101) + "@example.com";
            TeacherRequestDto request = requestWith(validRequest(), "Laura", "Sosa", longEmail, "12345678");

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.MAX_LENGTH_EMAIL);
        }

        @Test
        @DisplayName("Should fail when dni has invalid format")
        void shouldFailWhenDniInvalid() {
            TeacherRequestDto request = requestWith(validRequest(), "Laura", "Sosa", "teacher@example.com", "1234ABCD");

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.PATTERN_DNI);
        }

        @Test
        @DisplayName("Should fail with multiple violations when fields blank")
        void shouldFailWithMultipleViolationsWhenBlankFields() {
            TeacherRequestDto request = requestWith(validRequest(), "", "", "", "");

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                    ValidationMessages.NOT_EMPTY_NAME,
                    ValidationMessages.NOT_EMPTY_LASTNAME,
                    ValidationMessages.NOT_EMPTY_EMAIL,
                    ValidationMessages.NOT_EMPTY_DNI
                );
        }

        @Test
        @DisplayName("Should fail when lastname contains digits")
        void shouldFailWhenLastnameContainsDigits() {
            TeacherRequestDto request = requestWith(validRequest(), "Laura", "Sosa123", "teacher@example.com", "12345678");

            Set<ConstraintViolation<TeacherRequestDto>> violations = validator.validate(request);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.PATTERN_LASTNAME);
        }

        private TeacherRequestDto validRequest() {
            return TeacherRequestDto.builder()
                .name("Laura")
                .lastname("Sosa")
                .email("teacher@example.com")
                .dni("12345678")
                .build();
        }
    }

    @Nested
    @DisplayName("TeacherUpdateDto validation")
    class TeacherUpdateDtoValidation {

        @Test
        @DisplayName("Should pass validation with all valid fields")
        void shouldPassWithValidUpdate() {
            TeacherUpdateDto dto = validUpdate();

            Set<ConstraintViolation<TeacherUpdateDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail when name contains invalid characters")
        void shouldFailWhenNameInvalid() {
            TeacherUpdateDto dto = updateWith(validUpdate(), "Laura123", "Sosa", "12345678");

            Set<ConstraintViolation<TeacherUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.PATTERN_NAME);
        }

        @Test
        @DisplayName("Should fail when lastname is null")
        void shouldFailWhenLastnameNull() {
            TeacherUpdateDto dto = updateWith(validUpdate(), "Laura", null, "12345678");

            Set<ConstraintViolation<TeacherUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.NOT_EMPTY_LASTNAME);
        }

        @Test
        @DisplayName("Should fail when dni is null")
        void shouldFailWhenDniNull() {
            TeacherUpdateDto dto = updateWith(validUpdate(), "Laura", "Sosa", null);

            Set<ConstraintViolation<TeacherUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.NOT_EMPTY_DNI);
        }

        @Test
        @DisplayName("Should fail when lastname exceeds max length")
        void shouldFailWhenLastnameTooLong() {
            TeacherUpdateDto dto = updateWith(validUpdate(), "Laura", repeat("a", 51), "12345678");

            Set<ConstraintViolation<TeacherUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.MAX_LENGTH_LASTNAME);
        }

        @Test
        @DisplayName("Should fail when dni has invalid characters")
        void shouldFailWhenDniInvalidFormat() {
            TeacherUpdateDto dto = updateWith(validUpdate(), "Laura", "Sosa", "12AB5678");

            Set<ConstraintViolation<TeacherUpdateDto>> violations = validator.validate(dto);

            assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(ValidationMessages.PATTERN_DNI);
        }

        private TeacherUpdateDto validUpdate() {
            return TeacherUpdateDto.builder()
                .name("Laura")
                .lastname("Sosa")
                .dni("12345678")
                .build();
        }
    }

    private String repeat(String value, int length) {
        return value.repeat(length);
    }

    private TeacherRequestDto requestWith(TeacherRequestDto base, String name, String lastname, String email, String dni) {
        return TeacherRequestDto.builder()
            .name(name)
            .lastname(lastname)
            .email(email)
            .dni(dni)
            .build();
    }

    private TeacherUpdateDto updateWith(TeacherUpdateDto base, String name, String lastname, String dni) {
        return TeacherUpdateDto.builder()
            .name(name)
            .lastname(lastname)
            .dni(dni)
            .build();
    }
}
