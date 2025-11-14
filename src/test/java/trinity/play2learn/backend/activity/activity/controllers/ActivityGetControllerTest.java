package trinity.play2learn.backend.activity.activity.controllers;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;

@ExtendWith(MockitoExtension.class)
class ActivityGetControllerTest {

    private static final String BASE_PATH = "/activity";
    private static final Long ACTIVITY_ID = ActivityTestMother.ACTIVITY_ID;

    private MockMvc mockMvc;

    @Mock
    private IActivityGetService activityGetService;

    private ActivityResponseDto activityResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ActivityGetController(activityGetService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        activityResponse = buildActivityResponse(ACTIVITY_ID);

        reset(activityGetService);
    }

    private ActivityResponseDto buildActivityResponse(Long id) {
        return AhorcadoResponseDto.builder()
            .id(id)
            .name("Ahorcado")
            .description("Adivina la palabra")
            .build();
    }

    @Nested
    @DisplayName("GET /activity/{id}")
    class GetActivity {

        @Test
        @DisplayName("Given existing activity When getting by id Then returns activity with 200 OK")
        void shouldReturnActivityById() throws Exception {
            // Given
            when(activityGetService.cu64GetActivity(ACTIVITY_ID)).thenReturn(activityResponse);

            // When & Then
            performGetById(ACTIVITY_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(ACTIVITY_ID))
                .andExpect(jsonPath("$.data.name").value("Ahorcado"))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityGetService).cu64GetActivity(ACTIVITY_ID);
        }

        @Test
        @DisplayName("Given non-existent activity When getting by id Then returns 404")
        void shouldReturnNotFound() throws Exception {
            // Given
            when(activityGetService.cu64GetActivity(ACTIVITY_ID))
                .thenThrow(new NotFoundException("Actividad no encontrada"));

            // When & Then
            performGetById(ACTIVITY_ID)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Actividad no encontrada"))
                .andExpect(jsonPath("$.errors[0]").value("Actividad no encontrada"));

            verify(activityGetService).cu64GetActivity(ACTIVITY_ID);
        }
    }

    private ResultActions performGetById(Long id) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/{id}", id));
    }
}

