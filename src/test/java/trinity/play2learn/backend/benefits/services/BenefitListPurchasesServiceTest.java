package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitListPurchasesServiceTest {

    private static final Long BENEFIT_ID = 1001L;
    private static final String TEACHER_EMAIL = "teacher@example.com";
    private static final String UNAUTHORIZED_TEACHER_EMAIL = "other.teacher@example.com";

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;
    @Mock
    private BenefitGetByIdService benefitGetByIdService;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitListPurchasesService benefitListPurchasesService;

    @BeforeEach
    void setUp() {
        benefitListPurchasesService = new BenefitListPurchasesService(
            teacherGetByEmailService,
            benefitGetByIdService,
            benefitPurchaseRepository
        );
    }

    @Nested
    @DisplayName("cu98ListPurchasesByBenefitId")
    class ListPurchasesByBenefitId {

        @Test
        @DisplayName("Given valid benefit and authorized teacher When listing Then returns list of purchases")
        void whenValidBenefitAndAuthorizedTeacher_returnsPurchasesList() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);
            Student student1 = BenefitTestMother.student(401L, "student1@example.com");
            Student student2 = BenefitTestMother.student(402L, "student2@example.com");
            BenefitPurchase purchase1 = BenefitTestMother.purchasedBenefitPurchase(benefit, student1);
            BenefitPurchase purchase2 = BenefitTestMother.purchasedBenefitPurchase(benefit, student2);
            List<BenefitPurchase> purchases = List.of(purchase1, purchase2);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            when(benefitPurchaseRepository.findAllByBenefit(benefit)).thenReturn(purchases);

            // When
            List<BenefitPurchaseSimpleResponseDto> result = benefitListPurchasesService.cu98ListPurchasesByBenefitId(user, BENEFIT_ID);

            // Then
            verify(teacherGetByEmailService).getByEmail(TEACHER_EMAIL);
            verify(benefitGetByIdService).getById(BENEFIT_ID);
            verify(benefitPurchaseRepository).findAllByBenefit(benefit);
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Given benefit not found When listing Then propagates NotFoundException")
        void whenBenefitMissing_propagatesNotFound() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BENEFIT_ID))
                .thenThrow(new NotFoundException("Beneficio no encontrado"));

            // When & Then
            // Nota: teacherGetByEmailService se llama antes de validar si el beneficio existe
            assertThatThrownBy(() -> benefitListPurchasesService.cu98ListPurchasesByBenefitId(user, BENEFIT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(teacherGetByEmailService).getByEmail(user.getEmail());
            verifyNoInteractions(benefitPurchaseRepository);
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
            assertThatThrownBy(() -> benefitListPurchasesService.cu98ListPurchasesByBenefitId(unauthorizedUser, BENEFIT_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede obtener las compras de este beneficio ya que no pertenece al docente");

            verifyNoInteractions(benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given benefit with no purchases When listing Then returns empty list")
        void whenBenefitHasNoPurchases_returnsEmptyList() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            when(benefitPurchaseRepository.findAllByBenefit(benefit)).thenReturn(List.of());

            // When
            List<BenefitPurchaseSimpleResponseDto> result = benefitListPurchasesService.cu98ListPurchasesByBenefitId(user, BENEFIT_ID);

            // Then
            verify(benefitPurchaseRepository).findAllByBenefit(benefit);
            assertThat(result).isEmpty();
        }
    }
}

