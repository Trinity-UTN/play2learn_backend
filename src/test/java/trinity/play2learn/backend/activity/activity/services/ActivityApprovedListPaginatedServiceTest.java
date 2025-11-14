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
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
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
    private IActivityGetByStudentService activityGetByStudentService;
    @Mock
    private IActivityFilterApprovedService activityFilterApprovedService;
    @Mock
    private IActivityPaginatedRepository activityPaginatedRepository;
    @Mock
    private IActivityCreateApprovedDtosService activityCreateApprovedDtosService;

    private ActivityApprovedListPaginatedService activityApprovedListPaginatedService;

    @BeforeEach
    void setUp() {
        activityApprovedListPaginatedService = new ActivityApprovedListPaginatedService(
            studentGetByEmailService,
            activityGetByStudentService,
            activityFilterApprovedService,
            activityPaginatedRepository,
            activityCreateApprovedDtosService
        );
    }

    @Nested
    @DisplayName("cu69ListApprovedActivitiesPaginated")
    class ListApprovedActivitiesPaginated {

        @Test
        @DisplayName("Given student with approved activities When listing paginated Then returns paginated data with approved activities")
        void whenApprovedActivitiesExist_returnsPaginatedData() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            
            List<Activity> allActivities = List.of(
                ActivityTestMother.ahorcadoActivity(1L),
                ActivityTestMother.ahorcadoActivity(2L),
                ActivityTestMother.ahorcadoActivity(3L)
            );
            
            List<Activity> approvedActivities = List.of(
                ActivityTestMother.ahorcadoActivity(1L),
                ActivityTestMother.ahorcadoActivity(2L)
            );
            
            Page<Activity> pageResult = buildPage(approvedActivities, 0, SIZE, 2);
            Pageable pageable = pageResult.getPageable();
            
            List<ActivityStudentApprovedResponseDto> dtos = List.of(
                ActivityStudentApprovedResponseDto.builder()
                    .id(1L)
                    .name("Ahorcado")
                    .build(),
                ActivityStudentApprovedResponseDto.builder()
                    .id(2L)
                    .name("Ahorcado")
                    .build()
            );
            PaginatedData<ActivityStudentApprovedResponseDto> expected = buildPaginated(dtos, 2, 1, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(allActivities);
            when(activityFilterApprovedService.filterByApproved(allActivities, student)).thenReturn(approvedActivities);
            
            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                when(activityPaginatedRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);
                when(activityCreateApprovedDtosService.createApprovedDtos(pageResult.getContent(), student))
                    .thenReturn(dtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, dtos)).thenReturn(expected);

                PaginatedData<ActivityStudentApprovedResponseDto> result = activityApprovedListPaginatedService
                    .cu69ListApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                verify(studentGetByEmailService).getByEmail(ActivityTestMother.STUDENT_EMAIL);
                verify(activityGetByStudentService).getByStudent(student);
                verify(activityFilterApprovedService).filterByApproved(allActivities, student);
                
                ArgumentCaptor<Specification<Activity>> specCaptor = createSpecificationCaptor();
                verify(activityPaginatedRepository).findAll(specCaptor.capture(), eq(pageable));

                assertThat(result)
                    .isNotNull()
                    .extracting(PaginatedData::getCount, PaginatedData::getTotalPages)
                    .containsExactly(2, 1);
                
                assertThat(result.getResults())
                    .hasSize(2)
                    .extracting(ActivityStudentApprovedResponseDto::getId)
                    .containsExactly(1L, 2L);
            }
        }

        @Test
        @DisplayName("Given student with no approved activities When listing paginated Then returns empty page")
        void whenNoApprovedActivities_returnsEmptyPage() {
            User user = ActivityTestMother.studentUser(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            Student student = ActivityTestMother.student(ActivityTestMother.STUDENT_ID, ActivityTestMother.STUDENT_EMAIL);
            List<Activity> allActivities = List.of(ActivityTestMother.ahorcadoActivity(1L));
            List<Activity> emptyApproved = new ArrayList<>();
            Pageable pageable = PageRequest.of(0, SIZE);
            PaginatedData<ActivityStudentApprovedResponseDto> expected = buildPaginated(List.of(), 0, 0, 1, SIZE);

            when(studentGetByEmailService.getByEmail(ActivityTestMother.STUDENT_EMAIL)).thenReturn(student);
            when(activityGetByStudentService.getByStudent(student)).thenReturn(allActivities);
            when(activityFilterApprovedService.filterByApproved(allActivities, student)).thenReturn(emptyApproved);
            
            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                paginationMock.when(() -> PaginationHelper.fromPage(Page.empty(pageable), List.of()))
                    .thenReturn(expected);

                PaginatedData<ActivityStudentApprovedResponseDto> result = activityApprovedListPaginatedService
                    .cu69ListApprovedActivitiesPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                verify(activityPaginatedRepository, org.mockito.Mockito.never()).findAll(any(Specification.class), any(Pageable.class));
                assertThat(result)
                    .isNotNull()
                    .extracting(PaginatedData::getCount, PaginatedData::getTotalPages)
                    .containsExactly(0, 0);
                
                assertThat(result.getResults()).isEmpty();
            }
        }
    }

    private Page<Activity> buildPage(List<Activity> content, int pageNumber, int pageSize, long totalElements) {
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }

    private PaginatedData<ActivityStudentApprovedResponseDto> buildPaginated(
        List<ActivityStudentApprovedResponseDto> dtos,
        int count,
        int totalPages,
        int currentPage,
        int pageSize
    ) {
        return PaginatedData.<ActivityStudentApprovedResponseDto>builder()
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

