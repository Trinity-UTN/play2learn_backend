package trinity.play2learn.backend.admin.teacher.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherExistsByDniService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.user.interfaces.IUserCreateService;

@ExtendWith(MockitoExtension.class)
class TeacherRegisterServiceTest {

    private static final Long TEACHER_ID = 500L;
    private static final String EMAIL = "teacher@example.com";
    private static final String DNI = "12345678";

    @Mock
    private IUserCreateService userCreateService;
    @Mock
    private ITeacherRepository teacherRepository;
    @Mock
    private ITeacherExistsByDniService teacherExistsByDniService;

    private TeacherRegisterService teacherRegisterService;

    @BeforeEach
    void setUp() {
        teacherRegisterService = new TeacherRegisterService(
            userCreateService,
            teacherRepository,
            teacherExistsByDniService
        );
    }

    @Nested
    @DisplayName("cu5RegisterTeacher")
    class RegisterTeacher {

        @Test
        @DisplayName("Given valid request When registering teacher Then validates DNI, creates user and persists teacher")
        void whenRequestValid_persistsTeacher() {
            TeacherRequestDto request = validRegisterRequest();
            User createdUser = TeacherTestMother.teacherUser(901L, EMAIL);

            when(userCreateService.create(EMAIL, DNI, Role.ROLE_TEACHER)).thenReturn(createdUser);
            when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> persistedTeacher(invocation.getArgument(0, Teacher.class)));

            TeacherResponseDto response = teacherRegisterService.cu5RegisterTeacher(request);

            verify(teacherExistsByDniService).validate(DNI);
            verify(userCreateService).create(EMAIL, DNI, Role.ROLE_TEACHER);

            ArgumentCaptor<Teacher> teacherCaptor = ArgumentCaptor.forClass(Teacher.class);
            verify(teacherRepository).save(teacherCaptor.capture());
            Teacher savedTeacher = teacherCaptor.getValue();

            assertThat(savedTeacher)
                .extracting(Teacher::getId, Teacher::getName, Teacher::getLastname, Teacher::getDni, Teacher::getUser)
                .containsExactly(TEACHER_ID, request.getName(), request.getLastname(), DNI, createdUser);

            assertThat(response)
                .extracting(TeacherResponseDto::getId, TeacherResponseDto::getName, TeacherResponseDto::getLastname, TeacherResponseDto::getDni, dto -> dto.getUser().getEmail(), TeacherResponseDto::isActive)
                .containsExactly(TEACHER_ID, request.getName(), request.getLastname(), DNI, EMAIL, true);
        }

        @Test
        @DisplayName("Given duplicate DNI When registering teacher Then throws ConflictException and avoids user creation")
        void whenDniAlreadyExists_throwsConflict() {
            TeacherRequestDto request = validRegisterRequest();

            ConflictException conflict = new ConflictException("Docente duplicado");
            doThrow(conflict)
                .when(teacherExistsByDniService)
                .validate(DNI);

            assertThatThrownBy(() -> teacherRegisterService.cu5RegisterTeacher(request))
                .isInstanceOf(ConflictException.class);

            verify(userCreateService, never()).create(any(), any(), any());
            verify(teacherRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given duplicate user email When registering teacher Then propagates ConflictException and skips persistence")
        void whenUserAlreadyExists_throwsConflict() {
            TeacherRequestDto request = validRegisterRequest();

            ConflictException conflict = new ConflictException("Usuario duplicado");
            when(userCreateService.create(EMAIL, DNI, Role.ROLE_TEACHER)).thenThrow(conflict);

            assertThatThrownBy(() -> teacherRegisterService.cu5RegisterTeacher(request))
                .isInstanceOf(ConflictException.class);

            verify(teacherExistsByDniService).validate(DNI);
            verify(teacherRepository, never()).save(any());
        }
    }
    private TeacherRequestDto validRegisterRequest() {
        return TeacherTestMother.registerRequestBuilder()
            .email(EMAIL)
            .dni(DNI)
            .build();
    }

    private Teacher persistedTeacher(Teacher teacher) {
        teacher.setId(TEACHER_ID);
        return teacher;
    }
}