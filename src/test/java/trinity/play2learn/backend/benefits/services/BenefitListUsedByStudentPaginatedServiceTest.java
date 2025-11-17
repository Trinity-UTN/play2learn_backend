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
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchasedUsedResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchasePaginatedRepository;
import trinity.play2learn.backend.benefits.specs.BenefitPurchasesSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class BenefitListUsedByStudentPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "id";
    private static final String ORDER_TYPE = "asc";
    private static final String STUDENT_EMAIL = "student@example.com";

    @Mock
    private IBenefitPurchasePaginatedRepository benefitPurchasePaginatedRepository;
    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    private BenefitListUsedByStudentPaginatedService benefitListUsedByStudentPaginatedService;

    @BeforeEach
    void setUp() {
        benefitListUsedByStudentPaginatedService = new BenefitListUsedByStudentPaginatedService(
            benefitPurchasePaginatedRepository,
            studentGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu109ListUsedPaginated")
    class ListUsedPaginated {

        @Test
        @DisplayName("Given student with used benefits When listing Then returns paginated used purchases")
        void whenStudentHasUsedBenefits_returnsPaginatedUsedPurchases() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            Benefit benefit1 = BenefitTestMother.benefit(1001L, subject);
            Benefit benefit2 = BenefitTestMother.benefit(1002L, subject);
            BenefitPurchase purchase1 = BenefitTestMother.usedBenefitPurchase(benefit1, student);
            BenefitPurchase purchase2 = BenefitTestMother.usedBenefitPurchase(benefit2, student);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<BenefitPurchase> pageResult = new PageImpl<>(List.of(purchase1, purchase2), pageable, 2);

            BenefitPurchasedUsedResponseDto dto1 = BenefitPurchasedUsedResponseDto.builder()
                .id(purchase1.getId())
                .benefitId(benefit1.getId())
                .state(BenefitPurchaseState.USED)
                .build();
            BenefitPurchasedUsedResponseDto dto2 = BenefitPurchasedUsedResponseDto.builder()
                .id(purchase2.getId())
                .benefitId(benefit2.getId())
                .state(BenefitPurchaseState.USED)
                .build();
            List<BenefitPurchasedUsedResponseDto> mappedDtos = List.of(dto1, dto2);

            PaginatedData<BenefitPurchasedUsedResponseDto> expected = PaginatedData.<BenefitPurchasedUsedResponseDto>builder()
                .results(mappedDtos)
                .count(2)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            String search = "recreo";

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);

            Specification<BenefitPurchase> notDeletedSpec = (root, query, cb) -> null;
            Specification<BenefitPurchase> benefitNameSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitPurchasesSpecs> benefitPurchasesSpecsMock = org.mockito.Mockito.mockStatic(BenefitPurchasesSpecs.class);
                MockedStatic<BenefitPurchaseMapper> benefitPurchaseMapperMock = org.mockito.Mockito.mockStatic(BenefitPurchaseMapper.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.notDeleted()).thenReturn(notDeletedSpec);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.benefitNameContains(search))
                    .thenReturn(benefitNameSpec);

                when(benefitPurchasePaginatedRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                benefitPurchaseMapperMock.when(() -> BenefitPurchaseMapper.toUsedDtoList(pageResult.getContent()))
                    .thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, mappedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitPurchasedUsedResponseDto> result = benefitListUsedByStudentPaginatedService.cu109ListUsedPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, search, null, null
                );

                // Then
                verify(studentGetByEmailService).getByEmail(STUDENT_EMAIL);
                verify(benefitPurchasePaginatedRepository).findAll(any(), eq(pageable));
                assertThat(result.getCount()).isEqualTo(2);
                assertThat(result.getResults()).hasSize(2);
                assertThat(result.getResults()).allMatch(dto -> dto.getState() == BenefitPurchaseState.USED);
            }
        }

        @Test
        @DisplayName("Given student with no used benefits When listing Then returns empty paginated data")
        void whenStudentHasNoUsedBenefits_returnsEmptyPaginatedData() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<BenefitPurchase> pageResult = new PageImpl<>(List.of(), pageable, 0);

            PaginatedData<BenefitPurchasedUsedResponseDto> expected = PaginatedData.<BenefitPurchasedUsedResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);

            Specification<BenefitPurchase> notDeletedSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitPurchasesSpecs> benefitPurchasesSpecsMock = org.mockito.Mockito.mockStatic(BenefitPurchasesSpecs.class);
                MockedStatic<BenefitPurchaseMapper> benefitPurchaseMapperMock = org.mockito.Mockito.mockStatic(BenefitPurchaseMapper.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.notDeleted()).thenReturn(notDeletedSpec);

                when(benefitPurchasePaginatedRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                benefitPurchaseMapperMock.when(() -> BenefitPurchaseMapper.toUsedDtoList(pageResult.getContent()))
                    .thenReturn(List.of());
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, List.of())).thenReturn(expected);

                // When
                PaginatedData<BenefitPurchasedUsedResponseDto> result = benefitListUsedByStudentPaginatedService.cu109ListUsedPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null
                );

                // Then
                assertThat(result.getCount()).isZero();
                assertThat(result.getResults()).isEmpty();
            }
        }
    }
}

