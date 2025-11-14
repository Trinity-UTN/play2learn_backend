package trinity.play2learn.backend.admin.teacher.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class TeacherRestoreServiceTest {

    private static final long TEACHER_ID = 88L;
    private static final String TEACHER_DNI = "87654321";

    @Mock
    private ITeacherRepository teacherRepository;
    @Mock
    private ITeacherGetByIdService teacherGetByIdService;

    private TeacherRestoreService teacherRestoreService;

    @BeforeEach
    void setUp() {
        teacherRestoreService = new TeacherRestoreService(
            teacherRepository,
            teacherGetByIdService
        );
    }

    @Nested
    @DisplayName("cu35RestoreTeacher")
    class RestoreTeacher {

        @Test
        @DisplayName("Given deleted teacher When restoring Then clears deletedAt and returns response DTO")
        void whenTeacherDeleted_restoresAndReturnsDto() {
            Teacher deletedTeacher = TeacherTestMother.deletedTeacher(TEACHER_ID, TEACHER_DNI);

            when(teacherGetByIdService.findDeletedById(TEACHER_ID)).thenReturn(deletedTeacher);
            when(teacherRepository.save(deletedTeacher)).thenAnswer(invocation -> invocation.getArgument(0, Teacher.class));

            TeacherResponseDto response = teacherRestoreService.cu35RestoreTeacher(TEACHER_ID);

            ArgumentCaptor<Teacher> teacherCaptor = ArgumentCaptor.forClass(Teacher.class);
            verify(teacherRepository).save(teacherCaptor.capture());
            Teacher restoredTeacher = teacherCaptor.getValue();

            assertThat(restoredTeacher.getDeletedAt()).isNull();
            assertThat(restoredTeacher.getUser().getDeletedAt()).isNull();

            assertThat(response.getId()).isEqualTo(TEACHER_ID);
            assertThat(response.getDni()).isEqualTo(TEACHER_DNI);
            assertThat(response.isActive()).isTrue();
        }

        @Test
        @DisplayName("Given teacher not found When restoring Then propagates NotFoundException")
        void whenTeacherNotFound_propagatesNotFound() {
            NotFoundException notFound = new NotFoundException("Docente eliminado no encontrado");
            when(teacherGetByIdService.findDeletedById(TEACHER_ID)).thenThrow(notFound);

            assertThatThrownBy(() -> teacherRestoreService.cu35RestoreTeacher(TEACHER_ID))
                .isInstanceOf(NotFoundException.class);

            verify(teacherRepository, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
        }
    }
}

