package trinity.play2learn.backend.activity.noLudica.controllers;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.noLudica.NoLudicaTestMother;
import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.activity.noLudica.services.interfaces.INoLudicaGenerateService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class NoLudicaGenerateControllerTest {

    private static final String BASE_PATH = "/activities/no-ludica";

    private MockMvc mockMvc;

    @Mock
    private INoLudicaGenerateService noLudicaGenerateService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new NoLudicaGenerateController(noLudicaGenerateService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        reset(noLudicaGenerateService);
    }

    @Nested
    @DisplayName("POST /activities/no-ludica")
    class GenerateNoLudica {

        @Test
        @DisplayName("Given valid request with exercise and tipoEntrega When generating no ludica Then returns NoLudicaResponseDto with 201 Created")
        void shouldGenerateNoLudicaSuccessfully() throws Exception {
            // Given
            NoLudicaRequestDto requestDto = NoLudicaTestMother.validNoLudicaRequestDto();
            NoLudicaResponseDto responseDto = NoLudicaTestMother.validNoLudicaResponseDto(
                ActivityTestMother.ACTIVITY_ID
            );

            when(noLudicaGenerateService.cu45GenerateNoLudica(
                ArgumentMatchers.any(NoLudicaRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            mockMvc.perform(post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("No Ludica"))
                .andExpect(jsonPath("$.data.excercise").value(requestDto.getExcercise()))
                .andExpect(jsonPath("$.data.tipoEntrega").value(requestDto.getTipoEntrega().name()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad no ludica")));

            verify(noLudicaGenerateService).cu45GenerateNoLudica(
                ArgumentMatchers.any(NoLudicaRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating no ludica Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            NoLudicaRequestDto requestDto = NoLudicaTestMother.validNoLudicaRequestDto();

            when(noLudicaGenerateService.cu45GenerateNoLudica(
                ArgumentMatchers.any(NoLudicaRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            mockMvc.perform(post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(noLudicaGenerateService).cu45GenerateNoLudica(
                ArgumentMatchers.any(NoLudicaRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given invalid JSON payload (contenido malformado - edge case) When generating no ludica Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenInvalidJson() throws Exception {
            // Given
            String invalidJson = "{ invalid json }";

            // When & Then
            mockMvc.perform(post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                .andExpect(status().isBadRequest());

            verify(noLudicaGenerateService, org.mockito.Mockito.never()).cu45GenerateNoLudica(
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
            );
        }
    }

}

