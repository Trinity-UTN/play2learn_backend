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
class TeacherGetByIdServiceTest {

    private static final long TEACHER_ID = 15L;
    private static final String TEACHER_DNI = "12345678";

    @Mock
    private ITeacherRepository teacherRepository;

    private TeacherGetByIdService teacherGetByIdService;

    @BeforeEach
    void setUp() {
        teacherGetByIdService = new TeacherGetByIdService(teacherRepository);
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Given existing active teacher When findById Then returns teacher")
        void whenActiveTeacherExists_returnsTeacher() {
            Teacher teacher = TeacherTestMother.teacher(TEACHER_ID, TEACHER_DNI);
            when(teacherRepository.findByIdAndDeletedAtIsNull(TEACHER_ID)).thenReturn(Optional.of(teacher));

            Teacher result = teacherGetByIdService.findById(TEACHER_ID);

            verify(teacherRepository).findByIdAndDeletedAtIsNull(TEACHER_ID);
            assertTeacher(result, TEACHER_ID, TEACHER_DNI);
        }

        @Test
        @DisplayName("Given missing active teacher When findById Then throws NotFoundException")
        void whenActiveTeacherMissing_throwsNotFound() {
            when(teacherRepository.findByIdAndDeletedAtIsNull(TEACHER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> teacherGetByIdService.findById(TEACHER_ID))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findDeletedById")
    class FindDeletedById {

        @Test
        @DisplayName("Given deleted teacher exists When findDeletedById Then returns teacher")
        void whenDeletedTeacherExists_returnsTeacher() {
            Teacher deletedTeacher = TeacherTestMother.deletedTeacher(TEACHER_ID, TEACHER_DNI);
            when(teacherRepository.findByIdAndDeletedAtIsNotNull(TEACHER_ID)).thenReturn(Optional.of(deletedTeacher));

            Teacher result = teacherGetByIdService.findDeletedById(TEACHER_ID);

            verify(teacherRepository).findByIdAndDeletedAtIsNotNull(TEACHER_ID);
            assertTeacher(result, TEACHER_ID, TEACHER_DNI);
        }

        @Test
        @DisplayName("Given deleted teacher missing When findDeletedById Then throws NotFoundException")
        void whenDeletedTeacherMissing_throwsNotFound() {
            when(teacherRepository.findByIdAndDeletedAtIsNotNull(TEACHER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> teacherGetByIdService.findDeletedById(TEACHER_ID))
                .isInstanceOf(NotFoundException.class);
        }
    }

    private void assertTeacher(Teacher teacher, long expectedId, String expectedDni) {
        assertThat(teacher)
            .extracting(Teacher::getId, Teacher::getDni)
            .containsExactly(expectedId, expectedDni);
    }
}

