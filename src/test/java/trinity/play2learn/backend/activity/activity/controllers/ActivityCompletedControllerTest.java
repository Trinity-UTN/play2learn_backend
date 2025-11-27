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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNoLudicaCompletedService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityCompletedControllerTest {

    private static final String BASE_PATH = "/activity/completed";
    private static final String NO_LUDICA_PATH = BASE_PATH + "/no-ludica";
    private static final Long ACTIVITY_ID = ActivityTestMother.ACTIVITY_ID;
    private static final Double REWARD = 50.0;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IActivityCompletedService activityCompletedService;

    @Mock
    private IActivityNoLudicaCompletedService activityNoLudicaCompletedService;

    private ActivityCompletedRequestDto requestDto;
    private ActivityCompletedResponseDto responseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ActivityCompletedController(activityCompletedService, activityNoLudicaCompletedService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        requestDto = buildApprovedRequest(ACTIVITY_ID);
        responseDto = buildApprovedResponse(ACTIVITY_ID, REWARD);

        reset(activityCompletedService, activityNoLudicaCompletedService);
    }

    private ActivityCompletedRequestDto buildApprovedRequest(Long activityId) {
        return ActivityTestMother.approvedRequest(activityId);
    }

    private ActivityCompletedResponseDto buildApprovedResponse(Long activityId, Double reward) {
        return ActivityTestMother.activityCompletedResponseDto(
            null,
            activityId,
            ActivityCompletedState.APPROVED,
            reward,
            2
        );
    }

    private ActivityCompletedResponseDto buildPendingResponse(Long activityId) {
        return ActivityTestMother.activityCompletedResponseDto(
            null,
            activityId,
            ActivityCompletedState.PENDING,
            null,
            2
        );
    }

    @Nested
    @DisplayName("POST /activity/completed")
    class CompleteActivity {

        @Test
        @DisplayName("Given valid request When completing activity Then returns ActivityCompleted with 201 Created")
        void shouldCompleteActivitySuccessfully() throws Exception {
            // Given
            when(activityCompletedService.cu61ActivityCompleted(
                ArgumentMatchers.any(ActivityCompletedRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            performPost(requestDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.activityId").value(ACTIVITY_ID))
                .andExpect(jsonPath("$.data.state").value("APPROVED"))
                .andExpect(jsonPath("$.data.reward").value(REWARD))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad completada")));

            verify(activityCompletedService).cu61ActivityCompleted(
                ArgumentMatchers.any(ActivityCompletedRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given activity already approved When completing activity Then returns 409 Conflict")
        void shouldReturnConflictWhenAlreadyApproved() throws Exception {
            // Given
            when(activityCompletedService.cu61ActivityCompleted(
                ArgumentMatchers.any(ActivityCompletedRequestDto.class),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("La actividad ya ha sido aprobada"));

            // When & Then
            performPost(requestDto)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La actividad ya ha sido aprobada"));

            verify(activityCompletedService).cu61ActivityCompleted(
                ArgumentMatchers.any(ActivityCompletedRequestDto.class),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    @Nested
    @DisplayName("POST /activity/completed/no-ludica")
    class CompleteNoLudicaActivity {

        @Test
        @DisplayName("Given valid plainText and file When completing no-ludica activity Then returns ActivityCompleted with 201 Created")
        void shouldCompleteNoLudicaActivitySuccessfully() throws Exception {
            // Given
            String plainText = "Respuesta del estudiante";
            ActivityCompletedResponseDto noLudicaResponse = buildPendingResponse(ACTIVITY_ID);

            when(activityNoLudicaCompletedService.cu72ActivityNoLudicaCompleted(
                ArgumentMatchers.eq(ACTIVITY_ID),
                ArgumentMatchers.eq(plainText),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            )).thenReturn(noLudicaResponse);

            // When & Then
            performPostNoLudica(ACTIVITY_ID, plainText, null)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.activityId").value(ACTIVITY_ID))
                .andExpect(jsonPath("$.data.state").value("PENDING"))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad completada")));

            verify(activityNoLudicaCompletedService).cu72ActivityNoLudicaCompleted(
                ArgumentMatchers.eq(ACTIVITY_ID),
                ArgumentMatchers.eq(plainText),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performPost(ActivityCompletedRequestDto request) throws Exception {
        return mockMvc.perform(post(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)));
    }

    private ResultActions performPostNoLudica(Long activityId, String plainText, Object file) throws Exception {
        return mockMvc.perform(post(NO_LUDICA_PATH)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .param("activityId", String.valueOf(activityId))
            .param("plainText", plainText != null ? plainText : ""));
    }

    private String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}

