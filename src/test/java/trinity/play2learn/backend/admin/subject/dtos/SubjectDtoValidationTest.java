package trinity.play2learn.backend.admin.subject.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.configs.messages.ValidationMessages;

class SubjectDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("SubjectRequestDto")
    class SubjectRequestDtoValidation {

        @Test
        @DisplayName("Given valid data When validating Then no violations")
        void validRequest_hasNoViolations() {
            SubjectRequestDto dto = subjectRequest("Robotica", 1L, true, 2L);

            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("Given empty name When validating Then returns not empty violation")
        void emptyName_violatesNotEmpty() {
            SubjectRequestDto dto = subjectRequest("", 1L, true, 2L);

            assertThat(messages(dto)).contains(ValidationMessages.NOT_EMPTY_NAME);
        }

        @Test
        @DisplayName("Given invalid pattern name When validating Then returns pattern violation")
        void invalidNamePattern_violatesPattern() {
            SubjectRequestDto dto = subjectRequest("Robótica!!!", 1L, true, 2L);

            assertThat(messages(dto)).contains(ValidationMessages.PATTERN_NAME);
        }

        @Test
        @DisplayName("Given null course id When validating Then returns not null violation")
        void nullCourse_violatesNotNull() {
            SubjectRequestDto dto = subjectRequest("Robotica", null, true, 2L);

            assertThat(messages(dto)).contains(ValidationMessages.NOT_NULL_COURSE);
        }

        @Test
        @DisplayName("Given null optional flag When validating Then returns not null violation")
        void nullOptional_violatesNotNull() {
            SubjectRequestDto dto = subjectRequest("Robotica", 1L, null, 2L);

            assertThat(messages(dto)).contains(ValidationMessages.NOT_NULL_OPTIONAL);
        }

        private SubjectRequestDto subjectRequest(String name, Long courseId, Boolean optional, Long teacherId) {
            return SubjectRequestDto.builder()
                .name(name)
                .courseId(courseId)
                .teacherId(teacherId)
                .optional(optional)
                .build();
        }
    }

    @Nested
    @DisplayName("SubjectUpdateRequestDto")
    class SubjectUpdateRequestDtoValidation {

        @Test
        @DisplayName("Given valid data When validating Then no violations")
        void validUpdate_hasNoViolations() {
            SubjectUpdateRequestDto dto = subjectUpdate("Robotica Avanzada", 1L, false, 2L);

            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("Given name exceeding max length When validating Then returns size violation")
        void nameTooLong_violatesSize() {
            SubjectUpdateRequestDto dto = subjectUpdate("A".repeat(51), 1L, true, null);

            assertThat(messages(dto)).contains(ValidationMessages.MAX_LENGTH_NAME_50);
        }

        private SubjectUpdateRequestDto subjectUpdate(String name, Long courseId, Boolean optional, Long teacherId) {
            return SubjectUpdateRequestDto.builder()
                .name(name)
                .courseId(courseId)
                .teacherId(teacherId)
                .optional(optional)
                .build();
        }
    }

    @Nested
    @DisplayName("SubjectAssignTeacherRequestDto")
    class SubjectAssignTeacherDtoValidation {

        @Test
        @DisplayName("Given valid assign request When validating Then no violations")
        void validAssignRequest_hasNoViolations() {
            SubjectAssignTeacherRequestDto dto = assignRequest(1L, 2L);

            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("Given missing subjectId When validating Then returns violation")
        void missingSubjectId_violatesNotNull() {
            SubjectAssignTeacherRequestDto dto = assignRequest(null, 2L);

            assertThat(propertyPaths(dto)).contains("subjectId");
        }

        @Test
        @DisplayName("Given missing teacherId When validating Then returns violation")
        void missingTeacherId_violatesNotNull() {
            SubjectAssignTeacherRequestDto dto = assignRequest(1L, null);

            assertThat(propertyPaths(dto)).contains("teacherId");
        }

        private SubjectAssignTeacherRequestDto assignRequest(Long subjectId, Long teacherId) {
            return SubjectAssignTeacherRequestDto.builder()
                .subjectId(subjectId)
                .teacherId(teacherId)
                .build();
        }
    }

    private Set<String> messages(Object dto) {
        return validator.validate(dto).stream()
            .map(ConstraintViolation::getMessage)
            .collect(java.util.stream.Collectors.toSet());
    }

    private Set<String> propertyPaths(Object dto) {
        return validator.validate(dto).stream()
            .map(violation -> violation.getPropertyPath().toString())
            .collect(java.util.stream.Collectors.toSet());
    }
}
