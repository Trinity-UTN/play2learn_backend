package trinity.play2learn.backend.admin.student.services.commons;

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

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.StudentTestMother;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;

@ExtendWith(MockitoExtension.class)
class StudentsGetByIdListServiceTest {

    private static final List<Long> STUDENT_IDS = List.of(11L, 12L, 13L);

    @Mock
    private IStudentGetByIdService studentGetByIdService;

    private StudentsGetByIdListService studentsGetByIdListService;

    @BeforeEach
    void setUp() {
        studentsGetByIdListService = new StudentsGetByIdListService(studentGetByIdService);
    }

    @Nested
    @DisplayName("getStudentsByIdList")
    class GetStudentsByIdList {

        @Test
        @DisplayName("Given list of ids When retrieving Then returns students preserving order")
        void whenIdsProvided_returnsStudents() {
            Student studentOne = StudentTestMother.student(11L, "10000000", 5L);
            Student studentTwo = StudentTestMother.student(12L, "20000000", 5L);
            Student studentThree = StudentTestMother.student(13L, "30000000", 6L);

            when(studentGetByIdService.findById(11L)).thenReturn(studentOne);
            when(studentGetByIdService.findById(12L)).thenReturn(studentTwo);
            when(studentGetByIdService.findById(13L)).thenReturn(studentThree);

            List<Student> result = studentsGetByIdListService.getStudentsByIdList(STUDENT_IDS);

            verify(studentGetByIdService).findById(11L);
            verify(studentGetByIdService).findById(12L);
            verify(studentGetByIdService).findById(13L);

            assertThat(result)
                .containsExactly(studentOne, studentTwo, studentThree);
        }

        @Test
        @DisplayName("Given empty ids When retrieving Then returns empty list")
        void whenIdsEmpty_returnsEmptyList() {
            List<Student> result = studentsGetByIdListService.getStudentsByIdList(List.of());

            assertThat(result).isEmpty();
        }
    }
}

