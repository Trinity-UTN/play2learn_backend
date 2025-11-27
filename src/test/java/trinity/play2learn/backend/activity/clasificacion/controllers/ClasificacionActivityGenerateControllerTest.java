package trinity.play2learn.backend.activity.clasificacion.controllers;

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
import trinity.play2learn.backend.activity.clasificacion.ClasificacionTestMother;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionGenerateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ClasificacionActivityGenerateControllerTest {

    private static final String BASE_PATH = "/activities/clasificacion";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IClasificacionGenerateService generateClasificacionService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ClasificacionActivityGenerateController(generateClasificacionService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        reset(generateClasificacionService);
    }

    @Nested
    @DisplayName("POST /activities/clasificacion")
    class GenerateClasificacion {

        @Test
        @DisplayName("Given valid request with 2 categories and concepts When generating clasificacion Then returns ClasificacionActivityResponseDto with 201 Created")
        void shouldGenerateClasificacionSuccessfully() throws Exception {
            // Given
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();
            ClasificacionActivityResponseDto responseDto = ClasificacionTestMother.validClasificacionResponseDto(
                ActivityTestMother.ACTIVITY_ID
            );

            when(generateClasificacionService.cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            performPost(requestDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("Clasificacion"))
                .andExpect(jsonPath("$.data.categories").isArray())
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad de clasificación")));

            verify(generateClasificacionService).cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating clasificacion Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();

            when(generateClasificacionService.cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(generateClasificacionService).cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given duplicate category names When generating clasificacion Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenDuplicateCategoryNames() throws Exception {
            // Given
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();

            when(generateClasificacionService.cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new BadRequestException("The categories names must be unique."));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The categories names must be unique."))
                .andExpect(jsonPath("$.errors[0]").value("The categories names must be unique."));

            verify(generateClasificacionService).cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given duplicate concept names When generating clasificacion Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenDuplicateConceptNames() throws Exception {
            // Given
            ClasificacionActivityRequestDto requestDto = ClasificacionTestMother.validClasificacionRequestDto();

            when(generateClasificacionService.cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new BadRequestException("Los nombres de los conceptos deben ser unicos."));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Los nombres de los conceptos deben ser unicos."))
                .andExpect(jsonPath("$.errors[0]").value("Los nombres de los conceptos deben ser unicos."));

            verify(generateClasificacionService).cu43GenerateClasificacionActivity(
                ArgumentMatchers.any(ClasificacionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performPost(ClasificacionActivityRequestDto request) throws Exception {
        return mockMvc.perform(post(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
    }
}

