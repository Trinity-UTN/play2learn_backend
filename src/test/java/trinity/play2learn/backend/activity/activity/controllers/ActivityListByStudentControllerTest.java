package trinity.play2learn.backend.activity.activity.controllers;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListApproveByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListNotApprovedByStudentService;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityListByStudentControllerTest {

    private static final String BASE_PATH = "/activity/student";
    private static final String NOT_APPROVED_PATH = BASE_PATH + "/not-approved";
    private static final String APPROVED_PATH = BASE_PATH + "/approved";

    private MockMvc mockMvc;

    @Mock
    private IActivityListNotApprovedByStudentService activityListNotApprovedByStudentService;

    @Mock
    private IActivityListApproveByStudentService activityListApproveByStudentService;

    private List<ActivityStudentNotApprovedResponseDto> notApprovedResponses;
    private List<ActivityStudentApprovedResponseDto> approvedResponses;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ActivityListByStudentController(
                    activityListNotApprovedByStudentService,
                    activityListApproveByStudentService
                )
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        notApprovedResponses = buildNotApprovedResponses();
        approvedResponses = buildApprovedResponses();

        reset(activityListNotApprovedByStudentService, activityListApproveByStudentService);
    }

    private List<ActivityStudentNotApprovedResponseDto> buildNotApprovedResponses() {
        return List.of(
            ActivityStudentNotApprovedResponseDto.builder()
                .id(1L)
                .name("Ahorcado")
                .remainingAttempts(2)
                .build(),
            ActivityStudentNotApprovedResponseDto.builder()
                .id(2L)
                .name("Preguntados")
                .remainingAttempts(1)
                .build()
        );
    }

    private List<ActivityStudentApprovedResponseDto> buildApprovedResponses() {
        return List.of(
            ActivityStudentApprovedResponseDto.builder()
                .id(3L)
                .name("Ahorcado")
                .reward(50.0)
                .build()
        );
    }

    @Nested
    @DisplayName("GET /activity/student/not-approved")
    class ListNotApproved {

        @Test
        @DisplayName("Given student with not approved activities When listing not approved Then returns list with 200 OK")
        void shouldReturnNotApprovedActivities() throws Exception {
            // Given
            when(activityListNotApprovedByStudentService.cu62ListNotApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(notApprovedResponses);

            // When & Then
            performGetNotApproved()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("Ahorcado"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityListNotApprovedByStudentService).cu62ListNotApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given student with no not approved activities When listing not approved Then returns empty list with 200 OK")
        void shouldReturnEmptyNotApprovedList() throws Exception {
            // Given
            when(activityListNotApprovedByStudentService.cu62ListNotApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(new ArrayList<>());

            // When & Then
            performGetNotApproved()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityListNotApprovedByStudentService).cu62ListNotApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }
    }

    @Nested
    @DisplayName("GET /activity/student/approved")
    class ListApproved {

        @Test
        @DisplayName("Given student with approved activities When listing approved Then returns list with 200 OK")
        void shouldReturnApprovedActivities() throws Exception {
            // Given
            when(activityListApproveByStudentService.cu63ListApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(approvedResponses);

            // When & Then
            performGetApproved()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(3L))
                .andExpect(jsonPath("$.data[0].name").value("Ahorcado"))
                .andExpect(jsonPath("$.data[0].reward").value(50.0))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityListApproveByStudentService).cu63ListApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given student with no approved activities When listing approved Then returns empty list with 200 OK")
        void shouldReturnEmptyApprovedList() throws Exception {
            // Given
            when(activityListApproveByStudentService.cu63ListApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            )).thenReturn(new ArrayList<>());

            // When & Then
            performGetApproved()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.okSuccessfully()));

            verify(activityListApproveByStudentService).cu63ListApprovedActivitiesByStudent(
                org.mockito.ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performGetNotApproved() throws Exception {
        return mockMvc.perform(get(NOT_APPROVED_PATH));
    }

    private ResultActions performGetApproved() throws Exception {
        return mockMvc.perform(get(APPROVED_PATH));
    }
}

