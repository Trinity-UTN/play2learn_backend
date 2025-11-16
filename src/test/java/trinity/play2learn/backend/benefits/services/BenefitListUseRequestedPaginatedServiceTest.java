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
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
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
class BenefitListUseRequestedPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "id";
    private static final String ORDER_TYPE = "asc";
    private static final String TEACHER_EMAIL = "teacher@example.com";

    @Mock
    private IBenefitPurchasePaginatedRepository benefitPurchasePaginatedRepository;
    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    private BenefitListUseRequestedPaginatedService benefitListUseRequestedPaginatedService;

    @BeforeEach
    void setUp() {
        benefitListUseRequestedPaginatedService = new BenefitListUseRequestedPaginatedService(
            benefitPurchasePaginatedRepository,
            teacherGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu108ListUseRequestedPaginated")
    class ListUseRequestedPaginated {

        @Test
        @DisplayName("Given teacher with use requested purchases When listing Then returns paginated use requested purchases")
        void whenTeacherHasUseRequestedPurchases_returnsPaginatedPurchases() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(1001L, subject);
            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchase = BenefitTestMother.useRequestedBenefitPurchase(benefit, student);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<BenefitPurchase> pageResult = new PageImpl<>(List.of(purchase), pageable, 1);

            BenefitPurchaseSimpleResponseDto dto = BenefitPurchaseSimpleResponseDto.builder()
                .id(purchase.getId())
                .benefitId(benefit.getId())
                .state(BenefitPurchaseState.USE_REQUESTED)
                .build();
            List<BenefitPurchaseSimpleResponseDto> mappedDtos = List.of(dto);

            PaginatedData<BenefitPurchaseSimpleResponseDto> expected = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                .results(mappedDtos)
                .count(1)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            String search = "student";

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);

            Specification<BenefitPurchase> notDeletedSpec = (root, query, cb) -> null;
            Specification<BenefitPurchase> notExpiredSpec = (root, query, cb) -> null;
            Specification<BenefitPurchase> studentNameSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitPurchasesSpecs> benefitPurchasesSpecsMock = org.mockito.Mockito.mockStatic(BenefitPurchasesSpecs.class);
                MockedStatic<BenefitPurchaseMapper> benefitPurchaseMapperMock = org.mockito.Mockito.mockStatic(BenefitPurchaseMapper.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.notDeleted()).thenReturn(notDeletedSpec);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.notExpired()).thenReturn(notExpiredSpec);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.studentFullNameContains(search))
                    .thenReturn(studentNameSpec);

                when(benefitPurchasePaginatedRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                benefitPurchaseMapperMock.when(() -> BenefitPurchaseMapper.toSimpleDtoList(pageResult.getContent()))
                    .thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, mappedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitPurchaseSimpleResponseDto> result = benefitListUseRequestedPaginatedService.cu108ListUseRequestedPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, search, null, null
                );

                // Then
                verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
                verify(benefitPurchasePaginatedRepository).findAll(any(), eq(pageable));
                assertThat(result.getCount()).isEqualTo(1);
                assertThat(result.getResults()).hasSize(1);
                assertThat(result.getResults().get(0).getState()).isEqualTo(BenefitPurchaseState.USE_REQUESTED);
            }
        }

        @Test
        @DisplayName("Given teacher with no use requested purchases When listing Then returns empty paginated data")
        void whenTeacherHasNoUseRequestedPurchases_returnsEmptyPaginatedData() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<BenefitPurchase> pageResult = new PageImpl<>(List.of(), pageable, 0);

            PaginatedData<BenefitPurchaseSimpleResponseDto> expected = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);

            Specification<BenefitPurchase> notDeletedSpec = (root, query, cb) -> null;
            Specification<BenefitPurchase> notExpiredSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitPurchasesSpecs> benefitPurchasesSpecsMock = org.mockito.Mockito.mockStatic(BenefitPurchasesSpecs.class);
                MockedStatic<BenefitPurchaseMapper> benefitPurchaseMapperMock = org.mockito.Mockito.mockStatic(BenefitPurchaseMapper.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.notDeleted()).thenReturn(notDeletedSpec);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.notExpired()).thenReturn(notExpiredSpec);

                when(benefitPurchasePaginatedRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                benefitPurchaseMapperMock.when(() -> BenefitPurchaseMapper.toSimpleDtoList(pageResult.getContent()))
                    .thenReturn(List.of());
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, List.of())).thenReturn(expected);

                // When
                PaginatedData<BenefitPurchaseSimpleResponseDto> result = benefitListUseRequestedPaginatedService.cu108ListUseRequestedPaginated(
                    user, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null
                );

                // Then
                assertThat(result.getCount()).isZero();
                assertThat(result.getResults()).isEmpty();
            }
        }
    }
}

