package trinity.play2learn.backend.admin.student.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentsListServicesTest {

    @Mock
    private IStudentRepository studentRepository;

    private StudentsListServices studentsListServices;

    @BeforeEach
    void setUp() {
        studentsListServices = new StudentsListServices(studentRepository);
    }

    @Nested
    @DisplayName("cu20ListStudents")
    class ListStudents {

        @Test
        @DisplayName("Given students exist When listing Then returns mapped DTOs")
        void whenStudentsExist_returnsDtoList() {
            Student activeStudent = StudentTestMother.student(501L, "12345678", 20L);
            Student deletedStudent = StudentTestMother.deletedStudent(502L, "87654321", 20L);
            when(studentRepository.findAll()).thenReturn(List.of(activeStudent, deletedStudent));

            List<StudentResponseDto> result = studentsListServices.cu20ListStudents();

            verify(studentRepository).findAll();
            assertThat(result)
                .hasSize(2)
                .extracting(StudentResponseDto::isActive)
                .containsExactly(true, false);
            assertThat(result)
                .extracting(StudentResponseDto::getCourse)
                .extracting(course -> course.getId())
                .containsExactly(20L, 20L);
        }

        @Test
        @DisplayName("Given no students When listing Then returns empty list")
        void whenNoStudents_returnsEmptyList() {
            when(studentRepository.findAll()).thenReturn(List.of());

            List<StudentResponseDto> result = studentsListServices.cu20ListStudents();

            verify(studentRepository).findAll();
            assertThat(result).isEmpty();
        }
    }
}

