package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterByDisapprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterNotApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
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
    private IActivityCreateNotApprovedDtosService activityCreateNotApprovedDtosService;
    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private IActivityGetByStudentService activityGetByStudentService;
    @Mock
    private IActivityFilterNotApprovedService activityFilterNotApprovedService;
    @Mock
    private IActivityFilterByDisapprovedService activityFilterByDisapprovedService;

    private ActivityNotApprovedListPaginatedService activityNotApprovedListPaginatedService;

    @BeforeEach
    void setUp() {
        activityNotApprovedListPaginatedService = new ActivityNotApprovedListPaginatedService(
            activityRepository,
            activityCreateNotApprovedDtosService,
            studentGetByEmailService,
            activityGetByStudentService,
            activityFilterNotApprovedService,
            activityFilterByDisapprovedService
        );
    }

    @Nested
    @DisplayName("cu66listNotApprovedActivitiesPaginated")
    class ListNotApprovedActivitiesPaginated {

        @Test
        @DisplayName("Given student with not approved activities When listing paginated Then returns paginated data with not approved activities")
        void whenNotApprovedActivitiesExist_returnsPaginatedData() {
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
            
            Page<Activity> pageResult = buildPage(notApprovedActivities, 0, SIZE, 2);
            Pageable pageable = pageResult.getPageable();
            
            List<ActivityStudentNotApprovedResponseDto> dtos = List.of(
                ActivityStudentNotApprovedResponseDto.builder()
                    .id(2L)
                    .name("Ahorcado")
                    .build(),
                ActivityStudentNotApprovedResponseDto.builder()
                    .id(3L)
                    .name("Ahorcado")
                    .build()
            );
            PaginatedData<ActivityStudentNotApprovedResponseDto> expected = buildPaginated(dtos, 2, 1, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(allActivities);
            when(activityFilterNotApprovedService.filterByNotApproved(allActivities, student))
                .thenReturn(notApprovedActivities);
            
            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                when(activityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);
                when(activityCreateNotApprovedDtosService.createNotApprovedDtos(pageResult.getContent(), student))
                    .thenReturn(dtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, dtos)).thenReturn(expected);

                PaginatedData<ActivityStudentNotApprovedResponseDto> result = activityNotApprovedListPaginatedService
                    .cu66listNotApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                verify(studentGetByEmailService).getByEmail(ActivityTestMother.STUDENT_EMAIL);
                verify(activityGetByStudentService).getByStudent(student);
                verify(activityFilterNotApprovedService).filterByNotApproved(allActivities, student);
                
                ArgumentCaptor<Specification<Activity>> specCaptor = createSpecificationCaptor();
                verify(activityRepository).findAll(specCaptor.capture(), eq(pageable));

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
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            List<Activity> allActivities = List.of(ActivityTestMother.ahorcadoActivity(1L));
            List<Activity> emptyNotApproved = new ArrayList<>();
            Pageable pageable = PageRequest.of(0, SIZE);
            PaginatedData<ActivityStudentNotApprovedResponseDto> expected = buildPaginated(List.of(), 0, 0, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(allActivities);
            when(activityFilterNotApprovedService.filterByNotApproved(allActivities, student))
                .thenReturn(emptyNotApproved);
            
            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                paginationMock.when(() -> PaginationHelper.fromPage(Page.empty(pageable), List.of()))
                    .thenReturn(expected);

                PaginatedData<ActivityStudentNotApprovedResponseDto> result = activityNotApprovedListPaginatedService
                    .cu66listNotApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                verify(activityRepository, org.mockito.Mockito.never()).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
                assertThat(result)
                    .isNotNull()
                    .extracting(PaginatedData::getCount, PaginatedData::getTotalPages)
                    .containsExactly(0, 0);
                
                assertThat(result.getResults()).isEmpty();
            }
        }

        @Test
        @DisplayName("Given filter disapproved When listing paginated Then applies disapproved filter")
        void whenDisapprovedFilter_appliesFilter() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            
            List<Activity> allActivities = List.of(
                ActivityTestMother.ahorcadoActivity(1L),
                ActivityTestMother.ahorcadoActivity(2L)
            );
            
            List<Activity> notApprovedActivities = new ArrayList<>(allActivities);
            List<Activity> filteredDisapproved = List.of(ActivityTestMother.ahorcadoActivity(2L));
            
            Page<Activity> pageResult = buildPage(filteredDisapproved, 0, SIZE, 1);
            Pageable pageable = pageResult.getPageable();
            
            List<ActivityStudentNotApprovedResponseDto> dtos = List.of(
                ActivityStudentNotApprovedResponseDto.builder()
                    .id(2L)
                    .name("Ahorcado")
                    .build()
            );
            PaginatedData<ActivityStudentNotApprovedResponseDto> expected = buildPaginated(dtos, 1, 1, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(allActivities);
            when(activityFilterNotApprovedService.filterByNotApproved(allActivities, student))
                .thenReturn(notApprovedActivities);
            when(activityFilterByDisapprovedService.filterByDisapproved(notApprovedActivities, student, true))
                .thenReturn(filteredDisapproved);
            
            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                when(activityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);
                when(activityCreateNotApprovedDtosService.createNotApprovedDtos(pageResult.getContent(), student))
                    .thenReturn(dtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, dtos)).thenReturn(expected);

                PaginatedData<ActivityStudentNotApprovedResponseDto> result = activityNotApprovedListPaginatedService
                    .cu66listNotApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null,
                        List.of("disapproved"), List.of("true"), user);

                verify(activityFilterByDisapprovedService).filterByDisapproved(notApprovedActivities, student, true);
                assertThat(result)
                    .isNotNull()
                    .extracting(PaginatedData::getCount)
                    .isEqualTo(1);
            }
        }
    }

    private Page<Activity> buildPage(List<Activity> content, int pageNumber, int pageSize, long totalElements) {
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }

    private PaginatedData<ActivityStudentNotApprovedResponseDto> buildPaginated(
        List<ActivityStudentNotApprovedResponseDto> dtos,
        int count,
        int totalPages,
        int currentPage,
        int pageSize
    ) {
        return PaginatedData.<ActivityStudentNotApprovedResponseDto>builder()
            .results(dtos)
            .count(count)
            .totalPages(totalPages)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .build();
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<Specification<Activity>> createSpecificationCaptor() {
        return ArgumentCaptor.forClass((Class<Specification<Activity>>) (Class<?>) Specification.class);
    }
}

