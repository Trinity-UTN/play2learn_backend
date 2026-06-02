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

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentCountResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStudentCountService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityCountByStudentStateControllerTest {

    private static final String BASE_PATH = "/activity/student/count";

    private MockMvc mockMvc;

    @Mock
    private IActivityStudentCountService activityStudentCountService;

    private ActivityStudentCountResponseDto countResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ActivityCountByStudentStateController(activityStudentCountService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        countResponse = buildCountResponse(5, 3, 2, 1);

        reset(activityStudentCountService);
    }

    private ActivityStudentCountResponseDto buildCountResponse(int available, int approved, int disapproved, int expired) {
        return ActivityStudentCountResponseDto.builder()
            .available(available)
            .approved(approved)
            .disapproved(disapproved)
            .expired(expired)
            .build();
    }

    @Nested
    @DisplayName("GET /activity/student/count")
    class CountByState {

        @Test
        @DisplayName("Given student with activities When counting by state Then returns counts with 200 OK")
        void shouldReturnCountsByState() throws Exception {
            // Given
            when(activityStudentCountService.cu88CountActivitiesPerState(
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(countResponse);

            // When & Then
            performGet()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(5))
                .andExpect(jsonPath("$.data.approved").value(3))
                .andExpect(jsonPath("$.data.disapproved").value(2))
                .andExpect(jsonPath("$.data.expired").value(1))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityStudentCountService).cu88CountActivitiesPerState(
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given student with no activities When counting by state Then returns zero counts with 200 OK")
        void shouldReturnZeroCounts() throws Exception {
            // Given
            ActivityStudentCountResponseDto emptyCount = buildCountResponse(0, 0, 0, 0);

            when(activityStudentCountService.cu88CountActivitiesPerState(
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(emptyCount);

            // When & Then
            performGet()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(0))
                .andExpect(jsonPath("$.data.approved").value(0))
                .andExpect(jsonPath("$.data.disapproved").value(0))
                .andExpect(jsonPath("$.data.expired").value(0));

            verify(activityStudentCountService).cu88CountActivitiesPerState(
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performGet() throws Exception {
        return mockMvc.perform(get(BASE_PATH));
    }
}

