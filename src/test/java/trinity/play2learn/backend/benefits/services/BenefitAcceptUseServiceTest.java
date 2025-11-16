package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitPurchaseGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitAcceptUseServiceTest {

    private static final Long PURCHASE_ID = 501L;
    private static final String TEACHER_EMAIL = "teacher@example.com";
    private static final String UNAUTHORIZED_TEACHER_EMAIL = "other.teacher@example.com";

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;
    @Mock
    private BenefitPurchaseGetByIdService benefitPurchaseGetByIdService;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitAcceptUseService benefitAcceptUseService;

    @BeforeEach
    void setUp() {
        benefitAcceptUseService = new BenefitAcceptUseService(
            teacherGetByEmailService,
            benefitPurchaseGetByIdService,
            benefitPurchaseRepository
        );
    }

    @Nested
    @DisplayName("cu85AcceptBenefitUse")
    class AcceptBenefitUse {

        @Test
        @DisplayName("Given valid use request When accepting Then updates state to USED and sets usedAt timestamp")
        void whenValidUseRequest_updatesStateToUsed() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(1001L, subject);
            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchase = BenefitTestMother.useRequestedBenefitPurchase(benefit, student);

            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitPurchaseGetByIdService.getById(PURCHASE_ID)).thenReturn(purchase);
            when(benefitPurchaseRepository.save(purchase)).thenReturn(purchase);

            // When
            BenefitPurchaseSimpleResponseDto response = benefitAcceptUseService.cu85AcceptBenefitUse(user, PURCHASE_ID);

            // Then
            ArgumentCaptor<BenefitPurchase> purchaseCaptor = ArgumentCaptor.forClass(BenefitPurchase.class);
            verify(benefitPurchaseRepository).save(purchaseCaptor.capture());

            BenefitPurchase savedPurchase = purchaseCaptor.getValue();
            assertThat(savedPurchase.getState()).isEqualTo(BenefitPurchaseState.USED);
            assertThat(savedPurchase.getUsedAt()).isNotNull();
            assertThat(response.getId()).isEqualTo(purchase.getId());
        }

        @Test
        @DisplayName("Given purchase not found When accepting Then propagates NotFoundException")
        void whenPurchaseMissing_propagatesNotFound() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);

            when(benefitPurchaseGetByIdService.getById(PURCHASE_ID))
                .thenThrow(new NotFoundException("Compra no encontrada"));

            // When & Then
            assertThatThrownBy(() -> benefitAcceptUseService.cu85AcceptBenefitUse(user, PURCHASE_ID))
                .isInstanceOf(NotFoundException.class);

            verifyNoInteractions(teacherGetByEmailService, benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given benefit is deleted When accepting Then throws ConflictException")
        void whenBenefitDeleted_throwsConflict() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit deletedBenefit = BenefitTestMother.deletedBenefit(1001L, subject);
            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchase = BenefitTestMother.useRequestedBenefitPurchase(deletedBenefit, student);

            when(benefitPurchaseGetByIdService.getById(PURCHASE_ID)).thenReturn(purchase);

            // When & Then
            assertThatThrownBy(() -> benefitAcceptUseService.cu85AcceptBenefitUse(user, PURCHASE_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede aceptar la solicitud del beneficio ya que este ha sido eliminado");

            verifyNoInteractions(benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given teacher is not owner of benefit When accepting Then throws ConflictException")
        void whenTeacherNotOwner_throwsConflict() {
            // Given
            User unauthorizedUser = BenefitTestMother.teacherUser(UNAUTHORIZED_TEACHER_EMAIL);
            Teacher unauthorizedTeacher = BenefitTestMother.teacher(302L, UNAUTHORIZED_TEACHER_EMAIL);
            Teacher authorizedTeacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), authorizedTeacher);
            Benefit benefit = BenefitTestMother.benefit(1001L, subject);
            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchase = BenefitTestMother.useRequestedBenefitPurchase(benefit, student);

            when(benefitPurchaseGetByIdService.getById(PURCHASE_ID)).thenReturn(purchase);
            when(teacherGetByEmailService.getByEmail(UNAUTHORIZED_TEACHER_EMAIL)).thenReturn(unauthorizedTeacher);

            // When & Then
            assertThatThrownBy(() -> benefitAcceptUseService.cu85AcceptBenefitUse(unauthorizedUser, PURCHASE_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede aceptar la solicitud de uso de este beneficio ya que no pertenece al docente");

            verifyNoInteractions(benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given benefit is expired When accepting Then throws ConflictException")
        void whenBenefitExpired_throwsConflict() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit expiredBenefit = BenefitTestMother.expiredBenefit(1001L, subject);
            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchase = BenefitTestMother.useRequestedBenefitPurchase(expiredBenefit, student);

            when(benefitPurchaseGetByIdService.getById(PURCHASE_ID)).thenReturn(purchase);
            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);

            // When & Then
            assertThatThrownBy(() -> benefitAcceptUseService.cu85AcceptBenefitUse(user, PURCHASE_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede aceptar la solicitud de uso de este beneficio ya que ha expirado");

            verifyNoInteractions(benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given purchase is not in USE_REQUESTED state When accepting Then throws ConflictException")
        void whenPurchaseNotInUseRequestedState_throwsConflict() {
            // Given
            User user = BenefitTestMother.teacherUser(TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(301L, TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), teacher);
            Benefit benefit = BenefitTestMother.benefit(1001L, subject);
            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchasedPurchase = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitPurchaseGetByIdService.getById(PURCHASE_ID)).thenReturn(purchasedPurchase);
            when(teacherGetByEmailService.getByEmail(TEACHER_EMAIL)).thenReturn(teacher);

            // When & Then
            assertThatThrownBy(() -> benefitAcceptUseService.cu85AcceptBenefitUse(user, PURCHASE_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede aceptar la solicitud de uso de este beneficio ya que no ha sido solicitada");

            verifyNoInteractions(benefitPurchaseRepository);
        }
    }
}

