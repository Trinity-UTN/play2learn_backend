package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.ActivityNotApprovedNativeRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.student.ActivityNotApprovedListPaginatedService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class ActivityNotApprovedListPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "id";
    private static final String ORDER_TYPE = "asc";

    @Mock
    private IActivityPaginatedRepository activityRepository;
    @Mock
    private ActivityNotApprovedNativeRepository activityNotApprovedNativeRepository;
    @Mock
    private IActivityCompletedRepository activityCompletedRepository;
    @Mock
    private IActivityCreateNotApprovedDtosService activityCreateNotApprovedDtosService;
    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private ISubjectRepository subjectRepository;

    private ActivityNotApprovedListPaginatedService activityNotApprovedListPaginatedService;

    @BeforeEach
    void setUp() {
        activityNotApprovedListPaginatedService = new ActivityNotApprovedListPaginatedService(
                activityRepository,
                activityNotApprovedNativeRepository,
                activityCompletedRepository,
                subjectRepository,
                activityCreateNotApprovedDtosService,
                studentGetByEmailService);
    }

    @Nested
    @DisplayName("cu66listNotApprovedActivitiesPaginated")
    class ListNotApprovedActivitiesPaginated {

        @Test
        @DisplayName("Given student with not approved activities When listing paginated Then returns paginated data with not approved activities")
        void whenNotApprovedActivitiesExist_returnsPaginatedData() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID,
                    ActivityTestMother.STUDENT_EMAIL);

            List<Activity> notApprovedActivities = List.of(
                    ActivityTestMother.ahorcadoActivity(2L),
                    ActivityTestMother.ahorcadoActivity(3L));

            Page<Long> idPage = new PageImpl<>(List.of(2L, 3L), PageRequest.of(0, SIZE), 2);
            Pageable pageable = idPage.getPageable();

            List<ActivityStudentNotApprovedResponseDto> dtos = List.of(
                    ActivityStudentNotApprovedResponseDto.builder()
                            .id(2L)
                            .name("Ahorcado")
                            .build(),
                    ActivityStudentNotApprovedResponseDto.builder()
                            .id(3L)
                            .name("Ahorcado")
                            .build());
            PaginatedData<ActivityStudentNotApprovedResponseDto> expected = buildPaginated(dtos, 2, 1, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityNotApprovedNativeRepository.findNotApprovedActivityIds(
                    eq(student), eq(pageable), eq(null), eq(null), eq(null)))
                    .thenReturn(idPage);
            when(activityRepository.findAllByIdInWithSubject(List.of(2L, 3L))).thenReturn(notApprovedActivities);
            when(activityCompletedRepository.findLatestByStudentAndActivityIds(eq(student), eq(List.of(2L, 3L))))
                    .thenReturn(Collections.emptyList());
            when(activityCompletedRepository.findActivityIdsByStudentAndActivityIdsAndState(
                    eq(student), eq(List.of(2L, 3L)), eq(ActivityCompletedState.IN_PROGRESS)))
                    .thenReturn(Collections.emptyList());
            when(subjectRepository.countStudentsGroupedBySubjectId(any())).thenReturn(Collections.emptyList());
            when(activityCompletedRepository.countApprovedGroupedByActivityId(eq(List.of(2L, 3L)), eq(0)))
                    .thenReturn(Collections.emptyList());

            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                    MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito
                            .mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);
                when(activityCreateNotApprovedDtosService.createNotApprovedDtos(
                        eq(notApprovedActivities), eq(student), any(Map.class), any(Set.class),
                        any(Map.class), any(Map.class)))
                        .thenReturn(dtos);
                paginationMock.when(() -> PaginationHelper.fromPage(any(Page.class), eq(dtos))).thenReturn(expected);

                PaginatedData<ActivityStudentNotApprovedResponseDto> result = activityNotApprovedListPaginatedService
                        .cu66listNotApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null,
                                user);

                verify(studentGetByEmailService).getByEmail(ActivityTestMother.STUDENT_EMAIL);
                verify(activityNotApprovedNativeRepository).findNotApprovedActivityIds(
                        eq(student), eq(pageable), eq(null), eq(null), eq(null));

                assertThat(result)
                        .isNotNull()
                        .extracting(PaginatedData::getCount, PaginatedData::getTotalPages)
                        .containsExactly(2, 1);

                assertThat(result.getResults())
                        .hasSize(2)
                        .extracting(ActivityStudentNotApprovedResponseDto::getId)
                        .containsExactly(2L, 3L);
            }
        }

        @Test
        @DisplayName("Given student with no not approved activities When listing paginated Then returns empty page")
        void whenNoNotApprovedActivities_returnsEmptyPage() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID,
                    ActivityTestMother.STUDENT_EMAIL);
            Pageable pageable = PageRequest.of(0, SIZE);
            Page<Long> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedData<ActivityStudentNotApprovedResponseDto> expected = buildPaginated(List.of(), 0, 0, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityNotApprovedNativeRepository.findNotApprovedActivityIds(
                    eq(student), eq(pageable), eq(null), eq(null), eq(null)))
                    .thenReturn(emptyPage);

            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                    MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito
                            .mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);
                paginationMock.when(() -> PaginationHelper.fromPage(Page.empty(pageable), List.of()))
                        .thenReturn(expected);

                PaginatedData<ActivityStudentNotApprovedResponseDto> result = activityNotApprovedListPaginatedService
                        .cu66listNotApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null,
                                user);

                assertThat(result)
                        .isNotNull()
                        .extracting(PaginatedData::getCount, PaginatedData::getTotalPages)
                        .containsExactly(0, 0);

                assertThat(result.getResults()).isEmpty();
            }
        }

        @Test
        @DisplayName("Given filter disapproved When listing paginated Then delegates filters to native repository")
        void whenDisapprovedFilter_appliesFilter() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID,
                    ActivityTestMother.STUDENT_EMAIL);

            List<Activity> filteredDisapproved = List.of(ActivityTestMother.ahorcadoActivity(2L));
            Page<Long> idPage = new PageImpl<>(List.of(2L), PageRequest.of(0, SIZE), 1);
            Pageable pageable = idPage.getPageable();

            List<ActivityStudentNotApprovedResponseDto> dtos = List.of(
                    ActivityStudentNotApprovedResponseDto.builder()
                            .id(2L)
                            .name("Ahorcado")
                            .build());
            PaginatedData<ActivityStudentNotApprovedResponseDto> expected = buildPaginated(dtos, 1, 1, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityNotApprovedNativeRepository.findNotApprovedActivityIds(
                    eq(student), eq(pageable), eq(null), eq(List.of("disapproved")), eq(List.of("true"))))
                    .thenReturn(idPage);
            when(activityRepository.findAllByIdInWithSubject(List.of(2L))).thenReturn(filteredDisapproved);
            when(activityCompletedRepository.findLatestByStudentAndActivityIds(eq(student), eq(List.of(2L))))
                    .thenReturn(Collections.emptyList());
            when(activityCompletedRepository.findActivityIdsByStudentAndActivityIdsAndState(
                    eq(student), eq(List.of(2L)), eq(ActivityCompletedState.IN_PROGRESS)))
                    .thenReturn(Collections.emptyList());
            when(subjectRepository.countStudentsGroupedBySubjectId(any())).thenReturn(Collections.emptyList());
            when(activityCompletedRepository.countApprovedGroupedByActivityId(eq(List.of(2L)), eq(0)))
                    .thenReturn(Collections.emptyList());

            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                    MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito
                            .mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);
                when(activityCreateNotApprovedDtosService.createNotApprovedDtos(
                        eq(filteredDisapproved), eq(student), any(Map.class), any(Set.class),
                        any(Map.class), any(Map.class)))
                        .thenReturn(dtos);
                paginationMock.when(() -> PaginationHelper.fromPage(any(Page.class), eq(dtos))).thenReturn(expected);

                PaginatedData<ActivityStudentNotApprovedResponseDto> result = activityNotApprovedListPaginatedService
                        .cu66listNotApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null,
                                List.of("disapproved"), List.of("true"), user);

                verify(activityNotApprovedNativeRepository).findNotApprovedActivityIds(
                        eq(student), eq(pageable), eq(null), eq(List.of("disapproved")), eq(List.of("true")));
                assertThat(result)
                        .isNotNull()
                        .extracting(PaginatedData::getCount)
                        .isEqualTo(1);
            }
        }
    }

    private PaginatedData<ActivityStudentNotApprovedResponseDto> buildPaginated(
            List<ActivityStudentNotApprovedResponseDto> dtos,
            int count,
            int totalPages,
            int currentPage,
            int pageSize) {
        return PaginatedData.<ActivityStudentNotApprovedResponseDto>builder()
                .results(dtos)
                .count(count)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
    }
}
