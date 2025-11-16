package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityCompletedServiceTest {

    private static final Long ACTIVITY_COMPLETED_ID = 500L;

    @Mock
    private IActivityGetByIdService activityFindByIdService;
    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private IActivityValidatePublishedStatusService activityValidatePublishedStatusService;
    @Mock
    private IActivityGetCompletedStateService activityGetCompletedStateService;
    @Mock
    private IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;
    @Mock
    private IActivityCompletedStrategyService approvedStrategyService;
    @Mock
    private IActivityCompletedStrategyService disapprovedStrategyService;

    private ActivityCompletedService activityCompletedService;

    @BeforeEach
    void setUp() {
        Map<String, IActivityCompletedStrategyService> strategyMap = new HashMap<>();
        strategyMap.put("APPROVED", approvedStrategyService);
        strategyMap.put("DISAPPROVED", disapprovedStrategyService);
        
        activityCompletedService = new ActivityCompletedService(
            activityFindByIdService,
            strategyMap,
            studentGetByEmailService,
            activityValidatePublishedStatusService,
            activityGetCompletedStateService,
            activityCompletedGetLastStartedService
        );
    }

    @Nested
    @DisplayName("cu61ActivityCompleted")
    class CompleteActivity {

        @Test
        @DisplayName("Given valid request with APPROVED state When completing activity Then delegates to approved strategy")
        void whenApprovedState_delegatesToApprovedStrategy() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompletedRequestDto request = ActivityTestMother.activityCompletedRequestDto(
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.APPROVED
            );
            ActivityCompleted lastStarted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                2
            );
            ActivityCompletedResponseDto expectedResponse = ActivityTestMother.activityCompletedResponseDto(
                ACTIVITY_COMPLETED_ID,
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.APPROVED,
                50.0,
                2
            );

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityCompletedGetLastStartedService.getLastStartedInProgress(activity, student))
                .thenReturn(Optional.of(lastStarted));
            when(approvedStrategyService.execute(lastStarted)).thenReturn(expectedResponse);

            ActivityCompletedResponseDto response = activityCompletedService.cu61ActivityCompleted(request, user);

            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
            verify(approvedStrategyService).execute(lastStarted);
            assertThat(response)
                .isNotNull()
                .extracting(ActivityCompletedResponseDto::getState, ActivityCompletedResponseDto::getReward)
                .containsExactly(ActivityCompletedState.APPROVED, 50.0);
        }

        @Test
        @DisplayName("Given activity already approved When completing activity Then throws ConflictException")
        void whenAlreadyApproved_throwsConflict() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompletedRequestDto request = ActivityTestMother.activityCompletedRequestDto(
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.APPROVED
            );

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.APPROVED);

            assertThatThrownBy(() -> activityCompletedService.cu61ActivityCompleted(request, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("La actividad ya ha sido aprobada");

            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
            verify(activityCompletedGetLastStartedService, never()).getLastStartedInProgress(any(), any());
        }

        @Test
        @DisplayName("Given no activity in progress When completing activity Then throws ConflictException")
        void whenNoActivityInProgress_throwsConflict() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompletedRequestDto request = ActivityTestMother.activityCompletedRequestDto(
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.APPROVED
            );

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityCompletedGetLastStartedService.getLastStartedInProgress(activity, student))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityCompletedService.cu61ActivityCompleted(request, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede actualizar la actividad ya que no se encuentra en curso");

            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
        }

        @Test
        @DisplayName("Given time exceeded maxTime When completing activity Then automatically sets state to DISAPPROVED")
        void whenTimeExceeded_setsDisapproved() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            activity.setMaxTime(30);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompletedRequestDto request = ActivityTestMother.activityCompletedRequestDto(
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.APPROVED
            );
            ActivityCompleted lastStarted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                2
            );
            lastStarted.setStartedAt(LocalDateTime.now().minusMinutes(35));
            ActivityCompletedResponseDto expectedResponse = ActivityTestMother.activityCompletedResponseDto(
                ACTIVITY_COMPLETED_ID,
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.DISAPPROVED,
                null,
                1
            );

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityCompletedGetLastStartedService.getLastStartedInProgress(activity, student))
                .thenReturn(Optional.of(lastStarted));
            when(disapprovedStrategyService.execute(lastStarted)).thenReturn(expectedResponse);

            ActivityCompletedResponseDto response = activityCompletedService.cu61ActivityCompleted(request, user);

            verify(disapprovedStrategyService).execute(lastStarted);
            assertThat(response)
                .isNotNull()
                .extracting(ActivityCompletedResponseDto::getState)
                .isEqualTo(ActivityCompletedState.DISAPPROVED);
        }
    }
}

