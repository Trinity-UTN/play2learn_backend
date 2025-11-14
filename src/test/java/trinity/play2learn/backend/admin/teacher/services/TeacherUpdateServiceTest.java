package trinity.play2learn.backend.admin.teacher.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

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
import trinity.play2learn.backend.admin.teacher.dtos.TeacherUpdateDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherExistsByDniService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class TeacherUpdateServiceTest {

    private static final long TEACHER_ID = 42L;
    private static final String ORIGINAL_DNI = "12345678";
    private static final String UPDATED_DNI = "87654321";

    @Mock
    private ITeacherRepository teacherRepository;
    @Mock
    private ITeacherGetByIdService teacherGetByIdService;
    @Mock
    private ITeacherExistsByDniService teacherExistsByDniService;

    private TeacherUpdateService teacherUpdateService;

    @BeforeEach
    void setUp() {
        teacherUpdateService = new TeacherUpdateService(
            teacherRepository,
            teacherGetByIdService,
            teacherExistsByDniService
        );
    }

    @Nested
    @DisplayName("cu23UpdateTeacher")
    class UpdateTeacher {

        @Test
        @DisplayName("Given valid update request When updating teacher Then persists changes and returns DTO")
        void whenDataValid_updatesTeacher() {
            Teacher existingTeacher = TeacherTestMother.teacher(TEACHER_ID, ORIGINAL_DNI);
            TeacherUpdateDto request = updatedRequest();

            when(teacherGetByIdService.findById(TEACHER_ID)).thenReturn(existingTeacher);
            when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> invocation.getArgument(0, Teacher.class));

            TeacherResponseDto response = teacherUpdateService.cu23UpdateTeacher(TEACHER_ID, request);

            verify(teacherGetByIdService).findById(TEACHER_ID);
            verify(teacherExistsByDniService).validate(request.getDni(), TEACHER_ID);

            ArgumentCaptor<Teacher> teacherCaptor = ArgumentCaptor.forClass(Teacher.class);
            verify(teacherRepository).save(teacherCaptor.capture());
            Teacher savedTeacher = teacherCaptor.getValue();

            assertThat(savedTeacher.getId()).isEqualTo(TEACHER_ID);
            assertThat(savedTeacher.getName()).isEqualTo(request.getName());
            assertThat(savedTeacher.getLastname()).isEqualTo(request.getLastname());
            assertThat(savedTeacher.getDni()).isEqualTo(request.getDni());
            assertThat(savedTeacher.getUser()).isEqualTo(existingTeacher.getUser());

            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getLastname()).isEqualTo(request.getLastname());
            assertThat(response.getDni()).isEqualTo(request.getDni());
            assertThat(response.isActive()).isTrue();
        }

        @Test
        @DisplayName("Given duplicated DNI When updating teacher Then throws ConflictException and avoids persistence")
        void whenDniDuplicated_throwsConflict() {
            Teacher existingTeacher = TeacherTestMother.teacher(TEACHER_ID, ORIGINAL_DNI);
            TeacherUpdateDto request = TeacherTestMother.updateRequestBuilder()
                .dni(UPDATED_DNI)
                .build();

            when(teacherGetByIdService.findById(TEACHER_ID)).thenReturn(existingTeacher);

            ConflictException conflict = new ConflictException("DNI duplicado");
            doThrow(conflict)
                .when(teacherExistsByDniService)
                .validate(UPDATED_DNI, TEACHER_ID);

            assertThatThrownBy(() -> teacherUpdateService.cu23UpdateTeacher(TEACHER_ID, request))
                .isInstanceOf(ConflictException.class);

            verify(teacherRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given teacher not found When updating Then propagates NotFoundException")
        void whenTeacherMissing_propagatesNotFound() {
            TeacherUpdateDto request = TeacherTestMother.updateRequestBuilder()
                .dni(ORIGINAL_DNI)
                .build();

            when(teacherGetByIdService.findById(TEACHER_ID)).thenThrow(new NotFoundException("Docente no encontrado"));

            assertThatThrownBy(() -> teacherUpdateService.cu23UpdateTeacher(TEACHER_ID, request))
                .isInstanceOf(NotFoundException.class);

            verify(teacherExistsByDniService, never()).validate(any(), anyLong());
            verify(teacherRepository, never()).save(any());
        }
    }
    private TeacherUpdateDto updatedRequest() {
        return TeacherTestMother.updateRequestBuilder()
            .name("Laura Actualizada")
            .lastname("Gómez")
            .dni(UPDATED_DNI)
            .build();
    }
}
