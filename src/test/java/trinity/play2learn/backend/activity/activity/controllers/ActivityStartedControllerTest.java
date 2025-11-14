package trinity.play2learn.backend.activity.activity.controllers;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStartService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityStartedControllerTest {

    private static final String BASE_PATH = "/activity/start";
    private static final Long ACTIVITY_ID = ActivityTestMother.ACTIVITY_ID;
    private static final Integer REMAINING_ATTEMPTS = 2;

    private MockMvc mockMvc;

    @Mock
    private IActivityStartService activityStartService;

    private ActivityCompletedResponseDto activityCompletedResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ActivityStartedController(activityStartService)
            )
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        activityCompletedResponse = buildInProgressResponse(ACTIVITY_ID, REMAINING_ATTEMPTS);

        reset(activityStartService);
    }

    private ActivityCompletedResponseDto buildInProgressResponse(Long activityId, Integer remainingAttempts) {
        return ActivityTestMother.activityCompletedResponseDto(
            null,
            activityId,
            ActivityCompletedState.IN_PROGRESS,
            null,
            remainingAttempts
        );
    }

    @Nested
    @DisplayName("POST /activity/start")
    class StartActivity {

        @Test
        @DisplayName("Given valid activity and user When starting activity Then returns ActivityCompleted with 201 Created")
        void shouldStartActivitySuccessfully() throws Exception {
            // Given
            when(activityStartService.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(ACTIVITY_ID)))
                .thenReturn(activityCompletedResponse);

            // When & Then
            performPost(ACTIVITY_ID)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.activityId").value(ACTIVITY_ID))
                .andExpect(jsonPath("$.data.state").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.remainingAttempts").value(REMAINING_ATTEMPTS))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad iniciada")));

            verify(activityStartService).execute(org.mockito.ArgumentMatchers.any(User.class), org.mockito.ArgumentMatchers.eq(ACTIVITY_ID));
        }

        @Test
        @DisplayName("Given activity already approved When starting activity Then returns 409 Conflict")
        void shouldReturnConflictWhenAlreadyApproved() throws Exception {
            // Given
            when(activityStartService.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(ACTIVITY_ID)))
                .thenThrow(new ConflictException("La actividad ya ha sido aprobada"));

            // When & Then
            performPost(ACTIVITY_ID)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La actividad ya ha sido aprobada"))
                .andExpect(jsonPath("$.errors[0]").value("La actividad ya ha sido aprobada"));

            verify(activityStartService).execute(org.mockito.ArgumentMatchers.any(User.class), org.mockito.ArgumentMatchers.eq(ACTIVITY_ID));
        }
    }

    private ResultActions performPost(Long activityId) throws Exception {
        return mockMvc.perform(post(BASE_PATH)
            .param("activityId", String.valueOf(activityId)));
    }
}

