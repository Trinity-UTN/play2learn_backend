package trinity.play2learn.backend.admin.student.services.commons;

import static org.assertj.core.api.Assertions.assertThatCode;
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

import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@ExtendWith(MockitoExtension.class)
class StudentsExistByCourseServiceTest {

    private static final long COURSE_ID = 55L;

    @Mock
    private IStudentRepository studentRepository;

    private StudentsExistByCourseService studentsExistByCourseService;

    @BeforeEach
    void setUp() {
        studentsExistByCourseService = new StudentsExistByCourseService(studentRepository);
    }

    @Nested
    @DisplayName("validate")
    class Validate {

        @Test
        @DisplayName("Given course without students When validating Then does not throw")
        void whenCourseWithoutStudents_doesNotThrow() {
            when(studentRepository.existsByCourseId(COURSE_ID)).thenReturn(false);

            assertThatCode(() -> studentsExistByCourseService.validate(COURSE_ID))
                .doesNotThrowAnyException();

            verify(studentRepository).existsByCourseId(COURSE_ID);
        }

        @Test
        @DisplayName("Given course with students When validating Then throws ConflictException")
        void whenCourseWithStudents_throwsConflict() {
            when(studentRepository.existsByCourseId(COURSE_ID)).thenReturn(true);

            assertThatThrownBy(() -> studentsExistByCourseService.validate(COURSE_ID))
                .isInstanceOf(ConflictException.class);

            verify(studentRepository).existsByCourseId(COURSE_ID);
        }
    }
}

