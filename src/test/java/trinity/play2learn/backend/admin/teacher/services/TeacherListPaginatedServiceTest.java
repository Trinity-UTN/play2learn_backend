package trinity.play2learn.backend.admin.teacher.services;

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

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherPaginatedRepository;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class TeacherListPaginatedServiceTest {

    private static final Teacher ACTIVE_TEACHER = TeacherTestMother.teacher(11L, "12345678");
    private static final Teacher ANOTHER_TEACHER = TeacherTestMother.teacher(12L, "87654321");
    private static final String ORDER_ASC = "asc";
    private static final String ORDER_DESC = "desc";

    @Mock
    private ITeacherPaginatedRepository teacherRepository;

    private TeacherListPaginatedService teacherListPaginatedService;

    @BeforeEach
    void setUp() {
        teacherListPaginatedService = new TeacherListPaginatedService(teacherRepository);
    }

    @Nested
    @DisplayName("cu26ListTeachersPaginated")
    class ListTeachersPaginated {

        @Test
        @DisplayName("Given search and filters When listing Then returns paginated data with mapped results")
        void whenSearchAndFiltersProvided_returnsPaginatedData() {
            Page<Teacher> pageResult = buildPage(List.of(ACTIVE_TEACHER, ANOTHER_TEACHER), 0, 10, 2);
            List<TeacherResponseDto> mappedDtos = mapTeachers(pageResult.getContent());
            PaginatedData<TeacherResponseDto> expected = buildPaginated(mappedDtos, 2, 1, 1, 10);

            ResultContext context = executePaginatedRequest(
                1,
                10,
                "name",
                ORDER_ASC,
                "Laura",
                List.of("dni"),
                List.of("12345678"),
                pageResult,
                mappedDtos,
                expected
            );

            assertPaginatedData(context.result(), expected);
            assertThat(context.specification()).isNotNull();
        }

        @Test
        @DisplayName("Given no search or filters When listing Then returns empty paginated data")
        void whenNoSearchNorFilters_returnsEmptyPage() {
            Page<Teacher> pageResult = buildPage(List.of(), 1, 5, 0);
            List<TeacherResponseDto> mappedDtos = mapTeachers(pageResult.getContent());
            PaginatedData<TeacherResponseDto> expected = buildPaginated(mappedDtos, 0, 0, 2, 5);

            ResultContext context = executePaginatedRequest(
                2,
                5,
                "id",
                ORDER_DESC,
                null,
                null,
                null,
                pageResult,
                mappedDtos,
                expected
            );

            assertPaginatedData(context.result(), expected);
            assertThat(context.specification()).isNotNull();
        }
    }

    private Page<Teacher> buildPage(List<Teacher> content, int pageNumber, int pageSize, long totalElements) {
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }

    private List<TeacherResponseDto> mapTeachers(List<Teacher> teachers) {
        return TeacherMapper.toListDto(teachers);
    }

    private PaginatedData<TeacherResponseDto> buildPaginated(
        List<TeacherResponseDto> dtos,
        int count,
        int totalPages,
        int currentPage,
        int pageSize
    ) {
        return PaginatedData.<TeacherResponseDto>builder()
            .results(dtos)
            .count(count)
            .totalPages(totalPages)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .build();
    }

    private ResultContext executePaginatedRequest(
        int page,
        int size,
        String orderBy,
        String orderType,
        String search,
        List<String> filters,
        List<String> filterValues,
        Page<Teacher> pageResult,
        List<TeacherResponseDto> mappedDtos,
        PaginatedData<TeacherResponseDto> expected
    ) {
        Pageable pageable = pageResult.getPageable();

        try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
             MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {

            paginatorMock.when(() -> PaginatorUtils.buildPageable(page, size, orderBy, orderType)).thenReturn(pageable);
            when(teacherRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
            paginationMock.when(() -> PaginationHelper.fromPage(pageResult, mappedDtos)).thenReturn(expected);

            PaginatedData<TeacherResponseDto> result = teacherListPaginatedService.cu26ListTeachersPaginated(
                page,
                size,
                orderBy,
                orderType,
                search,
                filters,
                filterValues
            );

            paginatorMock.verify(() -> PaginatorUtils.buildPageable(page, size, orderBy, orderType));
            paginationMock.verify(() -> PaginationHelper.fromPage(pageResult, mappedDtos));

            ArgumentCaptor<Specification<Teacher>> specCaptor = createSpecificationCaptor();
            verify(teacherRepository).findAll(specCaptor.capture(), eq(pageable));

            return new ResultContext(result, specCaptor.getValue());
        }
    }

    private void assertPaginatedData(
        PaginatedData<TeacherResponseDto> actual,
        PaginatedData<TeacherResponseDto> expected
    ) {
        assertThat(actual.getCount()).isEqualTo(expected.getCount());
        assertThat(actual.getTotalPages()).isEqualTo(expected.getTotalPages());
        assertThat(actual.getCurrentPage()).isEqualTo(expected.getCurrentPage());
        assertThat(actual.getPageSize()).isEqualTo(expected.getPageSize());
        assertThat(actual.getResults())
            .extracting(TeacherResponseDto::getId, TeacherResponseDto::getDni)
            .isEqualTo(
                expected.getResults()
                    .stream()
                    .map(dto -> org.assertj.core.groups.Tuple.tuple(dto.getId(), dto.getDni()))
                    .toList()
            );
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<Specification<Teacher>> createSpecificationCaptor() {
        return ArgumentCaptor.forClass((Class<Specification<Teacher>>) (Class<?>) Specification.class);
    }

    private static class ResultContext {
        private final PaginatedData<TeacherResponseDto> result;
        private final Specification<Teacher> specification;

        private ResultContext(
            PaginatedData<TeacherResponseDto> result,
            Specification<Teacher> specification
        ) {
            this.result = result;
            this.specification = specification;
        }

        PaginatedData<TeacherResponseDto> result() {
            return result;
        }

        Specification<Teacher> specification() {
            return specification;
        }
    }
}

