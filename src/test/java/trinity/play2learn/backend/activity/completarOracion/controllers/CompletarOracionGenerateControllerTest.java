package trinity.play2learn.backend.activity.completarOracion.controllers;

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
import trinity.play2learn.backend.activity.completarOracion.CompletarOracionTestMother;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionGenerateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class CompletarOracionGenerateControllerTest {

    private static final String BASE_PATH = "/activities/completar-oracion";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ICompletarOracionGenerateService generateCompletarOracionService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(
                new CompletarOracionGenerateController(generateCompletarOracionService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        reset(generateCompletarOracionService);
    }

    @Nested
    @DisplayName("POST /activities/completar-oracion")
    class GenerateCompletarOracion {

        @Test
        @DisplayName("Given valid request with sentences and words When generating completar oracion Then returns CompletarOracionActivityResponseDto with 201 Created")
        void shouldGenerateCompletarOracionSuccessfully() throws Exception {
            // Given
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();
            CompletarOracionActivityResponseDto responseDto = CompletarOracionTestMother.validCompletarOracionResponseDto(
                ActivityTestMother.ACTIVITY_ID
            );

            when(generateCompletarOracionService.cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            performPost(requestDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("Completar oracion"))
                .andExpect(jsonPath("$.data.sentences").isArray())
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad de completar oración")));

            verify(generateCompletarOracionService).cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating completar oracion Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();

            when(generateCompletarOracionService.cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(generateCompletarOracionService).cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given invalid words order When generating completar oracion Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenInvalidWordsOrder() throws Exception {
            // Given
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();

            when(generateCompletarOracionService.cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new BadRequestException("Los ordenes de las palabras no pueden repetirse."));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Los ordenes de las palabras no pueden repetirse."))
                .andExpect(jsonPath("$.errors[0]").value("Los ordenes de las palabras no pueden repetirse."));

            verify(generateCompletarOracionService).cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given no words missing (oración sin espacios en blanco - edge case) When generating completar oracion Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenNoWordsMissing() throws Exception {
            // Given
            CompletarOracionActivityRequestDto requestDto = CompletarOracionTestMother.validCompletarOracionRequestDto();

            when(generateCompletarOracionService.cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new BadRequestException("Cada oracion debe tener al menos una palabra faltante."));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cada oracion debe tener al menos una palabra faltante."))
                .andExpect(jsonPath("$.errors[0]").value("Cada oracion debe tener al menos una palabra faltante."));

            verify(generateCompletarOracionService).cu42generateCompletarOracionActivity(
                ArgumentMatchers.any(CompletarOracionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performPost(CompletarOracionActivityRequestDto request) throws Exception {
        return mockMvc.perform(post(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
    }
}

