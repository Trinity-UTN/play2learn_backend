package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitDeleteServiceTest {

    @Mock
    private IBenefitRepository benefitRepository;
    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;
    @Mock
    private BenefitGetByIdService benefitGetByIdService;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;
    @Mock
    private ITransactionGenerateService transactionGenerateService;

    private BenefitDeleteService benefitDeleteService;

    @BeforeEach
    void setUp() {
        benefitDeleteService = new BenefitDeleteService(
            benefitRepository,
            teacherGetByEmailService,
            benefitGetByIdService,
            benefitPurchaseRepository,
            transactionGenerateService
        );
    }

    @Nested
    @DisplayName("cu94DeleteBenefit")
    class DeleteBenefit {

        @Test
        @DisplayName("Given existing benefit and authorized teacher When deleting Then marks as deleted and persists")
        void whenBenefitExistsAndTeacherAuthorized_marksAsDeleted() {
            // Given
            User user = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                teacher
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(teacherGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(benefit);
            when(benefitPurchaseRepository.findByBenefit(benefit)).thenReturn(new ArrayList<>());

            // When
            benefitDeleteService.cu94DeleteBenefit(user, BenefitTestMother.DEFAULT_BENEFIT_ID);

            // Then
            ArgumentCaptor<Benefit> benefitCaptor = ArgumentCaptor.forClass(Benefit.class);
            verify(benefitRepository).save(benefitCaptor.capture());

            Benefit deletedBenefit = benefitCaptor.getValue();
            assertThat(deletedBenefit.getDeletedAt()).isNotNull();
            verify(teacherGetByEmailService).getByEmail(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            verify(benefitGetByIdService).getById(BenefitTestMother.DEFAULT_BENEFIT_ID);
        }

        @Test
        @DisplayName("Given benefit not found When deleting Then propagates NotFoundException")
        void whenBenefitMissing_propagatesNotFound() {
            // Given
            User user = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);

            when(teacherGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .thenThrow(new NotFoundException("Beneficio no encontrado"));

            // When & Then
            assertThatThrownBy(() -> benefitDeleteService.cu94DeleteBenefit(user, BenefitTestMother.DEFAULT_BENEFIT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(teacherGetByEmailService).getByEmail(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            verify(benefitGetByIdService).getById(BenefitTestMother.DEFAULT_BENEFIT_ID);
            verifyNoInteractions(benefitRepository, benefitPurchaseRepository, transactionGenerateService);
        }

        @Test
        @DisplayName("Given teacher is not owner of benefit When deleting Then throws ConflictException")
        void whenTeacherNotOwner_throwsConflict() {
            // Given
            User unauthorizedUser = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_UNAUTHORIZED_TEACHER_EMAIL);
            Teacher unauthorizedTeacher = BenefitTestMother.teacher(302L, BenefitTestMother.DEFAULT_UNAUTHORIZED_TEACHER_EMAIL);
            Teacher authorizedTeacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                authorizedTeacher
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(teacherGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_UNAUTHORIZED_TEACHER_EMAIL)).thenReturn(unauthorizedTeacher);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(benefit);

            // When & Then
            assertThatThrownBy(() -> benefitDeleteService.cu94DeleteBenefit(unauthorizedUser, BenefitTestMother.DEFAULT_BENEFIT_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No es posible eliminar este beneficio ya que no pertenece al docente");

            verify(teacherGetByEmailService).getByEmail(BenefitTestMother.DEFAULT_UNAUTHORIZED_TEACHER_EMAIL);
            verify(benefitGetByIdService).getById(BenefitTestMother.DEFAULT_BENEFIT_ID);
            verifyNoInteractions(benefitRepository, benefitPurchaseRepository, transactionGenerateService);
        }

        @Test
        @DisplayName("Given benefit with active purchases When deleting Then refunds all active purchases")
        void whenBenefitHasActivePurchases_refundsAllActivePurchases() {
            // Given
            User user = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                teacher
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student1 = BenefitTestMother.student(401L, "student1@example.com");
            Student student2 = BenefitTestMother.student(402L, "student2@example.com");

            BenefitPurchase purchase1 = BenefitTestMother.purchasedBenefitPurchase(benefit, student1);
            BenefitPurchase purchase2 = BenefitTestMother.useRequestedBenefitPurchase(benefit, student2);
            BenefitPurchase usedPurchase = BenefitTestMother.usedBenefitPurchase(benefit, student1);

            List<BenefitPurchase> purchases = List.of(purchase1, purchase2, usedPurchase);

            when(teacherGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(benefit);
            when(benefitPurchaseRepository.findByBenefit(benefit)).thenReturn(purchases);

            // When
            benefitDeleteService.cu94DeleteBenefit(user, BenefitTestMother.DEFAULT_BENEFIT_ID);

            // Then
            ArgumentCaptor<Benefit> benefitCaptor = ArgumentCaptor.forClass(Benefit.class);
            verify(benefitRepository).save(benefitCaptor.capture());

            // Verifica que se generen reembolsos solo para compras activas (no USED)
            verify(transactionGenerateService, times(2)).generate(
                eq(TypeTransaction.REEMBOLSO),
                eq((double) benefit.getCost()),
                eq("Reembolso de beneficio"),
                eq(TransactionActor.SISTEMA),
                eq(TransactionActor.ESTUDIANTE),
                any(),
                any(),
                any(),
                eq(benefit),
                any(),
                any(),
                any()
            );

            Benefit deletedBenefit = benefitCaptor.getValue();
            assertThat(deletedBenefit.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("Given expired benefit When deleting Then skips refund logic and marks as deleted")
        void whenBenefitExpired_skipsRefundLogic() {
            // Given
            User user = BenefitTestMother.teacherUser(BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Teacher teacher = BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                teacher
            );
            Benefit expiredBenefit = BenefitTestMother.expiredBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(401L, "student@example.com");
            BenefitPurchase purchase = BenefitTestMother.purchasedBenefitPurchase(expiredBenefit, student);

            when(teacherGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_TEACHER_EMAIL)).thenReturn(teacher);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(expiredBenefit);
            lenient().when(benefitPurchaseRepository.findByBenefit(expiredBenefit)).thenReturn(List.of(purchase));

            // When
            benefitDeleteService.cu94DeleteBenefit(user, BenefitTestMother.DEFAULT_BENEFIT_ID);

            // Then
            ArgumentCaptor<Benefit> benefitCaptor = ArgumentCaptor.forClass(Benefit.class);
            verify(benefitRepository).save(benefitCaptor.capture());

            // Verifica que NO se generen reembolsos si el beneficio está expirado
            verify(transactionGenerateService, never()).generate(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            );

            Benefit deletedBenefit = benefitCaptor.getValue();
            assertThat(deletedBenefit.getDeletedAt()).isNotNull();
        }
    }
}

