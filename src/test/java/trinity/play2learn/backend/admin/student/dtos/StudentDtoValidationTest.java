package trinity.play2learn.backend.admin.student.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.configs.messages.ValidationMessages;

class StudentDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("StudentRequestDto validation")
    class StudentRequestDtoValidation {

        @Test
        @DisplayName("When request is valid Then no violations are returned")
        void request_valid() {
            StudentRequestDto request = validRequest().build();

            Set<ConstraintViolation<StudentRequestDto>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When name is null Then reports NOT_EMPTY_NAME")
        void request_nameNull() {
            StudentRequestDto request = validRequest()
                .name(null)
                .build();

            Set<ConstraintViolation<StudentRequestDto>> violations = validator.validate(request);

            assertViolation(violations, ValidationMessages.NOT_EMPTY_NAME, "name");
        }

        @Test
        @DisplayName("When lastname contains invalid characters Then reports PATTERN_LASTNAME")
        void request_lastnamePattern() {
            StudentRequestDto request = validRequest()
                .lastname("Gomez123")
                .build();

            Set<ConstraintViolation<StudentRequestDto>> violations = validator.validate(request);

            assertViolation(violations, ValidationMessages.PATTERN_LASTNAME, "lastname");
        }

        @Test
        @DisplayName("When email is invalid Then reports PATTERN_EMAIL")
        void request_emailPattern() {
            StudentRequestDto request = validRequest()
                .email("invalid-email")
                .build();

            Set<ConstraintViolation<StudentRequestDto>> violations = validator.validate(request);

            assertViolation(violations, ValidationMessages.PATTERN_EMAIL, "email");
        }

        @Test
        @DisplayName("When DNI has less digits Then reports PATTERN_DNI")
        void request_dniPattern() {
            StudentRequestDto request = validRequest()
                .dni("1234")
                .build();

            Set<ConstraintViolation<StudentRequestDto>> violations = validator.validate(request);

            assertViolation(violations, ValidationMessages.PATTERN_DNI, "dni");
        }

        @Test
        @DisplayName("When course id is null Then reports NOT_NULL_COURSE")
        void request_courseNull() {
            StudentRequestDto request = validRequest()
                .course_id(null)
                .build();

            Set<ConstraintViolation<StudentRequestDto>> violations = validator.validate(request);

            assertViolation(violations, ValidationMessages.NOT_NULL_COURSE, "course_id");
        }

        @Test
        @DisplayName("When tutor email is invalid Then reports PATTERN_EMAIL")
        void request_tutorEmailPattern() {
            StudentRequestDto request = validRequest()
                .emailTutor("not-mail")
                .build();

            Set<ConstraintViolation<StudentRequestDto>> violations = validator.validate(request);

            assertViolation(violations, ValidationMessages.PATTERN_EMAIL, "emailTutor");
        }
    }

    @Nested
    @DisplayName("StudentUpdateRequestDto validation")
    class StudentUpdateRequestDtoValidation {

        @Test
        @DisplayName("When update dto is valid Then no violations are returned")
        void update_valid() {
            StudentUpdateRequestDto dto = validUpdate().build();

            Set<ConstraintViolation<StudentUpdateRequestDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("When name is null Then reports NOT_EMPTY_NAME")
        void update_nameNull() {
            StudentUpdateRequestDto dto = validUpdate()
                .name(null)
                .build();

            Set<ConstraintViolation<StudentUpdateRequestDto>> violations = validator.validate(dto);

            assertViolation(violations, ValidationMessages.NOT_EMPTY_NAME, "name");
        }

        @Test
        @DisplayName("When lastname pattern fails Then reports PATTERN_LASTNAME")
        void update_lastnamePattern() {
            StudentUpdateRequestDto dto = validUpdate()
                .lastname("Lopez99")
                .build();

            Set<ConstraintViolation<StudentUpdateRequestDto>> violations = validator.validate(dto);

            assertViolation(violations, ValidationMessages.PATTERN_LASTNAME, "lastname");
        }

        @Test
        @DisplayName("When DNI has non numeric characters Then reports PATTERN_DNI")
        void update_dniPattern() {
            StudentUpdateRequestDto dto = validUpdate()
                .dni("12AB5678")
                .build();

            Set<ConstraintViolation<StudentUpdateRequestDto>> violations = validator.validate(dto);

            assertViolation(violations, ValidationMessages.PATTERN_DNI, "dni");
        }

        @Test
        @DisplayName("When course id is null Then reports NOT_NULL_COURSE")
        void update_courseNull() {
            StudentUpdateRequestDto dto = validUpdate()
                .course_id(null)
                .build();

            Set<ConstraintViolation<StudentUpdateRequestDto>> violations = validator.validate(dto);

            assertViolation(violations, ValidationMessages.NOT_NULL_COURSE, "course_id");
        }

        @Test
        @DisplayName("When tutor email is invalid Then reports PATTERN_EMAIL")
        void update_tutorEmailPattern() {
            StudentUpdateRequestDto dto = validUpdate()
                .emailTutor("invalid-mail")
                .build();

            Set<ConstraintViolation<StudentUpdateRequestDto>> violations = validator.validate(dto);

            assertViolation(violations, ValidationMessages.PATTERN_EMAIL, "emailTutor");
        }
    }

    private StudentRequestDto.StudentRequestDtoBuilder validRequest() {
        return StudentRequestDto.builder()
            .name("Juan")
            .lastname("Pérez")
            .email("student@example.com")
            .dni("12345678")
            .course_id(10L)
            .emailTutor("tutor@example.com")
            .birthDate(LocalDate.of(2010, 6, 15));
    }

    private StudentUpdateRequestDto.StudentUpdateRequestDtoBuilder validUpdate() {
        return StudentUpdateRequestDto.builder()
            .name("Ana")
            .lastname("Gómez")
            .dni("87654321")
            .course_id(11L)
            .emailTutor("guardian@example.com")
            .birthDate(LocalDate.of(2011, 3, 20));
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

