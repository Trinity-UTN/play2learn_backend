package trinity.play2learn.backend.admin.teacher.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherUpdateDto;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherDeleteService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRegisterService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRestoreService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherUpdateService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;

@ExtendWith(MockitoExtension.class)
class TeacherWriteControllersTest {

    private static final String TEACHERS_BASE_URL = "/admin/teachers";
    private static final Long TEACHER_ID = 25L;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ITeacherRegisterService teacherRegisterService;

    @Mock
    private ITeacherUpdateService teacherUpdateService;

    @Mock
    private ITeacherDeleteService teacherDeleteService;

    @Mock
    private ITeacherRestoreService teacherRestoreService;

    private TeacherRequestDto registerRequest;
    private TeacherUpdateDto updateRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(
                new TeacherRegisterController(teacherRegisterService),
                new TeacherUpdateController(teacherUpdateService),
                new TeacherDeleteController(teacherDeleteService),
                new TeacherRestoreController(teacherRestoreService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        registerRequest = TeacherTestMother.registerRequestBuilder().build();
        updateRequest = TeacherTestMother.updateRequestBuilder().build();
        reset(teacherRegisterService, teacherUpdateService, teacherDeleteService, teacherRestoreService);
    }

    @Nested
    @DisplayName("POST /admin/teachers")
    class Register {

        @Test
        @DisplayName("Should create teacher and return 201 with response data")
        void shouldRegisterTeacherSuccessfully() throws Exception {
            TeacherResponseDto response = teacherResponse(40L, registerRequest.getDni(), registerRequest.getEmail());
            org.mockito.Mockito.when(teacherRegisterService.cu5RegisterTeacher(registerRequest)).thenReturn(response);

            performPost(registerRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(response.getId()))
                .andExpect(jsonPath("$.data.dni").value(response.getDni()))
                .andExpect(jsonPath("$.data.user.email").value(response.getUser().getEmail()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Docente")));

            verify(teacherRegisterService).cu5RegisterTeacher(registerRequest);
        }

        @Test
        @DisplayName("Should return 409 when service throws ConflictException")
        void shouldFailWhenTeacherAlreadyExists() throws Exception {
            org.mockito.Mockito.when(teacherRegisterService.cu5RegisterTeacher(registerRequest))
                .thenThrow(new ConflictException("Docente duplicado"));

            performPost(registerRequest)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Docente duplicado"))
                .andExpect(jsonPath("$.errors[0]").value("Docente duplicado"));
        }
    }

    @Nested
    @DisplayName("PUT /admin/teachers/{id}")
    class Update {

        @Test
        @DisplayName("Should update teacher and return 200 with response data")
        void shouldUpdateTeacherSuccessfully() throws Exception {
            TeacherResponseDto response = teacherResponse(TEACHER_ID, updateRequest.getDni(), "teacher@example.com");
            org.mockito.Mockito.when(teacherUpdateService.cu23UpdateTeacher(TEACHER_ID, updateRequest))
                .thenReturn(response);

            performPut(updateRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(response.getId()))
                .andExpect(jsonPath("$.data.dni").value(response.getDni()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.updatedSuccessfully("Docente")));
        }

        @Test
        @DisplayName("Should return 404 when teacher not found")
        void shouldReturnNotFoundWhenTeacherMissing() throws Exception {
            org.mockito.Mockito.when(teacherUpdateService.cu23UpdateTeacher(TEACHER_ID, updateRequest))
                .thenThrow(new NotFoundException("Docente no encontrado"));

            performPut(updateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Docente no encontrado"))
                .andExpect(jsonPath("$.errors[0]").value("Docente no encontrado"));
        }
    }

    @Nested
    @DisplayName("DELETE /admin/teachers/{id}")
    class Delete {

        @Test
        @DisplayName("Should delete teacher softly and return 204")
        void shouldDeleteTeacherSuccessfully() throws Exception {
            doNothing().when(teacherDeleteService).cu24DeleteTeacher(TEACHER_ID);

            performDelete()
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.deletedSuccessfully("Docente")));

            verify(teacherDeleteService).cu24DeleteTeacher(TEACHER_ID);
        }

        @Test
        @DisplayName("Should return 409 when teacher has associated subjects")
        void shouldReturnConflictWhenTeacherHasSubjects() throws Exception {
            doThrow(new ConflictException("Docente asociado a materias"))
                .when(teacherDeleteService)
                .cu24DeleteTeacher(TEACHER_ID);

            performDelete()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Docente asociado a materias"))
                .andExpect(jsonPath("$.errors[0]").value("Docente asociado a materias"));
        }
    }

    @Nested
    @DisplayName("PATCH /admin/teachers/restore/{id}")
    class Restore {

        @Test
        @DisplayName("Should restore teacher and return 200")
        void shouldRestoreTeacherSuccessfully() throws Exception {
            TeacherResponseDto response = teacherResponse(TEACHER_ID, "12345678", "teacher@example.com");
            org.mockito.Mockito.when(teacherRestoreService.cu35RestoreTeacher(TEACHER_ID)).thenReturn(response);

            performRestore()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(response.getId()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.restoredSuccessfully("Docente")));
        }

        @Test
        @DisplayName("Should return 404 when teacher to restore is not found")
        void shouldReturnNotFoundWhenRestoreFails() throws Exception {
            org.mockito.Mockito.when(teacherRestoreService.cu35RestoreTeacher(TEACHER_ID))
                .thenThrow(new NotFoundException("Docente eliminado no encontrado"));

            performRestore()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Docente eliminado no encontrado"))
                .andExpect(jsonPath("$.errors[0]").value("Docente eliminado no encontrado"));
        }
    }

    private ResultActions performPost(TeacherRequestDto request) throws Exception {
        return mockMvc.perform(post(TEACHERS_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performPut(TeacherUpdateDto request) throws Exception {
        return mockMvc.perform(put(TEACHERS_BASE_URL + "/{id}", TEACHER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performDelete() throws Exception {
        return mockMvc.perform(delete(TEACHERS_BASE_URL + "/{id}", TEACHER_ID));
    }

    private ResultActions performRestore() throws Exception {
        return mockMvc.perform(patch(TEACHERS_BASE_URL + "/restore/{id}", TEACHER_ID));
    }

    private TeacherResponseDto teacherResponse(Long id, String dni, String email) {
        return TeacherResponseDto.builder()
            .id(id)
            .name("Laura")
            .lastname("Sosa")
            .dni(dni)
            .user(UserResponseDto.builder()
                .id(90L)
                .email(email)
                .build())
            .active(true)
            .build();
    }
}

