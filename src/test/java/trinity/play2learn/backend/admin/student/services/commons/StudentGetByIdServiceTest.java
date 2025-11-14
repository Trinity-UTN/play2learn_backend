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
class StudentGetByIdServiceTest {

    private static final long STUDENT_ID = 901L;

    @Mock
    private IStudentRepository studentRepository;

    private StudentGetByIdService studentGetByIdService;

    @BeforeEach
    void setUp() {
        studentGetByIdService = new StudentGetByIdService(studentRepository);
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Given active student exists When finding by id Then returns entity")
        void whenStudentActiveExists_returnsEntity() {
            Student student = StudentTestMother.student(STUDENT_ID, "12345678", 15L);
            when(studentRepository.findByIdAndDeletedAtIsNull(STUDENT_ID)).thenReturn(Optional.of(student));

            Student result = studentGetByIdService.findById(STUDENT_ID);

            verify(studentRepository).findByIdAndDeletedAtIsNull(STUDENT_ID);
            assertThat(result)
                .extracting(Student::getId, Student::getDni, studentEntity -> studentEntity.getCourse().getId())
                .containsExactly(STUDENT_ID, student.getDni(), student.getCourse().getId());
        }

        @Test
        @DisplayName("Given student missing When finding by id Then throws NotFoundException")
        void whenStudentMissing_throwsNotFound() {
            when(studentRepository.findByIdAndDeletedAtIsNull(STUDENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentGetByIdService.findById(STUDENT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(studentRepository).findByIdAndDeletedAtIsNull(STUDENT_ID);
        }
    }

    @Nested
    @DisplayName("findDeletedById")
    class FindDeletedById {

        @Test
        @DisplayName("Given deleted student exists When finding by id Then returns entity")
        void whenDeletedStudentExists_returnsEntity() {
            Student deletedStudent = StudentTestMother.deletedStudent(STUDENT_ID, "87654321", 18L);
            when(studentRepository.findByIdAndDeletedAtIsNotNull(STUDENT_ID)).thenReturn(Optional.of(deletedStudent));

            Student result = studentGetByIdService.findDeletedById(STUDENT_ID);

            verify(studentRepository).findByIdAndDeletedAtIsNotNull(STUDENT_ID);
            assertThat(result)
                .extracting(Student::getId, Student::getDni)
                .containsExactly(STUDENT_ID, deletedStudent.getDni());
        }

        @Test
        @DisplayName("Given deleted student missing When finding by id Then throws NotFoundException")
        void whenDeletedStudentMissing_throwsNotFound() {
            when(studentRepository.findByIdAndDeletedAtIsNotNull(STUDENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentGetByIdService.findDeletedById(STUDENT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(studentRepository).findByIdAndDeletedAtIsNotNull(STUDENT_ID);
        }
    }
}

