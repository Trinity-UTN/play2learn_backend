package trinity.play2learn.backend.activity.ordenarSecuencia.controllers;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.ordenarSecuencia.OrdenarSecuenciaTestMother;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IOrdenarSecuenciaActivityGenerateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class OrdenarSecuenciaGenerateControllerTest {

    private static final String BASE_PATH = "/activities/ordenar-secuencia";

    private MockMvc mockMvc;

    @Mock
    private IOrdenarSecuenciaActivityGenerateService ordenarSecuenciaGenerateService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new OrdenarSecuenciaGenerateController(ordenarSecuenciaGenerateService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        reset(ordenarSecuenciaGenerateService);
    }

    @Nested
    @DisplayName("POST /activities/ordenar-secuencia")
    class GenerateOrdenarSecuencia {

        @Test
        @DisplayName("Given valid request with 3 events and images When generating ordenar secuencia Then returns OrdenarSecuenciaResponseDto with 201 Created")
        void shouldGenerateOrdenarSecuenciaSuccessfully() throws Exception {
            // Given
            OrdenarSecuenciaResponseDto responseDto = OrdenarSecuenciaTestMother.validOrdenarSecuenciaResponseDto(
                ActivityTestMother.ACTIVITY_ID
            );

            when(ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            performPost()
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("Ordenar Secuencia"))
                .andExpect(jsonPath("$.data.events").isArray())
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad de ordenar secuencia")));

            verify(ordenarSecuenciaGenerateService).cu44GenerateOrdenarSecuencia(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating ordenar secuencia Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            when(ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            performPost()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(ordenarSecuenciaGenerateService).cu44GenerateOrdenarSecuencia(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given invalid events order (eventos con orden inválido - edge case) When generating ordenar secuencia Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenInvalidEventsOrder() throws Exception {
            // Given
            when(ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new BadRequestException("Los ordenes de los eventos no son consecutivos"));

            // When & Then
            performPost()
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Los ordenes de los eventos no son consecutivos"))
                .andExpect(jsonPath("$.errors[0]").value("Los ordenes de los eventos no son consecutivos"));

            verify(ordenarSecuenciaGenerateService).cu44GenerateOrdenarSecuencia(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    private org.springframework.test.web.servlet.ResultActions performPost() throws Exception {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        // Create events with orders 0, 1, 2, 3
        java.util.List<trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto> events = java.util.List.of(
            trinity.play2learn.backend.activity.ordenarSecuencia.OrdenarSecuenciaTestMother.event("Evento 0", "Descripción 0", 0),
            trinity.play2learn.backend.activity.ordenarSecuencia.OrdenarSecuenciaTestMother.event("Evento 1", "Descripción 1", 1),
            trinity.play2learn.backend.activity.ordenarSecuencia.OrdenarSecuenciaTestMother.event("Evento 2", "Descripción 2", 2),
            trinity.play2learn.backend.activity.ordenarSecuencia.OrdenarSecuenciaTestMother.event("Evento 3", "Descripción 3", 3)
        );
        trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto requestDto = 
            trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto.builder()
                .events(events)
                .build();
        requestDto.setDescription("Descripción de la actividad de ordenar secuencia");
        requestDto.setStartDate(now.plusDays(1));
        requestDto.setEndDate(now.plusDays(7));
        requestDto.setDifficulty(trinity.play2learn.backend.activity.activity.models.activity.Difficulty.FACIL);
        requestDto.setMaxTime(30);
        requestDto.setAttempts(3);
        requestDto.setSubjectId(ActivityTestMother.SUBJECT_ID);
        requestDto.setInitialBalance(100.0);
        String payloadJson = objectMapper.writeValueAsString(requestDto);

        org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder requestBuilder = multipart(BASE_PATH);
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("payload", payloadJson);
        // MockMvc doesn't allow multiple files with same parameter name, so we use unique parameter names
        // The mapper uses MultipartFile.getName() (originalFilename) as key, so we set originalFilename to match event orders
        // Parameter names don't matter since controller collects all from MultiValueMap
        requestBuilder = requestBuilder.file(createMockImage("files0", "0"));
        requestBuilder = requestBuilder.file(createMockImage("files1", "1"));
        requestBuilder = requestBuilder.file(createMockImage("files2", "2"));
        requestBuilder = requestBuilder.file(createMockImage("files3", "3"));
        return mockMvc.perform(requestBuilder);
    }

    private MockMultipartFile createMockImage(String paramName, String originalFilename) {
        return new MockMultipartFile(
            paramName,
            originalFilename,
            MediaType.IMAGE_JPEG_VALUE,
            ("test image content " + originalFilename).getBytes()
        );
    }

}

