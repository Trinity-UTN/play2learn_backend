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

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class StudentRestoreServiceTest {

    private static final long STUDENT_ID = 701L;

    @Mock
    private IStudentGetByIdService studentGetByIdService;
    @Mock
    private IStudentRepository studentRepository;

    private StudentRestoreService studentRestoreService;

    @BeforeEach
    void setUp() {
        studentRestoreService = new StudentRestoreService(studentGetByIdService, studentRepository);
    }

    @Nested
    @DisplayName("cu38RestoreStudent")
    class RestoreStudent {

        @Test
        @DisplayName("Given deleted student When restoring Then removes deletedAt and returns DTO")
        void whenStudentDeleted_restoresAndReturnsDto() {
            Student deletedStudent = StudentTestMother.deletedStudent(STUDENT_ID, "12345678", 15L);

            when(studentGetByIdService.findDeletedById(STUDENT_ID)).thenReturn(deletedStudent);
            when(studentRepository.save(deletedStudent)).thenReturn(deletedStudent);

            StudentResponseDto response = studentRestoreService.cu38RestoreStudent(STUDENT_ID);

            verify(studentGetByIdService).findDeletedById(STUDENT_ID);
            ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(studentCaptor.capture());

            Student restoredStudent = studentCaptor.getValue();
            assertThat(restoredStudent.getDeletedAt()).isNull();
            assertThat(restoredStudent.getUser().getDeletedAt()).isNull();

            assertThat(response.getId()).isEqualTo(STUDENT_ID);
            assertThat(response.isActive()).isTrue();
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        @DisplayName("Given deleted student not found When restoring Then propagates NotFoundException")
        void whenStudentDeletedMissing_propagatesNotFound() {
            when(studentGetByIdService.findDeletedById(STUDENT_ID)).thenThrow(new NotFoundException("not found"));

            assertThatThrownBy(() -> studentRestoreService.cu38RestoreStudent(STUDENT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(studentGetByIdService).findDeletedById(STUDENT_ID);
            verifyNoMoreInteractions(studentRepository);
        }
    }
}

