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
import org.springframework.web.multipart.MultipartFile;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaCreateAttemptService;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaValidationsService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityNoLudicaCompletedServiceTest {

    private static final Long ACTIVITY_COMPLETED_ID = 500L;
    private static final Long NO_LUDICA_ATTEMPT_ID = 600L;
    private static final String PLAIN_TEXT = "Respuesta del estudiante";

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
    private INoLudicaCreateAttemptService noLudicaCreateAttemptService;
    @Mock
    private IActivityCompletedRepository activityCompletedRepository;
    @Mock
    private INoLudicaValidationsService noLudicaValidationsService;
    @Mock
    private IActivityCompletedService activityCompletedService;
    @Mock
    private MultipartFile file;

    private ActivityNoLudicaCompletedService activityNoLudicaCompletedService;

    @BeforeEach
    void setUp() {
        activityNoLudicaCompletedService = new ActivityNoLudicaCompletedService(
            activityFindByIdService,
            studentGetByEmailService,
            activityValidatePublishedStatusService,
            activityGetCompletedStateService,
            activityCompletedGetLastStartedService,
            noLudicaCreateAttemptService,
            activityCompletedRepository,
            noLudicaValidationsService,
            activityCompletedService
        );
    }

    @Nested
    @DisplayName("cu72ActivityNoLudicaCompleted")
    class NoLudicaCompleted {

        @Test
        @DisplayName("Given valid plainText When completing noLudica activity Then sets state to PENDING and creates attempt")
        void whenValidPlainText_setsPendingAndCreatesAttempt() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompleted lastStarted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                2
            );
            NoLudicaAttempt attempt = NoLudicaAttempt.builder()
                .id(NO_LUDICA_ATTEMPT_ID)
                .plainText(PLAIN_TEXT)
                .file(null)
                .build();
            ActivityCompleted savedCompleted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.PENDING,
                2
            );
            savedCompleted.setNoLudicaAttempt(attempt);

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityCompletedGetLastStartedService.get(activity, student))
                .thenReturn(Optional.of(lastStarted));
            when(noLudicaCreateAttemptService.createAttempt(PLAIN_TEXT, null)).thenReturn(attempt);
            when(activityCompletedRepository.save(any(ActivityCompleted.class))).thenReturn(savedCompleted);

            ActivityCompletedResponseDto response = activityNoLudicaCompletedService.cu72ActivityNoLudicaCompleted(
                ActivityTestMother.ACTIVITY_ID, PLAIN_TEXT, null, user);

            verify(noLudicaValidationsService).validateNoLudicaCompleted(PLAIN_TEXT, null);
            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
            verify(noLudicaCreateAttemptService).createAttempt(PLAIN_TEXT, null);
            
            ArgumentCaptor<ActivityCompleted> completedCaptor = ArgumentCaptor.forClass(ActivityCompleted.class);
            verify(activityCompletedRepository).save(completedCaptor.capture());
            ActivityCompleted savedActivityCompleted = completedCaptor.getValue();

            assertThat(savedActivityCompleted)
                .extracting(ActivityCompleted::getState, ActivityCompleted::getNoLudicaAttempt)
                .containsExactly(ActivityCompletedState.PENDING, attempt);

            assertThat(response)
                .isNotNull()
                .extracting(ActivityCompletedResponseDto::getActivityId, ActivityCompletedResponseDto::getState)
                .containsExactly(ActivityTestMother.ACTIVITY_ID, ActivityCompletedState.PENDING);
        }

        @Test
        @DisplayName("Given null plainText When completing noLudica activity Then sets to empty string")
        void whenNullPlainText_setsToEmptyString() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            ActivityCompleted lastStarted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.IN_PROGRESS,
                2
            );
            NoLudicaAttempt attempt = NoLudicaAttempt.builder()
                .id(NO_LUDICA_ATTEMPT_ID)
                .plainText("")
                .file(null)
                .build();
            ActivityCompleted savedCompleted = ActivityTestMother.activityCompleted(
                ACTIVITY_COMPLETED_ID,
                activity,
                student,
                ActivityCompletedState.PENDING,
                2
            );
            savedCompleted.setNoLudicaAttempt(attempt);

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityCompletedGetLastStartedService.get(activity, student))
                .thenReturn(Optional.of(lastStarted));
            when(noLudicaCreateAttemptService.createAttempt("", null)).thenReturn(attempt);
            when(activityCompletedRepository.save(any(ActivityCompleted.class))).thenReturn(savedCompleted);

            ActivityCompletedResponseDto response = activityNoLudicaCompletedService.cu72ActivityNoLudicaCompleted(
                ActivityTestMother.ACTIVITY_ID, null, null, user);

            verify(noLudicaValidationsService).validateNoLudicaCompleted("", null);
            verify(noLudicaCreateAttemptService).createAttempt("", null);
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("Given activity already approved When completing noLudica activity Then throws ConflictException")
        void whenAlreadyApproved_throwsConflict() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.APPROVED);

            assertThatThrownBy(() -> activityNoLudicaCompletedService.cu72ActivityNoLudicaCompleted(
                ActivityTestMother.ACTIVITY_ID, PLAIN_TEXT, null, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("La actividad ya ha sido aprobada");

            verify(noLudicaValidationsService).validateNoLudicaCompleted(PLAIN_TEXT, null);
            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
            verify(activityCompletedRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given activity pending review When completing noLudica activity Then throws ConflictException")
        void whenPendingReview_throwsConflict() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.PENDING);

            assertThatThrownBy(() -> activityNoLudicaCompletedService.cu72ActivityNoLudicaCompleted(
                ActivityTestMother.ACTIVITY_ID, PLAIN_TEXT, null, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("La actividad se encuentra pendiente de revision");

            verify(noLudicaValidationsService).validateNoLudicaCompleted(PLAIN_TEXT, null);
            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
        }

        @Test
        @DisplayName("Given no activity in progress When completing noLudica activity Then throws ConflictException")
        void whenNoActivityInProgress_throwsConflict() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);

            when(activityFindByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetCompletedStateService.getActivityCompletedState(activity, student))
                .thenReturn(ActivityCompletedState.DISAPPROVED);
            when(activityCompletedGetLastStartedService.get(activity, student))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityNoLudicaCompletedService.cu72ActivityNoLudicaCompleted(
                ActivityTestMother.ACTIVITY_ID, PLAIN_TEXT, null, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede realizar la actividad ya que no se encuentra en curso");

            verify(activityValidatePublishedStatusService).validatePublishedStatus(activity);
        }
    }
}

