package trinity.play2learn.backend.admin.student.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.StudentTestMother;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class StudentGetByEmailServiceTest {

    private static final String EMAIL = "active@student.com";

    @Mock
    private IStudentRepository studentRepository;

    private StudentGetByEmailService studentGetByEmailService;

    @BeforeEach
    void setUp() {
        studentGetByEmailService = new StudentGetByEmailService(studentRepository);
    }

    @Nested
    @DisplayName("getByEmail")
    class GetByEmail {

        @Test
        @DisplayName("Given active student exists When retrieving by email Then returns entity")
        void whenStudentExists_returnsEntity() {
            Student student = StudentTestMother.studentWithEmail(1001L, "12345678", 30L, EMAIL);
            when(studentRepository.findByUserEmailAndDeletedAtIsNull(EMAIL)).thenReturn(Optional.of(student));

            Student result = studentGetByEmailService.getByEmail(EMAIL);

            verify(studentRepository).findByUserEmailAndDeletedAtIsNull(EMAIL);
            assertThat(result)
                .extracting(Student::getId, studentEntity -> studentEntity.getUser().getEmail())
                .containsExactly(student.getId(), EMAIL);
        }

        @Test
        @DisplayName("Given student missing When retrieving by email Then throws NotFoundException")
        void whenStudentMissing_throwsNotFound() {
            when(studentRepository.findByUserEmailAndDeletedAtIsNull(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentGetByEmailService.getByEmail(EMAIL))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(EMAIL);

            verify(studentRepository).findByUserEmailAndDeletedAtIsNull(EMAIL);
        }
    }
}

