package trinity.play2learn.backend.admin.course.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.specs.CourseSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseListPaginatedService")
class CourseListPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "id";
    private static final String ORDER_TYPE = "asc";

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseListPaginatedService courseListPaginatedService;

    @Nested
    @DisplayName("cu16ListPaginatedCourses")
    class Cu16ListPaginatedCourses {

        @Test
        @DisplayName("Retorna datos paginados sin búsqueda ni filtros adicionales")
        void shouldReturnPaginatedCoursesWithoutExtraFilters() {
            Pageable pageable = Pageable.ofSize(SIZE).withPage(PAGE - 1);
            List<Course> courses = List.of(
                buildCourse(1L, "Matemática"),
                buildCourse(2L, "Lengua")
            );
            Page<Course> page = new PageImpl<>(courses, pageable, courses.size());
            List<CourseResponseDto> dtoList = List.of(
                buildCourseDto(1L, "Matemática"),
                buildCourseDto(2L, "Lengua")
            );
            PaginatedData<CourseResponseDto> expected = buildPaginatedData(
                dtoList,
                PAGE,
                SIZE,
                1,
                courses.size()
            );

            Specification<Course> notDeletedSpec = Specification.where((root, query, cb) -> cb.equal(cb.literal(1), 1));

            try (var mockedPaginator = Mockito.mockStatic(PaginatorUtils.class);
                 var mockedPaginationHelper = Mockito.mockStatic(PaginationHelper.class);
                 var mockedCourseSpecs = Mockito.mockStatic(CourseSpecs.class)) {

                mockedPaginator.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                    .thenReturn(pageable);
                mockedCourseSpecs.when(CourseSpecs::notDeleted).thenReturn(notDeletedSpec);
                mockedPaginationHelper.when(() -> PaginationHelper.fromPage(page, dtoList))
                    .thenReturn(expected);

                when(courseRepository.findAll(Mockito.<Specification<Course>>any(), any(Pageable.class)))
                    .thenReturn(page);

                PaginatedData<CourseResponseDto> result = courseListPaginatedService.cu16ListPaginatedCourses(
                    PAGE,
                    SIZE,
                    ORDER_BY,
                    ORDER_TYPE,
                    null,
                    null,
                    null
                );

                assertThat(result).isEqualTo(expected);
                mockedCourseSpecs.verify(CourseSpecs::notDeleted);
                mockedCourseSpecs.verifyNoMoreInteractions();
                verify(courseRepository).findAll(Mockito.<Specification<Course>>any(), any(Pageable.class));
            }
        }

        @Test
        @DisplayName("Aplica búsqueda textual y filtros dinámicos cuando están presentes")
        void shouldApplySearchAndDynamicFilters() {
            Pageable pageable = Pageable.ofSize(5).withPage(0);
            Page<Course> page = Page.empty(pageable);
            List<CourseResponseDto> dtoList = List.of();
            PaginatedData<CourseResponseDto> expected = buildPaginatedData(
                dtoList,
                1,
                5,
                0,
                0
            );

            Specification<Course> notDeletedSpec = Specification.where((root, query, cb) -> cb.conjunction());
            Specification<Course> nameContainsSpec = Specification.where((root, query, cb) -> cb.conjunction());
            Specification<Course> filterSpec = Specification.where((root, query, cb) -> cb.conjunction());

            try (var mockedPaginator = Mockito.mockStatic(PaginatorUtils.class);
                 var mockedPaginationHelper = Mockito.mockStatic(PaginationHelper.class);
                 var mockedCourseSpecs = Mockito.mockStatic(CourseSpecs.class)) {

                mockedPaginator.when(() -> PaginatorUtils.buildPageable(1, 5, "name", "desc"))
                    .thenReturn(pageable);
                mockedCourseSpecs.when(CourseSpecs::notDeleted).thenReturn(notDeletedSpec);
                mockedCourseSpecs.when(() -> CourseSpecs.nameContains("mat"))
                    .thenReturn(nameContainsSpec);
                mockedCourseSpecs.when(() -> CourseSpecs.genericFilter("year.id", "2025"))
                    .thenReturn(filterSpec);
                mockedPaginationHelper.when(() -> PaginationHelper.fromPage(page, dtoList))
                    .thenReturn(expected);

                when(courseRepository.findAll(Mockito.<Specification<Course>>any(), any(Pageable.class)))
                    .thenReturn(page);

                PaginatedData<CourseResponseDto> result = courseListPaginatedService.cu16ListPaginatedCourses(
                    1,
                    5,
                    "name",
                    "desc",
                    "mat",
                    List.of("year.id"),
                    List.of("2025")
                );

                assertThat(result).isEqualTo(expected);
                mockedCourseSpecs.verify(CourseSpecs::notDeleted);
                mockedCourseSpecs.verify(() -> CourseSpecs.nameContains("mat"));
                mockedCourseSpecs.verify(() -> CourseSpecs.genericFilter("year.id", "2025"));

                verify(courseRepository).findAll(Mockito.<Specification<Course>>any(), any(Pageable.class));
            }
        }

        @Test
        @DisplayName("Ignora filtros cuando la lista de valores no coincide")
        void shouldIgnoreFiltersWhenMismatched() {
            Pageable pageable = Pageable.ofSize(3).withPage(0);
            Page<Course> page = Page.empty(pageable);
            List<CourseResponseDto> dtoList = List.of();
            PaginatedData<CourseResponseDto> expected = buildPaginatedData(
                dtoList,
                1,
                3,
                0,
                0
            );

            Specification<Course> notDeletedSpec = Specification.where((root, query, cb) -> cb.conjunction());

            try (var mockedPaginator = Mockito.mockStatic(PaginatorUtils.class);
                 var mockedPaginationHelper = Mockito.mockStatic(PaginationHelper.class);
                 var mockedCourseSpecs = Mockito.mockStatic(CourseSpecs.class)) {

                mockedPaginator.when(() -> PaginatorUtils.buildPageable(1, 3, "id", "asc"))
                    .thenReturn(pageable);
                mockedCourseSpecs.when(CourseSpecs::notDeleted).thenReturn(notDeletedSpec);
                mockedPaginationHelper.when(() -> PaginationHelper.fromPage(page, dtoList))
                    .thenReturn(expected);

                when(courseRepository.findAll(Mockito.<Specification<Course>>any(), any(Pageable.class)))
                    .thenReturn(page);

                PaginatedData<CourseResponseDto> result = courseListPaginatedService.cu16ListPaginatedCourses(
                    1,
                    3,
                    "id",
                    "asc",
                    null,
                    List.of("name"),
                    List.of()
                );

                assertThat(result).isEqualTo(expected);
                mockedCourseSpecs.verify(CourseSpecs::notDeleted);
                mockedCourseSpecs.verifyNoMoreInteractions();
            }
        }
    }

    private Course buildCourse(Long id, String name) {
        return Course.builder()
            .id(id)
            .name(name)
            .year(trinity.play2learn.backend.admin.year.models.Year.builder()
                .id(100L + id)
                .name("2025")
                .build())
            .build();
    }

    private CourseResponseDto buildCourseDto(Long id, String name) {
        return CourseResponseDto.builder()
            .id(id)
            .name(name)
            .year(trinity.play2learn.backend.admin.year.dtos.YearResponseDto.builder()
                .id(100L + id)
                .name("2025")
                .build())
            .build();
    }

    private PaginatedData<CourseResponseDto> buildPaginatedData(
        List<CourseResponseDto> data,
        int currentPage,
        int pageSize,
        int totalPages,
        int count
    ) {
        return PaginatedData.<CourseResponseDto>builder()
            .results(data)
            .count(count)
            .totalPages(totalPages)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .build();
    }
}

