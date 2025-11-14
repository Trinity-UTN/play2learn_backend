package trinity.play2learn.backend.admin.teacher.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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

import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectExistsByTeacherService;
import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class TeacherDeleteServiceTest {

    private static final long TEACHER_ID = 77L;
    private static final String TEACHER_DNI = "12345678";

    @Mock
    private ITeacherRepository teacherRepository;
    @Mock
    private ITeacherGetByIdService teacherGetByIdService;
    @Mock
    private ISubjectExistsByTeacherService subjectExistsByTeacherService;

    private TeacherDeleteService teacherDeleteService;

    @BeforeEach
    void setUp() {
        teacherDeleteService = new TeacherDeleteService(
            teacherRepository,
            teacherGetByIdService,
            subjectExistsByTeacherService
        );
    }

    @Nested
    @DisplayName("cu24DeleteTeacher")
    class DeleteTeacher {

        @Test
        @DisplayName("Given teacher without subjects When deleting Then performs soft delete and persists")
        void whenTeacherHasNoSubjects_softDeleteAndSave() {
            Teacher teacher = TeacherTestMother.teacher(TEACHER_ID, TEACHER_DNI);

            when(teacherGetByIdService.findById(TEACHER_ID)).thenReturn(teacher);
            doNothing().when(subjectExistsByTeacherService).validate(teacher);

            teacherDeleteService.cu24DeleteTeacher(TEACHER_ID);

            verify(subjectExistsByTeacherService).validate(teacher);

            ArgumentCaptor<Teacher> teacherCaptor = ArgumentCaptor.forClass(Teacher.class);
            verify(teacherRepository).save(teacherCaptor.capture());

            Teacher savedTeacher = teacherCaptor.getValue();
            assertThat(savedTeacher.getDeletedAt()).isNotNull();
            assertThat(savedTeacher.getUser().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("Given teacher not found When deleting Then propagates NotFoundException")
        void whenTeacherNotFound_propagatesNotFound() {
            NotFoundException notFound = new NotFoundException("Docente no encontrado");
            when(teacherGetByIdService.findById(TEACHER_ID)).thenThrow(notFound);

            assertThatThrownBy(() -> teacherDeleteService.cu24DeleteTeacher(TEACHER_ID))
                .isInstanceOf(NotFoundException.class);

            verify(subjectExistsByTeacherService, never()).validate(org.mockito.ArgumentMatchers.any());
            verify(teacherRepository, never()).save(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("Given teacher associated to subjects When deleting Then throws ConflictException and avoids save")
        void whenTeacherHasSubjects_throwsConflict() {
            Teacher teacher = TeacherTestMother.teacher(TEACHER_ID, TEACHER_DNI);

            when(teacherGetByIdService.findById(TEACHER_ID)).thenReturn(teacher);
            doThrow(new ConflictException("Conflicto")).when(subjectExistsByTeacherService).validate(teacher);

            assertThatThrownBy(() -> teacherDeleteService.cu24DeleteTeacher(TEACHER_ID))
                .isInstanceOf(ConflictException.class);

            verify(teacherRepository, never()).save(org.mockito.ArgumentMatchers.any());
        }
    }
}

