package trinity.play2learn.backend.admin.subject.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.admin.subject.dtos.SubjectAssignTeacherRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectUpdateRequestDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAssignTeacherService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectDeleteService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRegisterService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectUnassignTeacherService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectUpdateService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SubjectWriteControllersTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ISubjectRegisterService subjectRegisterService;
    @Mock
    private ISubjectUpdateService subjectUpdateService;
    @Mock
    private ISubjectDeleteService subjectDeleteService;
    @Mock
    private ISubjectAssignTeacherService subjectAssignTeacherService;
    @Mock
    private ISubjectUnassignTeacherService subjectUnassignTeacherService;

    @BeforeEach
    void setUp() {
        SubjectRegisterController registerController = new SubjectRegisterController(subjectRegisterService);
        SubjectUpdateController updateController = new SubjectUpdateController(subjectUpdateService);
        SubjectDeleteController deleteController = new SubjectDeleteController(subjectDeleteService);
        SubjectAssignTeacherController assignController = new SubjectAssignTeacherController(
            subjectAssignTeacherService,
            subjectUnassignTeacherService
        );

        mockMvc = MockMvcBuilders.standaloneSetup(
            registerController,
            updateController,
            deleteController,
            assignController
        ).setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /admin/subjects")
    class RegisterSubject {

        @Test
        @DisplayName("When subject is registered successfully Then returns 201 and payload")
        void registerSubject_success() throws Exception {
            SubjectResponseDto responseDto = SubjectResponseDto.builder().id(1L).name("Robotica").build();
            when(subjectRegisterService.cu28RegisterSubject(any())).thenReturn(responseDto);

            mockMvc.perform(post("/admin/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(SubjectRequestDto.builder()
                        .name("Robotica")
                        .courseId(1L)
                        .optional(true)
                        .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("When registration conflicts Then returns 409")
        void registerSubject_conflict() throws Exception {
            doThrow(new ConflictException("exists"))
                .when(subjectRegisterService)
                .cu28RegisterSubject(any());

            mockMvc.perform(post("/admin/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(SubjectRequestDto.builder()
                        .name("Robotica")
                        .courseId(1L)
                        .optional(true)
                        .build())))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /admin/subjects/{id}")
    class UpdateSubject {

        @Test
        @DisplayName("When update succeeds Then returns 201 with updated payload")
        void updateSubject_success() throws Exception {
            SubjectResponseDto responseDto = SubjectResponseDto.builder().id(2L).name("Robotica").build();
            when(subjectUpdateService.cu29UpdateSubject(any(), any())).thenReturn(responseDto);

            mockMvc.perform(put("/admin/subjects/{id}", 2L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(SubjectUpdateRequestDto.builder()
                        .name("Robotica")
                        .courseId(1L)
                        .optional(false)
                        .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(2L));
        }

        @Test
        @DisplayName("When subject not found Then returns 404")
        void updateSubject_notFound() throws Exception {
            when(subjectUpdateService.cu29UpdateSubject(any(), any()))
                .thenThrow(new NotFoundException("not found"));

            mockMvc.perform(put("/admin/subjects/{id}", 99L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(SubjectUpdateRequestDto.builder()
                        .name("Robotica")
                        .courseId(1L)
                        .optional(false)
                        .build())))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /admin/subjects/{id}")
    class DeleteSubject {

        @Test
        @DisplayName("When deleting existing subject Then returns 204")
        void deleteSubject_success() throws Exception {
            mockMvc.perform(delete("/admin/subjects/{id}", 3L))
                .andExpect(status().isNoContent());

            verify(subjectDeleteService).cu30DeleteSubject(3L);
        }

        @Test
        @DisplayName("When delete conflicts (students assigned) Then returns 409")
        void deleteSubject_conflict() throws Exception {
            doThrow(new ConflictException("students assigned"))
                .when(subjectDeleteService).cu30DeleteSubject(3L);

            mockMvc.perform(delete("/admin/subjects/{id}", 3L))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PATCH /admin/subjects/assign-teacher")
    class AssignTeacher {

        @Test
        @DisplayName("When assigning teacher Then returns 200 with updated DTO")
        void assignTeacher_success() throws Exception {
            SubjectResponseDto responseDto = SubjectResponseDto.builder().id(4L).name("Robotica").build();
            when(subjectAssignTeacherService.cu49AssignTeacher(any())).thenReturn(responseDto);

            mockMvc.perform(patch("/admin/subjects/assign-teacher")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(SubjectAssignTeacherRequestDto.builder()
                        .subjectId(4L)
                        .teacherId(10L)
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(4L));
        }

        @Test
        @DisplayName("When teacher not found Then returns 404")
        void assignTeacher_notFound() throws Exception {
            when(subjectAssignTeacherService.cu49AssignTeacher(any()))
                .thenThrow(new NotFoundException("teacher"));

            mockMvc.perform(patch("/admin/subjects/assign-teacher")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(SubjectAssignTeacherRequestDto.builder()
                        .subjectId(4L)
                        .teacherId(10L)
                        .build())))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /admin/subjects/unassign-teacher/{subjectId}")
    class UnassignTeacher {

        @Test
        @DisplayName("When unassigning teacher Then returns 200")
        void unassignTeacher_success() throws Exception {
            SubjectResponseDto responseDto = SubjectResponseDto.builder().id(5L).name("Robotica").build();
            when(subjectUnassignTeacherService.cu50UnassignTeacher(5L)).thenReturn(responseDto);

            mockMvc.perform(patch("/admin/subjects/unassign-teacher/{subjectId}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(5L));
        }

        @Test
        @DisplayName("When subject not found during unassign Then returns 404")
        void unassignTeacher_notFound() throws Exception {
            when(subjectUnassignTeacherService.cu50UnassignTeacher(5L))
                .thenThrow(new NotFoundException("subject"));

            mockMvc.perform(patch("/admin/subjects/unassign-teacher/{subjectId}", 5L))
                .andExpect(status().isNotFound());
        }
    }

    private String asJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
