package trinity.play2learn.backend.activity.ahorcado.controllers;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.ahorcado.AhorcadoTestMother;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.activity.ahorcado.services.interfaces.IAhorcadoGenerateService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class GenerateAhorcadoControllerTest {

    private static final String BASE_PATH = "/activities/ahorcado";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IAhorcadoGenerateService generateAhorcadoService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(
                new GenerateAhorcadoController(generateAhorcadoService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        reset(generateAhorcadoService);
    }

    @Nested
    @DisplayName("POST /activities/ahorcado")
    class GenerateAhorcado {

        @Test
        @DisplayName("Given valid request When generating ahorcado Then returns AhorcadoResponseDto with 201 Created")
        void shouldGenerateAhorcadoSuccessfully() throws Exception {
            // Given
            AhorcadoRequestDto requestDto = AhorcadoTestMother.validAhorcadoRequestDto();
            AhorcadoResponseDto responseDto = AhorcadoTestMother.validAhorcadoResponseDto(ActivityTestMother.ACTIVITY_ID);

            when(generateAhorcadoService.cu39GenerateAhorcado(
                ArgumentMatchers.any(AhorcadoRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            performPost(requestDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("Ahorcado"))
                .andExpect(jsonPath("$.data.word").value(AhorcadoTestMother.DEFAULT_WORD))
                .andExpect(jsonPath("$.data.errorsPermited").value(Errors.TRES.getValue()))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad de ahorcado")));

            verify(generateAhorcadoService).cu39GenerateAhorcado(
                ArgumentMatchers.any(AhorcadoRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating ahorcado Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            AhorcadoRequestDto requestDto = AhorcadoTestMother.validAhorcadoRequestDto();

            when(generateAhorcadoService.cu39GenerateAhorcado(
                ArgumentMatchers.any(AhorcadoRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(generateAhorcadoService).cu39GenerateAhorcado(
                ArgumentMatchers.any(AhorcadoRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performPost(AhorcadoRequestDto request) throws Exception {
        return mockMvc.perform(post(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
    }
}

