package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Comparator;
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

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPaginatedRepository;
import trinity.play2learn.backend.benefits.specs.BenefitSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class BenefitListByTeacherPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "name";
    private static final String ORDER_TYPE = "asc";
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;
    @Mock
    private IBenefitPaginatedRepository benefitRepository;

    private BenefitListByTeacherPaginatedService benefitListByTeacherPaginatedService;

    @BeforeEach
    void setUp() {
        benefitListByTeacherPaginatedService = new BenefitListByTeacherPaginatedService(
            teacherGetByEmailService,
            benefitRepository
        );
    }

    @Nested
    @DisplayName("cu56ListBenefitsPaginated")
    class ListBenefitsPaginated {

        @Test
        @DisplayName("Given search and filters When listing Then applies specifications and returns paginated data sorted by state")
        void whenSearchAndFiltersProvided_returnsPaginatedData() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            
            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<Benefit> pageResult = new PageImpl<>(List.of(benefit1, benefit2), pageable, 2);
            
            BenefitResponseDto dto1 = BenefitTestMother.benefitResponseBuilder(1001L).state(BenefitState.PUBLISHED).build();
            BenefitResponseDto dto2 = BenefitTestMother.benefitResponseBuilder(1002L).state(BenefitState.PUBLISHED).build();
            List<BenefitResponseDto> mappedDtos = List.of(dto1, dto2);
            List<BenefitResponseDto> sortedDtos = mappedDtos.stream()
                .sorted(Comparator.comparing(BenefitResponseDto::getState))
                .toList();
            
            PaginatedData<BenefitResponseDto> expected = PaginatedData.<BenefitResponseDto>builder()
                .results(sortedDtos)
                .count(2)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            String search = "recreo";
            List<String> filters = List.of("category");
            List<String> filterValues = List.of("EXTRAS");

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);

            Specification<Benefit> notDeletedSpec = (root, query, cb) -> null;
            Specification<Benefit> nameSpec = (root, query, cb) -> null;
            Specification<Benefit> categorySpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitSpecs> benefitSpecsMock = org.mockito.Mockito.mockStatic(BenefitSpecs.class);
                MockedStatic<BenefitMapper> benefitMapperMock = org.mockito.Mockito.mockStatic(BenefitMapper.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                
                benefitSpecsMock.when(() -> BenefitSpecs.notDeleted()).thenReturn(notDeletedSpec);
                benefitSpecsMock.when(() -> BenefitSpecs.nameContains(search)).thenReturn(nameSpec);
                benefitSpecsMock.when(() -> BenefitSpecs.genericFilter("category", "EXTRAS")).thenReturn(categorySpec);

                when(benefitRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                benefitMapperMock.when(() -> BenefitMapper.toListDto(pageResult.getContent())).thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, sortedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitResponseDto> result = benefitListByTeacherPaginatedService.cu56ListBenefitsPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, search, filters, filterValues
                );

                // Then
                @SuppressWarnings("unchecked")
                ArgumentCaptor<Specification<Benefit>> specCaptor = ArgumentCaptor.forClass((Class<Specification<Benefit>>) (Class<?>) Specification.class);
                verify(benefitRepository).findAll(specCaptor.capture(), eq(pageable));
                
                assertThat(result.getCount()).isEqualTo(expected.getCount());
                assertThat(result.getTotalPages()).isEqualTo(expected.getTotalPages());
                assertThat(result.getCurrentPage()).isEqualTo(PAGE);
                assertThat(result.getPageSize()).isEqualTo(SIZE);
                assertThat(result.getResults()).hasSize(2);
            }
        }

        @Test
        @DisplayName("Given no search or filters When listing Then returns paginated data with teacher filter only")
        void whenNoSearchNorFilters_returnsPaginatedDataWithTeacherFilter() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(1001L, subject);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<Benefit> pageResult = new PageImpl<>(List.of(benefit), pageable, 1);
            
            BenefitResponseDto dto = BenefitTestMother.benefitResponseBuilder(1001L).build();
            List<BenefitResponseDto> mappedDtos = List.of(dto);
            List<BenefitResponseDto> sortedDtos = mappedDtos.stream()
                .sorted(Comparator.comparing(BenefitResponseDto::getState))
                .toList();
            
            PaginatedData<BenefitResponseDto> expected = PaginatedData.<BenefitResponseDto>builder()
                .results(sortedDtos)
                .count(1)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);

            Specification<Benefit> notDeletedSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitSpecs> benefitSpecsMock = org.mockito.Mockito.mockStatic(BenefitSpecs.class);
                MockedStatic<BenefitMapper> benefitMapperMock = org.mockito.Mockito.mockStatic(BenefitMapper.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitSpecsMock.when(() -> BenefitSpecs.notDeleted()).thenReturn(notDeletedSpec);

                when(benefitRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                benefitMapperMock.when(() -> BenefitMapper.toListDto(pageResult.getContent())).thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, sortedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitResponseDto> result = benefitListByTeacherPaginatedService.cu56ListBenefitsPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null
                );

                // Then
                assertThat(result.getCount()).isEqualTo(1);
                assertThat(result.getResults()).hasSize(1);
            }
        }
    }
}

