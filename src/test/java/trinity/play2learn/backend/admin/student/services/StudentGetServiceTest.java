package trinity.play2learn.backend.admin.student.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class StudentGetServiceTest {

    private static final long STUDENT_ID = 301L;

    @Mock
    private IStudentGetByIdService studentGetByIdService;

    private StudentGetService studentGetService;

    @BeforeEach
    void setUp() {
        studentGetService = new StudentGetService(studentGetByIdService);
    }

    @Nested
    @DisplayName("cu22GetStudent")
    class GetStudent {

        @Test
        @DisplayName("Given existing student When retrieving Then returns mapped DTO")
        void whenStudentExists_returnsDto() {
            Student student = StudentTestMother.student(STUDENT_ID, "12345678", 10L);
            when(studentGetByIdService.findById(STUDENT_ID)).thenReturn(student);

            StudentResponseDto response = studentGetService.cu22GetStudent(STUDENT_ID);

            verify(studentGetByIdService).findById(STUDENT_ID);
            assertThat(response)
                .extracting(
                    StudentResponseDto::getId,
                    dto -> dto.getUser().getEmail(),
                    dto -> dto.getCourse().getId(),
                    StudentResponseDto::isActive
                )
                .containsExactly(STUDENT_ID, student.getUser().getEmail(), student.getCourse().getId(), true);
        }

        @Test
        @DisplayName("Given student not found When retrieving Then propagates NotFoundException")
        void whenStudentMissing_propagatesNotFound() {
            when(studentGetByIdService.findById(STUDENT_ID)).thenThrow(new NotFoundException("not found"));

            assertThatThrownBy(() -> studentGetService.cu22GetStudent(STUDENT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(studentGetByIdService).findById(STUDENT_ID);
        }
    }
}

