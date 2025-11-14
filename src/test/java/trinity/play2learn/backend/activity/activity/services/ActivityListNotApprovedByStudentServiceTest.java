package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
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
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterNotApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityListNotApprovedByStudentServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private IActivityGetByStudentService activityGetByStudentService;
    @Mock
    private IActivityCreateNotApprovedDtosService activityCreateNotApprovedDtosService;
    @Mock
    private IActivityFilterNotApprovedService activityFilterNotApprovedService;

    private ActivityListNotApprovedByStudentService activityListNotApprovedByStudentService;

    @BeforeEach
    void setUp() {
        activityListNotApprovedByStudentService = new ActivityListNotApprovedByStudentService(
            studentGetByEmailService,
            activityGetByStudentService,
            activityCreateNotApprovedDtosService,
            activityFilterNotApprovedService
        );
    }

    @Nested
    @DisplayName("cu62ListNotApprovedActivitiesByStudent")
    class ListNotApprovedActivities {

        @Test
        @DisplayName("Given student with not approved activities When listing not approved activities Then returns not approved DTOs")
        void whenNotApprovedActivitiesExist_returnsNotApprovedDtos() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            
            List<Activity> allActivities = List.of(
                ActivityTestMother.ahorcadoActivity(1L),
                ActivityTestMother.ahorcadoActivity(2L),
                ActivityTestMother.ahorcadoActivity(3L)
            );
            
            List<Activity> notApprovedActivities = List.of(
                ActivityTestMother.ahorcadoActivity(2L),
                ActivityTestMother.ahorcadoActivity(3L)
            );
            
            List<ActivityStudentNotApprovedResponseDto> expectedDtos = List.of(
                ActivityStudentNotApprovedResponseDto.builder()
                    .id(2L)
                    .name("Ahorcado")
                    .build(),
                ActivityStudentNotApprovedResponseDto.builder()
                    .id(3L)
                    .name("Ahorcado")
                    .build()
            );

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(allActivities);
            when(activityFilterNotApprovedService.filterByNotApproved(allActivities, student))
                .thenReturn(notApprovedActivities);
            when(activityCreateNotApprovedDtosService.createNotApprovedDtos(notApprovedActivities, student))
                .thenReturn(expectedDtos);

            List<ActivityStudentNotApprovedResponseDto> response = 
                activityListNotApprovedByStudentService.cu62ListNotApprovedActivitiesByStudent(user);

            verify(studentGetByEmailService).getByEmail(ActivityTestMother.STUDENT_EMAIL);
            verify(activityGetByStudentService).getByStudent(student);
            verify(activityFilterNotApprovedService).filterByNotApproved(allActivities, student);
            verify(activityCreateNotApprovedDtosService).createNotApprovedDtos(notApprovedActivities, student);
            
            assertThat(response)
                .isNotNull()
                .hasSize(2)
                .extracting(ActivityStudentNotApprovedResponseDto::getId)
                .containsExactly(2L, 3L);
        }

        @Test
        @DisplayName("Given student with no not approved activities When listing not approved activities Then returns empty list")
        void whenNoNotApprovedActivities_returnsEmptyList() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            List<Activity> allActivities = List.of(ActivityTestMother.ahorcadoActivity(1L));
            List<Activity> emptyNotApproved = new ArrayList<>();

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(allActivities);
            when(activityFilterNotApprovedService.filterByNotApproved(allActivities, student))
                .thenReturn(emptyNotApproved);
            when(activityCreateNotApprovedDtosService.createNotApprovedDtos(emptyNotApproved, student))
                .thenReturn(new ArrayList<>());

            List<ActivityStudentNotApprovedResponseDto> response = 
                activityListNotApprovedByStudentService.cu62ListNotApprovedActivitiesByStudent(user);

            assertThat(response).isNotNull().isEmpty();
        }
    }
}

