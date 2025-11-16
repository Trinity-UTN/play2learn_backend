package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchasePaginatedRepository;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitGetByIdService;
import trinity.play2learn.backend.benefits.specs.BenefitPurchasesSpecs;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class BenefitListPurchasesPaginatedServiceTest {

    private static final Long BENEFIT_ID = 1001L;
    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "id";
    private static final String ORDER_TYPE = "asc";
    private static final String TEACHER_EMAIL = "teacher@example.com";
    private static final String UNAUTHORIZED_TEACHER_EMAIL = "other.teacher@example.com";

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;
    @Mock
    private BenefitGetByIdService benefitGetByIdService;
    @Mock
    private IBenefitPurchasePaginatedRepository benefitPurchasePaginatedRepository;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitListPurchasesPaginatedService benefitListPurchasesPaginatedService;

    @BeforeEach
    void setUp() {
        benefitListPurchasesPaginatedService = new BenefitListPurchasesPaginatedService(
            teacherGetByEmailService,
            benefitGetByIdService,
            benefitPurchasePaginatedRepository,
            benefitPurchaseRepository
        );
    }

    @Nested
    @DisplayName("cu101ListPurchasesPaginated")
    class ListPurchasesPaginated {

        @Test
        @DisplayName("Given valid benefit and authorized teacher When listing Then returns paginated purchases")
        void whenValidBenefitAndAuthorizedTeacher_returnsPaginatedPurchases() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);
            Student student1 = BenefitTestMother.student(401L, "student1@example.com");
            Student student2 = BenefitTestMother.student(402L, "student2@example.com");
            BenefitPurchase purchase1 = BenefitTestMother.purchasedBenefitPurchase(benefit, student1);
            BenefitPurchase purchase2 = BenefitTestMother.purchasedBenefitPurchase(benefit, student2);
            
            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);
            Page<BenefitPurchase> pageResult = new PageImpl<>(List.of(purchase1, purchase2), pageable, 2);
            
            BenefitPurchaseSimpleResponseDto dto1 = BenefitPurchaseSimpleResponseDto.builder()
                .id(purchase1.getId())
                .benefitId(benefit.getId())
                .benefitName(benefit.getName())
                .build();
            BenefitPurchaseSimpleResponseDto dto2 = BenefitPurchaseSimpleResponseDto.builder()
                .id(purchase2.getId())
                .benefitId(benefit.getId())
                .benefitName(benefit.getName())
                .build();
            List<BenefitPurchaseSimpleResponseDto> mappedDtos = List.of(dto1, dto2);
            
            PaginatedData<BenefitPurchaseSimpleResponseDto> expected = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                .results(mappedDtos)
                .count(2)
                .totalPages(1)
                .currentPage(PAGE)
                .pageSize(SIZE)
                .build();

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            when(benefitPurchaseRepository.findAllByBenefit(benefit)).thenReturn(List.of(purchase1, purchase2));

            Specification<BenefitPurchase> notDeletedSpec = (root, query, cb) -> null;
            Specification<BenefitPurchase> studentNameSpec = (root, query, cb) -> null;

            try (
                MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                MockedStatic<BenefitPurchasesSpecs> benefitPurchasesSpecsMock = org.mockito.Mockito.mockStatic(BenefitPurchasesSpecs.class);
                MockedStatic<BenefitPurchaseMapper> benefitPurchaseMapperMock = org.mockito.Mockito.mockStatic(BenefitPurchaseMapper.class);
                MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)
            ) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.notDeleted()).thenReturn(notDeletedSpec);
                benefitPurchasesSpecsMock.when(() -> BenefitPurchasesSpecs.studentFullNameContains("student"))
                    .thenReturn(studentNameSpec);

                when(benefitPurchasePaginatedRepository.findAll(any(), eq(pageable))).thenReturn(pageResult);
                benefitPurchaseMapperMock.when(() -> BenefitPurchaseMapper.toSimpleDtoList(pageResult.getContent()))
                    .thenReturn(mappedDtos);
                paginationMock.when(() -> PaginationHelper.fromPage(pageResult, mappedDtos)).thenReturn(expected);

                // When
                PaginatedData<BenefitPurchaseSimpleResponseDto> result = benefitListPurchasesPaginatedService.cu101ListPurchasesPaginated(
                    user, BENEFIT_ID, PAGE, SIZE, ORDER_BY, ORDER_TYPE, "student", null, null
                );

                // Then
                verify(benefitGetByIdService).getById(BENEFIT_ID);
                verify(benefitPurchaseRepository).findAllByBenefit(benefit);
                assertThat(result.getCount()).isEqualTo(2);
                assertThat(result.getResults()).hasSize(2);
            }
        }

        @Test
        @DisplayName("Given teacher is not owner of benefit When listing Then throws ConflictException")
        void whenTeacherNotOwner_throwsConflict() {
            // Given
            User unauthorizedUser = BenefitTestMother.teacherUser(UNAUTHORIZED_TEACHER_EMAIL);
            Teacher unauthorizedTeacher = BenefitTestMother.teacher(302L, UNAUTHORIZED_TEACHER_EMAIL);
            Teacher authorizedTeacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), authorizedTeacher);
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);

            when(teacherGetByEmailService.getByEmail(UNAUTHORIZED_TEACHER_EMAIL)).thenReturn(unauthorizedTeacher);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);

            // When & Then
            assertThatThrownBy(() -> benefitListPurchasesPaginatedService.cu101ListPurchasesPaginated(
                unauthorizedUser, BENEFIT_ID, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede obtener las compras de este beneficio ya que no pertenece al docente");

            verifyNoInteractions(benefitPurchaseRepository, benefitPurchasePaginatedRepository);
        }

        @Test
        @DisplayName("Given benefit with no purchases When listing Then returns empty paginated data")
        void whenBenefitHasNoPurchases_returnsEmptyPaginatedData() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);

            Pageable pageable = PageRequest.of(PAGE - 1, SIZE);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            when(benefitPurchaseRepository.findAllByBenefit(benefit)).thenReturn(List.of());

            try (MockedStatic<PaginatorUtils> paginatorMock = org.mockito.Mockito.mockStatic(PaginatorUtils.class);
                 MockedStatic<PaginationHelper> paginationMock = org.mockito.Mockito.mockStatic(PaginationHelper.class)) {
                paginatorMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE)).thenReturn(pageable);
                PaginatedData<BenefitPurchaseSimpleResponseDto> expected = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                    .results(List.of())
                    .count(0)
                    .totalPages(0)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();
                paginationMock.when(() -> PaginationHelper.fromPage(Page.empty(pageable), List.of())).thenReturn(expected);

                // When
                PaginatedData<BenefitPurchaseSimpleResponseDto> result = benefitListPurchasesPaginatedService.cu101ListPurchasesPaginated(
                    user, BENEFIT_ID, PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null
                );

                // Then
                assertThat(result.getCount()).isZero();
                assertThat(result.getResults()).isEmpty();
            }
        }
    }
}

