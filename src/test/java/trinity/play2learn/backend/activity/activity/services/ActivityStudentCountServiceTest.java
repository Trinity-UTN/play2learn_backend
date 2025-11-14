package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentCountResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterByDisapprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStatusService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityStudentCountServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private IActivityGetByStudentService activityGetByStudentService;
    @Mock
    private IActivityFilterApprovedService activityFilterApprovedService;
    @Mock
    private IActivityFilterByDisapprovedService activityFilterByDisapprovedService;
    @Mock
    private IActivityGetStatusService activityGetStatusService;

    private ActivityStudentCountService activityStudentCountService;

    @BeforeEach
    void setUp() {
        activityStudentCountService = new ActivityStudentCountService(
            studentGetByEmailService,
            activityGetByStudentService,
            activityFilterApprovedService,
            activityFilterByDisapprovedService,
            activityGetStatusService
        );
    }

    @Nested
    @DisplayName("cu88CountActivitiesPerState")
    class CountActivitiesPerState {

        @Test
        @DisplayName("Given student with activities in different states When counting Then returns correct counts")
        void whenActivitiesInDifferentStates_returnsCorrectCounts() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            
            List<Activity> allActivities = new ArrayList<>();
            Activity approved1 = ActivityTestMother.ahorcadoActivity(1L);
            Activity approved2 = ActivityTestMother.ahorcadoActivity(2L);
            Activity disapproved1 = ActivityTestMother.ahorcadoActivity(3L);
            Activity published1 = ActivityTestMother.ahorcadoActivity(4L);
            Activity published2 = ActivityTestMother.ahorcadoActivity(5L);
            Activity expired1 = ActivityTestMother.ahorcadoActivity(6L);
            
            allActivities.add(approved1);
            allActivities.add(approved2);
            allActivities.add(disapproved1);
            allActivities.add(published1);
            allActivities.add(published2);
            allActivities.add(expired1);
            
            List<Activity> approvedActivities = List.of(approved1, approved2);
            List<Activity> disapprovedActivities = List.of(disapproved1);
            List<Activity> remainingActivities = List.of(published1, published2, expired1);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(new ArrayList<>(allActivities));
            when(activityFilterApprovedService.filterByApproved(anyList(), eq(student))).thenReturn(approvedActivities);
            when(activityFilterByDisapprovedService.filterByDisapproved(anyList(), eq(student), eq(true)))
                .thenReturn(disapprovedActivities);
            when(activityGetStatusService.getStatus(published1)).thenReturn(ActivityStatus.PUBLISHED);
            when(activityGetStatusService.getStatus(published2)).thenReturn(ActivityStatus.PUBLISHED);
            when(activityGetStatusService.getStatus(expired1)).thenReturn(ActivityStatus.EXPIRED);

            ActivityStudentCountResponseDto response = activityStudentCountService.cu88CountActivitiesPerState(user);

            verify(studentGetByEmailService).getByEmail(ActivityTestMother.STUDENT_EMAIL);
            verify(activityGetByStudentService).getByStudent(student);
            verify(activityFilterApprovedService).filterByApproved(anyList(), eq(student));
            verify(activityFilterByDisapprovedService).filterByDisapproved(anyList(), eq(student), eq(true));
            
            assertThat(response)
                .isNotNull()
                .extracting(ActivityStudentCountResponseDto::getAvailable, 
                    ActivityStudentCountResponseDto::getApproved,
                    ActivityStudentCountResponseDto::getDisapproved,
                    ActivityStudentCountResponseDto::getExpired)
                .containsExactly(2, 2, 1, 1);
        }

        @Test
        @DisplayName("Given student with no activities When counting Then returns zero counts")
        void whenNoActivities_returnsZeroCounts() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            List<Activity> emptyActivities = new ArrayList<>();

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(emptyActivities);
            when(activityFilterApprovedService.filterByApproved(emptyActivities, student)).thenReturn(new ArrayList<>());
            when(activityFilterByDisapprovedService.filterByDisapproved(emptyActivities, student, true))
                .thenReturn(new ArrayList<>());

            ActivityStudentCountResponseDto response = activityStudentCountService.cu88CountActivitiesPerState(user);

            assertThat(response)
                .isNotNull()
                .extracting(ActivityStudentCountResponseDto::getAvailable, 
                    ActivityStudentCountResponseDto::getApproved,
                    ActivityStudentCountResponseDto::getDisapproved,
                    ActivityStudentCountResponseDto::getExpired)
                .containsExactly(0, 0, 0, 0);
        }
    }
}

