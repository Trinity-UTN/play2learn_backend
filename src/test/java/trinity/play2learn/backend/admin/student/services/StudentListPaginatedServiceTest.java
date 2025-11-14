package trinity.play2learn.backend.admin.student.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.specs.StudentSpects;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class StudentListPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 5;
    private static final String ORDER_BY = "name";
    private static final String ORDER_TYPE = "asc";

    @Mock
    private IStudentRepository studentRepository;

    private StudentListPaginatedService studentListPaginatedService;

    @BeforeEach
    void setUp() {
        studentListPaginatedService = new StudentListPaginatedService(studentRepository);
    }

    @Nested
    @DisplayName("cu21ListPaginatedStudents")
    class ListPaginatedStudents {

        @Test
        @DisplayName("Given search and filters When listing Then applies specifications and returns paginated data")
        void whenSearchAndFiltersProvided_returnsPaginatedData() {
            String search = "ana";
            List<String> filters = List.of("courseId", "active");
            List<String> filterValues = List.of("20", "true");

            Student student = StudentTestMother.student(1L, "12345678", 20L);
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<Student> pageResult = new PageImpl<>(List.of(student), pageable, 1);

            StudentResponseDto studentDto = StudentResponseDto.builder()
                .id(student.getId())
                .name(student.getName())
                .active(true)
                .build();
            PaginatedData<StudentResponseDto> expectedPaginatedData = PaginatedData.<StudentResponseDto>builder()
                .results(List.of(studentDto))
                .count(1)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            when(studentRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);

            try (
                MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                MockedStatic<StudentSpects> studentSpectsMock = mockStatic(StudentSpects.class);
                MockedStatic<StudentMapper> studentMapperMock = mockStatic(StudentMapper.class);
                MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)
            ) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);

                Specification<Student> nameSpec = (root, query, cb) -> null;
                Specification<Student> courseSpec = (root, query, cb) -> null;
                Specification<Student> activeSpec = (root, query, cb) -> null;

                studentSpectsMock.when(() -> StudentSpects.nameContains(search)).thenReturn(nameSpec);
                studentSpectsMock.when(() -> StudentSpects.genericFilter("courseId", "20")).thenReturn(courseSpec);
                studentSpectsMock.when(() -> StudentSpects.genericFilter("active", "true")).thenReturn(activeSpec);

                studentMapperMock.when(() -> StudentMapper.toListDto(pageResult.getContent())).thenReturn(List.of(studentDto));
                paginationHelperMock.when(() -> PaginationHelper.fromPage(pageResult, List.of(studentDto))).thenReturn(expectedPaginatedData);

                PaginatedData<StudentResponseDto> result = studentListPaginatedService.cu21ListPaginatedStudents(
                    PAGE, SIZE, ORDER_BY, ORDER_TYPE, search, filters, filterValues
                );

                paginatorUtilsMock.verify(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE));
                studentSpectsMock.verify(() -> StudentSpects.nameContains(search));
                studentSpectsMock.verify(() -> StudentSpects.genericFilter("courseId", "20"));
                studentSpectsMock.verify(() -> StudentSpects.genericFilter("active", "true"));
                studentMapperMock.verify(() -> StudentMapper.toListDto(pageResult.getContent()));
                paginationHelperMock.verify(() -> PaginationHelper.fromPage(pageResult, List.of(studentDto)));

                verify(studentRepository).findAll(any(), eq(pageable));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }

        @Test
        @DisplayName("Given blank search and mismatched filters When listing Then skips specifications and returns empty results")
        void whenSearchBlankAndFiltersMismatch_skipsSpecifications() {
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<Student> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedData<StudentResponseDto> expectedPaginatedData = PaginatedData.<StudentResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            when(studentRepository.findAll(any(), eq(pageable))).thenReturn(emptyPage);

            try (
                MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                MockedStatic<StudentSpects> studentSpectsMock = mockStatic(StudentSpects.class);
                MockedStatic<StudentMapper> studentMapperMock = mockStatic(StudentMapper.class);
                MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)
            ) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                studentMapperMock.when(() -> StudentMapper.toListDto(emptyPage.getContent())).thenReturn(List.of());
                paginationHelperMock.when(() -> PaginationHelper.fromPage(emptyPage, List.of())).thenReturn(expectedPaginatedData);

                PaginatedData<StudentResponseDto> result = studentListPaginatedService.cu21ListPaginatedStudents(
                    PAGE, SIZE, ORDER_BY, ORDER_TYPE, "   ", List.of("courseId"), List.of()
                );

                paginatorUtilsMock.verify(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE));
                studentSpectsMock.verifyNoInteractions();
                studentMapperMock.verify(() -> StudentMapper.toListDto(emptyPage.getContent()));
                paginationHelperMock.verify(() -> PaginationHelper.fromPage(emptyPage, List.of()));

                verify(studentRepository).findAll(any(), eq(pageable));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }
    }
}

