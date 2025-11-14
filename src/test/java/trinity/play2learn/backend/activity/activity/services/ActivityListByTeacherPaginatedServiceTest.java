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
import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherSimpleDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateTeacherSimpleDtosService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class ActivityListByTeacherPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "id";
    private static final String ORDER_TYPE = "asc";
    private static final String SEARCH = "Ahorcado";

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;
    @Mock
    private IActivityPaginatedRepository activityRepository;
    @Mock
    private IActivityCreateTeacherSimpleDtosService activityCreateTeacherSimpleDtosService;

    private ActivityListByTeacherPaginatedService activityListByTeacherPaginatedService;

    @BeforeEach
    void setUp() {
        activityListByTeacherPaginatedService = new ActivityListByTeacherPaginatedService(
            teacherGetByEmailService,
            activityRepository,
            activityCreateTeacherSimpleDtosService
        );
    }

    @Nested
    @DisplayName("cu111ListActivityByTeacherPaginated")
    class ListActivityByTeacherPaginated {

        @Test
        @DisplayName("Given teacher with activities When listing paginated Then returns paginated data with teacher's activities")
        void whenTeacherHasActivities_returnsPaginatedData() {
            User user = User.builder()
                .id(100L)
                .email("teacher@example.com")
                .role(Role.ROLE_TEACHER)
                .build();
            Teacher teacher = Teacher.builder()
                .id(50L)
                .name("Laura")
                .lastname("Sosa")
                .dni("12345678")
                .build();
            Activity activity1 = ActivityTestMother.ahorcadoActivity(1L);
            Activity activity2 = ActivityTestMother.ahorcadoActivity(2L);
            Page<Activity> pageResult = buildPage(List.of(activity1, activity2), 0, SIZE, 2);
            Pageable pageable = pageResult.getPageable();
            
            List<ActivityTeacherSimpleDto> dtos = List.of(
                ActivityTeacherSimpleDto.builder()
                    .id(1L)
                    .name("Ahorcado")
                    .build(),
                ActivityTeacherSimpleDto.builder()
                    .id(2L)
                    .name("Ahorcado")
                    .build()
            );
            PaginatedData<ActivityTeacherSimpleDto> expected = buildPaginated(dtos, 2, 1, 1, SIZE);

            when(teacherGetByEmailService.getByEmail("teacher@example.com")).thenReturn(teacher);
            
            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                when(activityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);
                when(activityCreateTeacherSimpleDtosService.createTeacherSimpleDtos(pageResult.getContent()))
                    .thenReturn(dtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, dtos)).thenReturn(expected);

                PaginatedData<ActivityTeacherSimpleDto> result = activityListByTeacherPaginatedService
                    .cu111ListActivityByTeacherPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, SEARCH, null, null, user);

                verify(teacherGetByEmailService).getByEmail("teacher@example.com");
                paginatorMock.verify(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE));
                paginationMock.verify(() -> PaginationHelper.fromPage(pageResult, dtos));
                
                ArgumentCaptor<Specification<Activity>> specCaptor = createSpecificationCaptor();
                verify(activityRepository).findAll(specCaptor.capture(), eq(pageable));

                assertThat(result)
                    .isNotNull()
                    .extracting(PaginatedData::getCount, PaginatedData::getTotalPages,
                        PaginatedData::getCurrentPage, PaginatedData::getPageSize)
                    .containsExactly(2, 1, 1, SIZE);
                
                assertThat(result.getResults())
                    .hasSize(2)
                    .extracting(ActivityTeacherSimpleDto::getId)
                    .containsExactly(1L, 2L);
            }
        }

        @Test
        @DisplayName("Given teacher with no activities When listing paginated Then returns empty paginated data")
        void whenNoActivities_returnsEmptyPage() {
            User user = User.builder()
                .id(100L)
                .email("teacher@example.com")
                .role(Role.ROLE_TEACHER)
                .build();
            Teacher teacher = Teacher.builder()
                .id(50L)
                .name("Laura")
                .lastname("Sosa")
                .dni("12345678")
                .build();
            Page<Activity> emptyPage = buildPage(List.of(), 0, SIZE, 0);
            Pageable pageable = emptyPage.getPageable();
            PaginatedData<ActivityTeacherSimpleDto> expected = buildPaginated(List.of(), 0, 0, 1, SIZE);

            when(teacherGetByEmailService.getByEmail("teacher@example.com")).thenReturn(teacher);
            
            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                when(activityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);
                when(activityCreateTeacherSimpleDtosService.createTeacherSimpleDtos(emptyPage.getContent()))
                    .thenReturn(List.of());
                paginationMock.when(() -> PaginationHelper.fromPage(emptyPage, List.of())).thenReturn(expected);

                PaginatedData<ActivityTeacherSimpleDto> result = activityListByTeacherPaginatedService
                    .cu111ListActivityByTeacherPaginated(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

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

    private PaginatedData<ActivityTeacherSimpleDto> buildPaginated(
        List<ActivityTeacherSimpleDto> dtos,
        int count,
        int totalPages,
        int currentPage,
        int pageSize
    ) {
        return PaginatedData.<ActivityTeacherSimpleDto>builder()
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

