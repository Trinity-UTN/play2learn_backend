package trinity.play2learn.backend.admin.student.controllers;

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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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

import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.dtos.StudentUpdateRequestDto;
import trinity.play2learn.backend.admin.student.services.StudentRegisterService;
import trinity.play2learn.backend.admin.student.services.StudentTestMother;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentDeleteService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentRestoreService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentUpdateService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class StudentWriteControllersTest {

    private static final String BASE_URL = "/admin/students";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private StudentRegisterService studentRegisterService;
    @Mock
    private IStudentUpdateService studentUpdateService;
    @Mock
    private IStudentDeleteService studentDeleteService;
    @Mock
    private IStudentRestoreService studentRestoreService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(
                new StudentRegisterController(studentRegisterService),
                new StudentUpdateController(studentUpdateService),
                new StudentDeleteController(studentDeleteService),
                new StudentRestoreController(studentRestoreService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Nested
    @DisplayName("POST /admin/students")
    class RegisterStudent {

        @Test
        @DisplayName("When registration succeeds Then returns 201 with response data")
        void registerStudent_success() throws Exception {
            StudentResponseDto responseDto = StudentResponseDto.builder()
                .id(1L)
                .name("Juan")
                .build();
            when(studentRegisterService.cu4registerStudent(any(StudentRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(registerRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.message").exists());

            verify(studentRegisterService).cu4registerStudent(any(StudentRequestDto.class));
        }

        @Test
        @DisplayName("When registration conflicts Then returns 409")
        void registerStudent_conflict() throws Exception {
            when(studentRegisterService.cu4registerStudent(any(StudentRequestDto.class)))
                .thenThrow(new ConflictException("exists"));

            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(registerRequest())))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /admin/students/{id}")
    class UpdateStudent {

        @Test
        @DisplayName("When update succeeds Then returns 201 with updated payload")
        void updateStudent_success() throws Exception {
            StudentResponseDto responseDto = StudentResponseDto.builder()
                .id(2L)
                .name("Ana")
                .build();
            when(studentUpdateService.cu18updateStudent(any(StudentUpdateRequestDto.class), any(Long.class)))
                .thenReturn(responseDto);

            mockMvc.perform(put(BASE_URL + "/{id}", 2L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(updateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(2L));

            verify(studentUpdateService).cu18updateStudent(any(StudentUpdateRequestDto.class), any(Long.class));
        }

        @Test
        @DisplayName("When student not found Then returns 404")
        void updateStudent_notFound() throws Exception {
            when(studentUpdateService.cu18updateStudent(any(StudentUpdateRequestDto.class), any(Long.class)))
                .thenThrow(new NotFoundException("student"));

            mockMvc.perform(put(BASE_URL + "/{id}", 55L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(updateRequest())))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /admin/students/{id}")
    class DeleteStudent {

        @Test
        @DisplayName("When deleting existing student Then returns 204")
        void deleteStudent_success() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/{id}", 3L))
                .andExpect(status().isNoContent());

            verify(studentDeleteService).cu19DeleteStudent(3L);
        }

        @Test
        @DisplayName("When student not found on delete Then returns 404")
        void deleteStudent_notFound() throws Exception {
            doThrow(new NotFoundException("student")).when(studentDeleteService).cu19DeleteStudent(3L);

            mockMvc.perform(delete(BASE_URL + "/{id}", 3L))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /admin/students/restore/{id}")
    class RestoreStudent {

        @Test
        @DisplayName("When restore succeeds Then returns 200 with DTO")
        void restoreStudent_success() throws Exception {
            StudentResponseDto responseDto = StudentResponseDto.builder()
                .id(5L)
                .name("Ana")
                .build();
            when(studentRestoreService.cu38RestoreStudent(5L)).thenReturn(responseDto);

            mockMvc.perform(patch(BASE_URL + "/restore/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(5L));

            verify(studentRestoreService).cu38RestoreStudent(5L);
        }

        @Test
        @DisplayName("When restore student missing Then returns 404")
        void restoreStudent_notFound() throws Exception {
            when(studentRestoreService.cu38RestoreStudent(5L)).thenThrow(new NotFoundException("student"));

            mockMvc.perform(patch(BASE_URL + "/restore/{id}", 5L))
                .andExpect(status().isNotFound());
        }
    }

    private String asJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private StudentRequestDto registerRequest() {
        return StudentTestMother.registerRequestBuilder(10L).build();
    }

    private StudentUpdateRequestDto updateRequest() {
        return StudentTestMother.updateRequestBuilder(20L).build();
    }
}

