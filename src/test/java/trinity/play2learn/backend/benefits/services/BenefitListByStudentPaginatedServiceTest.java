package trinity.play2learn.backend.benefits.services;

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
import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPaginatedRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitGetByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitCreateStudentDtosService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterByStudentStateService;
import trinity.play2learn.backend.benefits.specs.BenefitSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class BenefitListByStudentPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "name";
    private static final String ORDER_TYPE = "asc";
    private static final String STUDENT_EMAIL = "student@example.com";

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private BenefitGetByStudentService benefitGetByStudentService;
    @Mock
    private IBenefitFilterByStudentStateService benefitFilterByStudentStateService;
    @Mock
    private IBenefitPaginatedRepository benefitRepository;
    @Mock
    private IBenefitCreateStudentDtosService benefitCreateStudentDtos;

    private BenefitListByStudentPaginatedService benefitListByStudentPaginatedService;

    @BeforeEach
    void setUp() {
        benefitListByStudentPaginatedService = new BenefitListByStudentPaginatedService(
            studentGetByEmailService,
            benefitGetByStudentService,
            benefitFilterByStudentStateService,
            benefitRepository,
            benefitCreateStudentDtos
        );
    }

    @Nested
    @DisplayName("cu80ListBenefitsByStudentPaginated")
    class ListBenefitsByStudentPaginated {

        @Test
        @DisplayName("Given student with benefits When listing Then returns paginated student benefit DTOs")
        void whenStudentHasBenefits_returnsPaginatedStudentBenefitDtos() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            List<Benefit> allBenefits = List.of(benefit1, benefit2);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<Benefit> pageResult = new PageImpl<>(List.of(benefit1, benefit2), pageable, 2);

            BenefitStudentResponseDto dto1 = BenefitStudentResponseDto.builder()
                .id(benefit1.getId())
                .name(benefit1.getName())
                .state(BenefitStudentState.AVAILABLE)
                .build();
            BenefitStudentResponseDto dto2 = BenefitStudentResponseDto.builder()
                .id(benefit2.getId())
                .name(benefit2.getName())
                .state(BenefitStudentState.AVAILABLE)
                .build();
            List<BenefitStudentResponseDto> mappedDtos = List.of(dto1, dto2);

            PaginatedData<BenefitStudentResponseDto> expected = PaginatedData.<BenefitStudentResponseDto>builder()
                .results(mappedDtos)
                .count(2)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByStudentService.getByStudent(student)).thenReturn(allBenefits);

            Specification<Benefit> notDeletedSpec = (root, query, cb) -> null;
            Specification<Benefit> nameSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitSpecs> benefitSpecsMock = org.mockito.Mockito.mockStatic(BenefitSpecs.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitSpecsMock.when(() -> BenefitSpecs.notDeleted()).thenReturn(notDeletedSpec);
                benefitSpecsMock.when(() -> BenefitSpecs.nameContains("recreo")).thenReturn(nameSpec);

                when(benefitRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                when(benefitCreateStudentDtos.createBenefitStudentDtos(pageResult.getContent(), student)).thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, mappedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitStudentResponseDto> result = benefitListByStudentPaginatedService.cu80ListBenefitsByStudentPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, "recreo", null, null
                );

                // Then
                verify(studentGetByEmailService).getByEmail(STUDENT_EMAIL);
                verify(benefitGetByStudentService).getByStudent(student);
                verify(benefitRepository).findAll(any(), eq(pageable));
                verify(benefitCreateStudentDtos).createBenefitStudentDtos(pageResult.getContent(), student);
                
                assertThat(result.getCount()).isEqualTo(2);
                assertThat(result.getResults()).hasSize(2);
                assertThat(result.getResults()).extracting(BenefitStudentResponseDto::getId).containsExactly(1001L, 1002L);
            }
        }

        @Test
        @DisplayName("Given student with state filter When listing Then filters by student state and returns paginated data")
        void whenStateFilterProvided_filtersByStudentStateAndReturnsPaginatedData() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            List<Benefit> allBenefits = List.of(benefit1, benefit2);
            List<Benefit> filteredBenefits = List.of(benefit1); // Solo los disponibles

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<Benefit> pageResult = new PageImpl<>(List.of(benefit1), pageable, 1);

            BenefitStudentResponseDto dto1 = BenefitStudentResponseDto.builder()
                .id(benefit1.getId())
                .state(BenefitStudentState.AVAILABLE)
                .build();
            List<BenefitStudentResponseDto> mappedDtos = List.of(dto1);

            PaginatedData<BenefitStudentResponseDto> expected = PaginatedData.<BenefitStudentResponseDto>builder()
                .results(mappedDtos)
                .count(1)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            List<String> filters = List.of("state");
            List<String> filterValues = List.of("AVAILABLE");

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByStudentService.getByStudent(student)).thenReturn(allBenefits);
            when(benefitFilterByStudentStateService.filterByStudentState(allBenefits, student, BenefitStudentState.AVAILABLE))
                .thenReturn(filteredBenefits);

            Specification<Benefit> notDeletedSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitSpecs> benefitSpecsMock = org.mockito.Mockito.mockStatic(BenefitSpecs.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitSpecsMock.when(() -> BenefitSpecs.notDeleted()).thenReturn(notDeletedSpec);

                when(benefitRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                when(benefitCreateStudentDtos.createBenefitStudentDtos(pageResult.getContent(), student)).thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, mappedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitStudentResponseDto> result = benefitListByStudentPaginatedService.cu80ListBenefitsByStudentPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, filters, filterValues
                );

                // Then
                verify(benefitFilterByStudentStateService).filterByStudentState(allBenefits, student, BenefitStudentState.AVAILABLE);
                assertThat(result.getCount()).isEqualTo(1);
                assertThat(result.getResults()).hasSize(1);
                assertThat(result.getResults().get(0).getState()).isEqualTo(BenefitStudentState.AVAILABLE);
            }
        }

        @Test
        @DisplayName("Given student with no benefits When listing Then returns empty paginated data")
        void whenStudentHasNoBenefits_returnsEmptyPaginatedData() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByStudentService.getByStudent(student)).thenReturn(List.of());

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                
                PaginatedData<BenefitStudentResponseDto> expected = PaginatedData.<BenefitStudentResponseDto>builder()
                    .results(List.of())
                    .count(0)
                    .totalPages(0)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();
                paginationMock.when(() -> PaginationHelper.fromPage(Page.empty(pageable), List.of())).thenReturn(expected);

                // When
                PaginatedData<BenefitStudentResponseDto> result = benefitListByStudentPaginatedService.cu80ListBenefitsByStudentPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null
                );

                // Then
                verify(benefitGetByStudentService).getByStudent(student);
                assertThat(result.getCount()).isZero();
                assertThat(result.getResults()).isEmpty();
            }
        }

        @Test
        @DisplayName("Given invalid state filter value When listing Then ignores filter and returns all benefits")
        void whenInvalidStateFilterValue_ignoresFilterAndReturnsAllBenefits() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            List<Benefit> allBenefits = List.of(benefit1, benefit2);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<Benefit> pageResult = new PageImpl<>(List.of(benefit1, benefit2), pageable, 2);

            BenefitStudentResponseDto dto1 = BenefitStudentResponseDto.builder()
                .id(benefit1.getId())
                .state(BenefitStudentState.AVAILABLE)
                .build();
            BenefitStudentResponseDto dto2 = BenefitStudentResponseDto.builder()
                .id(benefit2.getId())
                .state(BenefitStudentState.AVAILABLE)
                .build();
            List<BenefitStudentResponseDto> mappedDtos = List.of(dto1, dto2);

            PaginatedData<BenefitStudentResponseDto> expected = PaginatedData.<BenefitStudentResponseDto>builder()
                .results(mappedDtos)
                .count(2)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            List<String> filters = List.of("state");
            List<String> filterValues = List.of("INVALID_STATE");

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByStudentService.getByStudent(student)).thenReturn(allBenefits);

            Specification<Benefit> notDeletedSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitSpecs> benefitSpecsMock = org.mockito.Mockito.mockStatic(BenefitSpecs.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitSpecsMock.when(() -> BenefitSpecs.notDeleted()).thenReturn(notDeletedSpec);

                when(benefitRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                when(benefitCreateStudentDtos.createBenefitStudentDtos(pageResult.getContent(), student)).thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, mappedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitStudentResponseDto> result = benefitListByStudentPaginatedService.cu80ListBenefitsByStudentPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, filters, filterValues
                );

                // Then
                // Verifica que NO se llama al filtro de estado si el valor es inválido
                verify(benefitRepository).findAll(any(), eq(pageable));
                assertThat(result.getCount()).isEqualTo(2);
            }
        }
    }
}

