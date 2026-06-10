package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentStateResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.ActivityApprovedNativeRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.student.ActivityApprovedListPaginatedService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class ActivityApprovedListPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "id";
    private static final String ORDER_TYPE = "asc";

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private ActivityApprovedNativeRepository activityApprovedNativeRepository;
    @Mock
    private IActivityCompletedRepository activityCompletedRepository;

    private ActivityApprovedListPaginatedService activityApprovedListPaginatedService;

    @BeforeEach
    void setUp() {
        activityApprovedListPaginatedService = new ActivityApprovedListPaginatedService(
                studentGetByEmailService,
                activityApprovedNativeRepository,
                activityCompletedRepository);
    }

    @Nested
    @DisplayName("cu69ListApprovedActivitiesPaginated")
    class ListApprovedActivitiesPaginated {

        @Test
        @DisplayName("Given student with approved activities When listing paginated Then returns paginated data with approved activities")
        void whenApprovedActivitiesExist_returnsPaginatedData() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID,
                    ActivityTestMother.STUDENT_EMAIL);

            Activity activity1 = ActivityTestMother.ahorcadoActivity(1L);
            Activity activity2 = ActivityTestMother.ahorcadoActivity(2L);

            List<ActivityCompleted> approvedCompletions = List.of(
                    buildApprovedCompletion(10L, activity1),
                    buildApprovedCompletion(20L, activity2));

            Page<Long> completionIdPage = new PageImpl<>(List.of(10L, 20L), PageRequest.of(0, SIZE), 2);
            Pageable pageable = completionIdPage.getPageable();

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityApprovedNativeRepository.findApprovedCompletionIds(
                    eq(student), eq(pageable), eq(null), eq(null), eq(null)))
                    .thenReturn(completionIdPage);
            when(activityCompletedRepository.findAllByIdInWithActivityAndSubject(List.of(10L, 20L)))
                    .thenReturn(approvedCompletions);

            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                    MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito
                            .mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                PaginatedData<ActivityStudentStateResponseDto> expected = PaginatedData
                        .<ActivityStudentStateResponseDto>builder()
                        .results(List.of(
                                ActivityStudentStateResponseDto.builder().id(1L).name("Ahorcado").build(),
                                ActivityStudentStateResponseDto.builder().id(2L).name("Ahorcado").build()))
                        .count(2)
                        .totalPages(1)
                        .currentPage(1)
                        .pageSize(SIZE)
                        .build();

                paginationMock.when(() -> PaginationHelper.fromPage(any(Page.class), any(List.class)))
                        .thenReturn(expected);

                PaginatedData<ActivityStudentStateResponseDto> result = activityApprovedListPaginatedService
                        .cu69ListApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                verify(studentGetByEmailService).getByEmail(ActivityTestMother.STUDENT_EMAIL);
                verify(activityApprovedNativeRepository).findApprovedCompletionIds(
                        eq(student), eq(pageable), eq(null), eq(null), eq(null));

                assertThat(result)
                        .isNotNull()
                        .extracting(PaginatedData::getCount, PaginatedData::getTotalPages)
                        .containsExactly(2, 1);

                assertThat(result.getResults())
                        .hasSize(2)
                        .extracting(ActivityStudentStateResponseDto::getId)
                        .containsExactly(1L, 2L);
            }
        }

        @Test
        @DisplayName("Given student with no approved activities When listing paginated Then returns empty page")
        void whenNoApprovedActivities_returnsEmptyPage() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID,
                    ActivityTestMother.STUDENT_EMAIL);
            Pageable pageable = PageRequest.of(0, SIZE);
            Page<Long> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityApprovedNativeRepository.findApprovedCompletionIds(
                    eq(student), eq(pageable), eq(null), eq(null), eq(null)))
                    .thenReturn(emptyPage);

            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                    MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito
                            .mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                PaginatedData<ActivityStudentStateResponseDto> expected = PaginatedData
                        .<ActivityStudentStateResponseDto>builder()
                        .results(List.of())
                        .count(0)
                        .totalPages(0)
                        .currentPage(1)
                        .pageSize(SIZE)
                        .build();

                paginationMock.when(() -> PaginationHelper.fromPage(Page.empty(pageable), List.of()))
                        .thenReturn(expected);

                PaginatedData<ActivityStudentStateResponseDto> result = activityApprovedListPaginatedService
                        .cu69ListApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                assertThat(result)
                        .isNotNull()
                        .extracting(PaginatedData::getCount, PaginatedData::getTotalPages)
                        .containsExactly(0, 0);

                assertThat(result.getResults()).isEmpty();
            }
        }
    }

    private ActivityCompleted buildApprovedCompletion(Long id, Activity activity) {
        return ActivityCompleted.builder()
                .id(id)
                .activity(activity)
                .state(ActivityCompletedState.APPROVED)
                .remainingAttempts(1)
                .reward(10.0)
                .build();
    }
}
