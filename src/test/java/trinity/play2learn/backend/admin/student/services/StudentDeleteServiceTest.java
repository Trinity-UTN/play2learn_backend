package trinity.play2learn.backend.admin.student.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class StudentDeleteServiceTest {

    private static final long STUDENT_ID = 601L;

    @Mock
    private IStudentGetByIdService studentGetByIdService;
    @Mock
    private IStudentRepository studentRepository;

    private StudentDeleteService studentDeleteService;

    @BeforeEach
    void setUp() {
        studentDeleteService = new StudentDeleteService(studentGetByIdService, studentRepository);
    }

    @Nested
    @DisplayName("cu19DeleteStudent")
    class DeleteStudent {

        @Test
        @DisplayName("Given existing student When deleting Then marks as deleted and persists")
        void whenStudentExists_marksAsDeleted() {
            Student student = StudentTestMother.student(STUDENT_ID, "12345678", 12L);

            when(studentGetByIdService.findById(STUDENT_ID)).thenReturn(student);

            studentDeleteService.cu19DeleteStudent(STUDENT_ID);

            verify(studentGetByIdService).findById(STUDENT_ID);
            ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(captor.capture());

            Student deletedStudent = captor.getValue();
            assertThat(deletedStudent.getDeletedAt()).isNotNull();
            assertThat(deletedStudent.getUser().getDeletedAt()).isNotNull();
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        @DisplayName("Given student not found When deleting Then propagates NotFoundException")
        void whenStudentMissing_propagatesNotFound() {
            when(studentGetByIdService.findById(STUDENT_ID)).thenThrow(new NotFoundException("not found"));

            assertThatThrownBy(() -> studentDeleteService.cu19DeleteStudent(STUDENT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(studentGetByIdService).findById(STUDENT_ID);
            verifyNoMoreInteractions(studentRepository);
        }
    }
}

