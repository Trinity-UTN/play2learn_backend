package trinity.play2learn.backend.activity.arbolDeDecision.controllers;

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
import trinity.play2learn.backend.activity.arbolDeDecision.ArbolDeDecisionTestMother;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.services.interfaces.IArbolDecisionGenerateService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ArbolDeDecisionGenerateControllerTest {

    private static final String BASE_PATH = "/activities/arbol-decision";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IArbolDecisionGenerateService generateArbolDeDecisionService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ArbolDeDecisionGenerateController(generateArbolDeDecisionService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        reset(generateArbolDeDecisionService);
    }

    @Nested
    @DisplayName("POST /activities/arbol-decision")
    class GenerateArbolDeDecision {

        @Test
        @DisplayName("Given valid request with 2 decisions When generating arbol de decision Then returns ArbolDeDecisionActivityResponseDto with 201 Created")
        void shouldGenerateArbolDeDecisionSuccessfully() throws Exception {
            // Given
            ArbolDeDecisionActivityRequestDto requestDto = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();
            ArbolDeDecisionActivityResponseDto responseDto = ArbolDeDecisionTestMother.validArbolDeDecisionResponseDto(
                ActivityTestMother.ACTIVITY_ID
            );

            when(generateArbolDeDecisionService.cu46GenerateArbolDeDecisionActivity(
                ArgumentMatchers.any(ArbolDeDecisionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            performPost(requestDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("Arbol de decision"))
                .andExpect(jsonPath("$.data.introduction").value(ArbolDeDecisionTestMother.DEFAULT_INTRODUCTION))
                .andExpect(jsonPath("$.data.decisionTree").isArray())
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad arbol de decision")));

            verify(generateArbolDeDecisionService).cu46GenerateArbolDeDecisionActivity(
                ArgumentMatchers.any(ArbolDeDecisionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When generating arbol de decision Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            ArbolDeDecisionActivityRequestDto requestDto = ArbolDeDecisionTestMother.validArbolDeDecisionRequestDto();

            when(generateArbolDeDecisionService.cu46GenerateArbolDeDecisionActivity(
                ArgumentMatchers.any(ArbolDeDecisionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(generateArbolDeDecisionService).cu46GenerateArbolDeDecisionActivity(
                ArgumentMatchers.any(ArbolDeDecisionActivityRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performPost(ArbolDeDecisionActivityRequestDto request) throws Exception {
        return mockMvc.perform(post(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
    }
}

