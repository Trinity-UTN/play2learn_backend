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
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class ActivityListApprovedByStudentServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private IActivityGetByStudentService activityGetByStudentService;
    @Mock
    private IActivityCreateApprovedDtosService activityCreateApprovedDtosService;

    private ActivityListApprovedByStudentService activityListApprovedByStudentService;

    @BeforeEach
    void setUp() {
        activityListApprovedByStudentService = new ActivityListApprovedByStudentService(
            studentGetByEmailService,
            activityGetByStudentService,
            activityCreateApprovedDtosService
        );
    }

    @Nested
    @DisplayName("cu63ListApprovedActivitiesByStudent")
    class ListApprovedActivities {

        @Test
        @DisplayName("Given student with approved activities When listing approved activities Then returns approved DTOs")
        void whenApprovedActivitiesExist_returnsApprovedDtos() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            
            List<Activity> activities = List.of(
                ActivityTestMother.ahorcadoActivity(1L),
                ActivityTestMother.ahorcadoActivity(2L)
            );
            
            List<ActivityStudentApprovedResponseDto> expectedDtos = List.of(
                ActivityStudentApprovedResponseDto.builder()
                    .id(1L)
                    .name("Ahorcado")
                    .build(),
                ActivityStudentApprovedResponseDto.builder()
                    .id(2L)
                    .name("Ahorcado")
                    .build()
            );

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(activities);
            when(activityCreateApprovedDtosService.createApprovedDtos(activities, student)).thenReturn(expectedDtos);

            List<ActivityStudentApprovedResponseDto> response = 
                activityListApprovedByStudentService.cu63ListApprovedActivitiesByStudent(user);

            verify(studentGetByEmailService).getByEmail(ActivityTestMother.STUDENT_EMAIL);
            verify(activityGetByStudentService).getByStudent(student);
            verify(activityCreateApprovedDtosService).createApprovedDtos(activities, student);
            
            assertThat(response)
                .isNotNull()
                .hasSize(2)
                .extracting(ActivityStudentApprovedResponseDto::getId)
                .containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("Given student with no activities When listing approved activities Then returns empty list")
        void whenNoActivities_returnsEmptyList() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            List<Activity> emptyActivities = new ArrayList<>();

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(emptyActivities);
            when(activityCreateApprovedDtosService.createApprovedDtos(emptyActivities, student))
                .thenReturn(new ArrayList<>());

            List<ActivityStudentApprovedResponseDto> response = 
                activityListApprovedByStudentService.cu63ListApprovedActivitiesByStudent(user);

            assertThat(response).isNotNull().isEmpty();
        }
    }
}

