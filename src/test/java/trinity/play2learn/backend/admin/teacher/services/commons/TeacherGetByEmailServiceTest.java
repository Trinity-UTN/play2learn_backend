package trinity.play2learn.backend.admin.teacher.services.commons;

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

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class TeacherGetByEmailServiceTest {

    private static final String EMAIL = "teacher@example.com";
    private static final String TEACHER_DNI = "12345678";

    @Mock
    private ITeacherRepository teacherRepository;

    private TeacherGetByEmailService teacherGetByEmailService;

    @BeforeEach
    void setUp() {
        teacherGetByEmailService = new TeacherGetByEmailService(teacherRepository);
    }

    @Nested
    @DisplayName("getByEmail")
    class GetByEmail {

        @Test
        @DisplayName("Given teacher exists When getByEmail Then returns teacher")
        void whenTeacherExists_returnsTeacher() {
            Teacher teacher = TeacherTestMother.teacher(30L, TEACHER_DNI);
            when(teacherRepository.findByUserEmailAndDeletedAtIsNull(EMAIL)).thenReturn(Optional.of(teacher));

            Teacher result = teacherGetByEmailService.getByEmail(EMAIL);

            verify(teacherRepository).findByUserEmailAndDeletedAtIsNull(EMAIL);
            assertTeacher(result, 30L, TEACHER_DNI);
        }

        @Test
        @DisplayName("Given teacher does not exist When getByEmail Then throws NotFoundException")
        void whenTeacherMissing_throwsNotFound() {
            when(teacherRepository.findByUserEmailAndDeletedAtIsNull(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> teacherGetByEmailService.getByEmail(EMAIL))
                .isInstanceOf(NotFoundException.class);
        }
    }

    private void assertTeacher(Teacher teacher, Long expectedId, String expectedDni) {
        assertThat(teacher)
            .extracting(Teacher::getId, Teacher::getDni)
            .containsExactly(expectedId, expectedDni);
    }
}

