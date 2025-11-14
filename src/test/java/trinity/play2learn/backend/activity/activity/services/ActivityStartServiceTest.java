package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityStartServiceTest {

    private static final Integer REMAINING_ATTEMPTS = 2;
    private static final Long ACTIVITY_COMPLETED_ID = 500L;

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private IActivityGetByIdService activityFindByIdService;
    @Mock
    private IActivityValidatePublishedStatusService activityValidatePublishedStatusService;
    @Mock
    private IActivityGetCompletedStateService activityGetCompletedStateService;
    @Mock
    private IActivityGetRemainingAttemptsService activityGetRemainingAttemptsService;
    @Mock
    private IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;
    @Mock
    private IActivityCompletedRepository activityCompletedRepository;
    @Mock
    private IActivityCompletedService activityCompletedService;

    private ActivityStartService activityStartService;

    @BeforeEach
    void setUp() {
        activityStartService = new ActivityStartService(
            studentGetByEmailService,
            activityFindByIdService,
            activityValidatePublishedStatusService,
            activityGetCompletedStateService,
            activityGetRemainingAttemptsService,
            activityCompletedGetLastStartedService,
            activityCompletedRepository,
            activityCompletedService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given valid activity and student When starting activity Then creates ActivityCompleted with IN_PROGRESS state")
        void whenValidRequest_createsActivityCompleted() {
            // Given
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompleted savedCompleted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                REMAINING_ATTEMPTS
            );

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student))
                .thenReturn(REMAINING_ATTEMPTS);
            when(activityCompletedGetLastStartedService.get(activity, student)).thenReturn(Optional.empty());
            when(activityCompletedRepository.save(any(ActivityCompleted.class))).thenReturn(savedCompleted);

            // When
            ActivityCompletedResponseDto response = activityStartService.execute(user, ActivityTestMother.ACTIVITY_ID);

            // Then
            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
            verify(activityGetCompletedStateService).getActivityCompletedState(activity, student);
            verify(activityGetRemainingAttemptsService).getStudentRemainingAttempts(activity, student);
            
            ArgumentCaptor<ActivityCompleted> completedCaptor = ArgumentCaptor.forClass(ActivityCompleted.class);
            verify(activityCompletedRepository).save(completedCaptor.capture());
            ActivityCompleted savedActivityCompleted = completedCaptor.getValue();

            assertThat(savedActivityCompleted)
                .extracting(ActivityCompleted::getActivity, ActivityCompleted::getStudent, 
                    ActivityCompleted::getState, ActivityCompleted::getRemainingAttempts)
                .containsExactly(activity, student, ActivityCompletedState.IN_PROGRESS, REMAINING_ATTEMPTS);

            assertThat(response)
                .isNotNull()
                .extracting(ActivityCompletedResponseDto::getActivityId, ActivityCompletedResponseDto::getState,
                    ActivityCompletedResponseDto::getRemainingAttempts)
                .containsExactly(ActivityTestMother.ACTIVITY_ID, ActivityCompletedState.IN_PROGRESS, REMAINING_ATTEMPTS);
        }

        @Test
        @DisplayName("Given activity already approved When starting activity Then throws ConflictException")
        void whenActivityAlreadyApproved_throwsConflict() {
            // Given
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.APPROVED);

            // When & Then
            assertThatThrownBy(() -> activityStartService.execute(user, ActivityTestMother.ACTIVITY_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("La actividad ya ha sido aprobada");

            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
            verify(activityGetRemainingAttemptsService, never()).getStudentRemainingAttempts(any(), any());
            verify(activityCompletedRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given no remaining attempts When starting activity Then throws ConflictException")
        void whenNoRemainingAttempts_throwsConflict() {
            // Given
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student))
                .thenReturn(0);

            // When & Then
            assertThatThrownBy(() -> activityStartService.execute(user, ActivityTestMother.ACTIVITY_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No quedan intentos");

            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
            verify(activityCompletedRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given last started activity exists When starting new activity Then finalizes previous as DISAPPROVED")
        void whenLastStartedExists_finalizesPrevious() {
            // Given
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompleted lastStarted = ActivityTestMother.activityCompleted(
                400L,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                3
            );
            ActivityCompletedResponseDto disapprovedResponse = ActivityTestMother.activityCompletedResponseDto(
                400L,
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.DISAPPROVED,
                null,
                2
            );
            ActivityCompleted savedCompleted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                2
            );

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student))
                .thenReturn(3);
            when(activityCompletedGetLastStartedService.get(activity, student))
                .thenReturn(Optional.of(lastStarted));
            when(activityCompletedService.cu61ActivityCompleted(any(ActivityCompletedRequestDto.class), any(User.class)))
                .thenReturn(disapprovedResponse);
            when(activityCompletedRepository.save(any(ActivityCompleted.class))).thenReturn(savedCompleted);

            // When
            ActivityCompletedResponseDto response = activityStartService.execute(user, ActivityTestMother.ACTIVITY_ID);

            // Then
            verify(activityCompletedService).cu61ActivityCompleted(any(ActivityCompletedRequestDto.class), any(User.class));
            assertThat(response)
                .isNotNull()
                .extracting(ActivityCompletedResponseDto::getState, ActivityCompletedResponseDto::getRemainingAttempts)
                .containsExactly(ActivityCompletedState.IN_PROGRESS, 2);
        }

        @Test
        @DisplayName("Given last started exists and no attempts remain after disapproval When starting activity Then throws ConflictException")
        void whenNoAttemptsAfterDisapproval_throwsConflict() {
            // Given
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompleted lastStarted = ActivityTestMother.activityCompleted(
                400L,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                1
            );
            ActivityCompletedResponseDto disapprovedResponse = ActivityTestMother.activityCompletedResponseDto(
                400L,
                ActivityTestMother.ACTIVITY_ID,
                ActivityCompletedState.DISAPPROVED,
                null,
                0
            );

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student))
                .thenReturn(1);
            when(activityCompletedGetLastStartedService.get(activity, student))
                .thenReturn(Optional.of(lastStarted));
            when(activityCompletedService.cu61ActivityCompleted(any(ActivityCompletedRequestDto.class), any(User.class)))
                .thenReturn(disapprovedResponse);

            // When & Then
            assertThatThrownBy(() -> activityStartService.execute(user, ActivityTestMother.ACTIVITY_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No quedan intentos");

            verify(activityCompletedService).cu61ActivityCompleted(any(ActivityCompletedRequestDto.class), any(User.class));
            verify(activityCompletedRepository, never()).save(any());
        }
    }
}

