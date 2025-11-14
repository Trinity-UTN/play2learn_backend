package trinity.play2learn.backend.admin.student.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.StudentTestMother;

@ExtendWith(MockitoExtension.class)
class StudentGetByCourseServiceTest {

    private static final long COURSE_ID = 77L;

    @Mock
    private IStudentRepository studentRepository;

    private StudentGetByCourseService studentGetByCourseService;

    @BeforeEach
    void setUp() {
        studentGetByCourseService = new StudentGetByCourseService(studentRepository);
    }

    @Nested
    @DisplayName("getStudentsByCourseId")
    class GetStudentsByCourseId {

        @Test
        @DisplayName("Given students in course When retrieving Then returns list")
        void whenStudentsExist_returnsList() {
            Student studentOne = StudentTestMother.student(201L, "12345678", COURSE_ID);
            Student studentTwo = StudentTestMother.student(202L, "87654321", COURSE_ID);
            when(studentRepository.findByCourseId(COURSE_ID)).thenReturn(List.of(studentOne, studentTwo));

            List<Student> result = studentGetByCourseService.getStudentsByCourseId(COURSE_ID);

            verify(studentRepository).findByCourseId(COURSE_ID);
            assertThat(result)
                .extracting(Student::getId, Student::getDni)
                .containsExactly(
                    tuple(studentOne.getId(), studentOne.getDni()),
                    tuple(studentTwo.getId(), studentTwo.getDni())
                );
        }

        @Test
        @DisplayName("Given no students in course When retrieving Then returns empty list")
        void whenNoStudents_returnsEmptyList() {
            when(studentRepository.findByCourseId(COURSE_ID)).thenReturn(List.of());

            List<Student> result = studentGetByCourseService.getStudentsByCourseId(COURSE_ID);

            verify(studentRepository).findByCourseId(COURSE_ID);
            assertThat(result).isEmpty();
        }
    }
}

