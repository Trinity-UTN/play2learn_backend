package trinity.play2learn.backend.activity.preguntados.controllers;

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
import trinity.play2learn.backend.activity.preguntados.PreguntadosTestMother;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.activity.preguntados.services.interfaces.IPreguntadosGenerateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class PreguntadosGenerateControllerTest {

    private static final String BASE_PATH = "/activities/preguntados";

    private MockMvc mockMvc;

    @Mock
    private IPreguntadosGenerateService preguntadosGenerateService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new PreguntadosGenerateController(preguntadosGenerateService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        reset(preguntadosGenerateService);
    }

    @Nested
    @DisplayName("POST /activities/preguntados")
    class GeneratePreguntados {

        @Test
        @DisplayName("Given valid request with 5 questions and options When generating preguntados Then returns PreguntadosResponseDto with 201 Created")
        void shouldGeneratePreguntadosSuccessfully() throws Exception {
            // Given
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();
            PreguntadosResponseDto responseDto = PreguntadosTestMother.validPreguntadosResponseDto(
                ActivityTestMother.ACTIVITY_ID
            );

            when(preguntadosGenerateService.cu40GeneratePreguntados(
                ArgumentMatchers.any(PreguntadosRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            mockMvc.perform(post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("Preguntados"))
                .andExpect(jsonPath("$.data.questions").isArray())
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad de preguntados")));

            verify(preguntadosGenerateService).cu40GeneratePreguntados(
                ArgumentMatchers.any(PreguntadosRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating preguntados Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();

            when(preguntadosGenerateService.cu40GeneratePreguntados(
                ArgumentMatchers.any(PreguntadosRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            mockMvc.perform(post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(preguntadosGenerateService).cu40GeneratePreguntados(
                ArgumentMatchers.any(PreguntadosRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given question without correct option (pregunta sin opción correcta - edge case) When generating preguntados Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenQuestionWithoutCorrectOption() throws Exception {
            // Given
            PreguntadosRequestDto requestDto = PreguntadosTestMother.validPreguntadosRequestDto();

            when(preguntadosGenerateService.cu40GeneratePreguntados(
                ArgumentMatchers.any(PreguntadosRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new BadRequestException("Debe haber exactamente una opción correcta por pregunta"));

            // When & Then
            mockMvc.perform(post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Debe haber exactamente una opción correcta por pregunta"))
                .andExpect(jsonPath("$.errors[0]").value("Debe haber exactamente una opción correcta por pregunta"));

            verify(preguntadosGenerateService).cu40GeneratePreguntados(
                ArgumentMatchers.any(PreguntadosRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given invalid JSON payload (contenido malformado - edge case) When generating preguntados Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenInvalidJson() throws Exception {
            // Given
            String invalidJson = "{ invalid json }";

            // When & Then
            mockMvc.perform(post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                .andExpect(status().isBadRequest());

            verify(preguntadosGenerateService, org.mockito.Mockito.never()).cu40GeneratePreguntados(
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
            );
        }
    }

}

