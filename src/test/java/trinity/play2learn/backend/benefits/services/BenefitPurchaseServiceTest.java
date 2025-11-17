package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectHasStudentService;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitGetByIdService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetPurchasesLeftByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitValidateIfPurchasedByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitValidatePurchaseLimitService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitPurchaseServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private ISubjectHasStudentService subjectHasStudentService;
    @Mock
    private BenefitGetByIdService benefitGetByIdService;
    @Mock
    private IBenefitValidatePurchaseLimitService benefitValidatePurchaseLimitService;
    @Mock
    private IBenefitGetPurchasesLeftByStudentService benefitGetPurchasesPerStudentService;
    @Mock
    private IBenefitValidateIfPurchasedByStudentService benefitValidateIfPurchasedByStudentService;
    @Mock
    private ITransactionGenerateService transactionGenerateService;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitPurchaseService benefitPurchaseService;

    @BeforeEach
    void setUp() {
        benefitPurchaseService = new BenefitPurchaseService(
            studentGetByEmailService,
            subjectHasStudentService,
            benefitGetByIdService,
            benefitValidatePurchaseLimitService,
            benefitGetPurchasesPerStudentService,
            benefitValidateIfPurchasedByStudentService,
            transactionGenerateService,
            benefitPurchaseRepository
        );
    }

    @Nested
    @DisplayName("cu75PurchaseBenefit")
    class PurchaseBenefit {

        @Test
        @DisplayName("Given valid benefit request and authorized student When purchasing Then creates purchase, generates transaction and decrements limit")
        void whenRequestValidAndStudentAuthorized_persistsPurchaseAndGeneratesTransaction() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacherAndStudent(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.DEFAULT_COURSE_ID,
                BenefitTestMother.DEFAULT_TEACHER_EMAIL,
                BenefitTestMother.DEFAULT_STUDENT_ID,
                BenefitTestMother.DEFAULT_STUDENT_EMAIL
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            BenefitPurchase savedPurchase = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(benefit);
            when(benefitGetPurchasesPerStudentService.getPurchasesLeftByStudent(benefit, student)).thenReturn(1);
            when(benefitPurchaseRepository.save(any(BenefitPurchase.class))).thenReturn(savedPurchase);

            // When
            BenefitPurchaseResponseDto response = benefitPurchaseService.cu75PurchaseBenefit(request, user);

            // Then
            verify(studentGetByEmailService).getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            verify(benefitGetByIdService).getById(BenefitTestMother.DEFAULT_BENEFIT_ID);
            verify(subjectHasStudentService).subjectHasStudent(subject, student);
            verify(benefitValidatePurchaseLimitService).validatePurchaseLimit(benefit);
            verify(benefitGetPurchasesPerStudentService).getPurchasesLeftByStudent(benefit, student);
            verify(benefitValidateIfPurchasedByStudentService).validateIfPurchasedByStudent(benefit, student);

            ArgumentCaptor<BenefitPurchase> purchaseCaptor = ArgumentCaptor.forClass(BenefitPurchase.class);
            verify(benefitPurchaseRepository).save(purchaseCaptor.capture());

            BenefitPurchase capturedPurchase = purchaseCaptor.getValue();
            assertThat(capturedPurchase.getBenefit()).isEqualTo(benefit);
            assertThat(capturedPurchase.getStudent()).isEqualTo(student);

            verify(transactionGenerateService).generate(
                eq(TypeTransaction.COMPRA),
                eq((double) benefit.getCost()),
                eq("Compra de beneficio"),
                eq(TransactionActor.ESTUDIANTE),
                eq(TransactionActor.SISTEMA),
                eq(student.getWallet()),
                eq(null),
                eq(null),
                eq(benefit),
                eq(null),
                eq(null),
                eq(null)
            );

            assertThat(response.getId()).isEqualTo(savedPurchase.getId());
            assertThat(benefit.getPurchasesLeft()).isEqualTo(49); // 50 - 1
        }

        @Test
        @DisplayName("Given student not found When purchasing Then propagates NotFoundException and stops flow")
        void whenStudentMissing_propagatesNotFound() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL))
                .thenThrow(new NotFoundException("Estudiante no encontrado"));

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseService.cu75PurchaseBenefit(request, user))
                .isInstanceOf(NotFoundException.class);

            verify(studentGetByEmailService).getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            verifyNoInteractions(
                benefitGetByIdService,
                subjectHasStudentService,
                benefitValidatePurchaseLimitService,
                benefitGetPurchasesPerStudentService,
                benefitValidateIfPurchasedByStudentService,
                transactionGenerateService,
                benefitPurchaseRepository
            );
        }

        @Test
        @DisplayName("Given benefit not found When purchasing Then propagates NotFoundException and stops flow")
        void whenBenefitMissing_propagatesNotFound() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .thenThrow(new NotFoundException("Beneficio no encontrado"));

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseService.cu75PurchaseBenefit(request, user))
                .isInstanceOf(NotFoundException.class);

            verify(studentGetByEmailService).getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            verify(benefitGetByIdService).getById(BenefitTestMother.DEFAULT_BENEFIT_ID);
            verifyNoInteractions(
                subjectHasStudentService,
                benefitValidatePurchaseLimitService,
                benefitGetPurchasesPerStudentService,
                benefitValidateIfPurchasedByStudentService,
                transactionGenerateService,
                benefitPurchaseRepository
            );
        }

        @Test
        @DisplayName("Given expired benefit When purchasing Then throws ConflictException and avoids persistence")
        void whenBenefitExpired_throwsConflict() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit expiredBenefit = BenefitTestMother.expiredBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(expiredBenefit);

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseService.cu75PurchaseBenefit(request, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No se puede comprar este beneficio ya que ha expirado");

            verify(studentGetByEmailService).getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            verify(benefitGetByIdService).getById(BenefitTestMother.DEFAULT_BENEFIT_ID);
            verifyNoInteractions(
                subjectHasStudentService,
                benefitValidatePurchaseLimitService,
                benefitGetPurchasesPerStudentService,
                benefitValidateIfPurchasedByStudentService,
                transactionGenerateService,
                benefitPurchaseRepository
            );
        }

        @Test
        @DisplayName("Given student not enrolled in subject When purchasing Then throws ConflictException and avoids persistence")
        void whenStudentNotEnrolledInSubject_throwsConflict() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(benefit);
            doThrow(new ConflictException("El estudiante no puede comprar este beneficio ya que no esta asignado a la materia"))
                .when(subjectHasStudentService).subjectHasStudent(subject, student);

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseService.cu75PurchaseBenefit(request, user))
                .isInstanceOf(ConflictException.class);

            verify(subjectHasStudentService).subjectHasStudent(subject, student);
            verifyNoInteractions(
                benefitValidatePurchaseLimitService,
                benefitGetPurchasesPerStudentService,
                benefitValidateIfPurchasedByStudentService,
                transactionGenerateService,
                benefitPurchaseRepository
            );
        }

        @Test
        @DisplayName("Given student reached purchase limit per student When purchasing Then throws ConflictException and avoids persistence")
        void whenStudentReachedPurchaseLimitPerStudent_throwsConflict() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacherAndStudent(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.DEFAULT_COURSE_ID,
                BenefitTestMother.DEFAULT_TEACHER_EMAIL,
                BenefitTestMother.DEFAULT_STUDENT_ID,
                BenefitTestMother.DEFAULT_STUDENT_EMAIL
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(benefit);
            when(benefitGetPurchasesPerStudentService.getPurchasesLeftByStudent(benefit, student)).thenReturn(0);

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseService.cu75PurchaseBenefit(request, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El estudiante ya ha alcanzado");

            verify(benefitGetPurchasesPerStudentService).getPurchasesLeftByStudent(benefit, student);
            verifyNoInteractions(
                benefitValidateIfPurchasedByStudentService,
                transactionGenerateService,
                benefitPurchaseRepository
            );
        }

        @Test
        @DisplayName("Given unlimited benefit When purchasing Then creates purchase without limit checks")
        void whenUnlimitedBenefit_createsPurchaseWithoutLimitChecks() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacherAndStudent(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.DEFAULT_COURSE_ID,
                BenefitTestMother.DEFAULT_TEACHER_EMAIL,
                BenefitTestMother.DEFAULT_STUDENT_ID,
                BenefitTestMother.DEFAULT_STUDENT_EMAIL
            );
            Benefit unlimitedBenefit = BenefitTestMother.unlimitedBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            BenefitPurchase savedPurchase = BenefitTestMother.purchasedBenefitPurchase(unlimitedBenefit, student);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(unlimitedBenefit);
            when(benefitGetPurchasesPerStudentService.getPurchasesLeftByStudent(unlimitedBenefit, student)).thenReturn(null);
            when(benefitPurchaseRepository.save(any(BenefitPurchase.class))).thenReturn(savedPurchase);

            // When
            BenefitPurchaseResponseDto response = benefitPurchaseService.cu75PurchaseBenefit(request, user);

            // Then
            ArgumentCaptor<BenefitPurchase> purchaseCaptor = ArgumentCaptor.forClass(BenefitPurchase.class);
            verify(benefitPurchaseRepository).save(purchaseCaptor.capture());

            BenefitPurchase capturedPurchase = purchaseCaptor.getValue();
            assertThat(capturedPurchase.getBenefit()).isEqualTo(unlimitedBenefit);
            assertThat(capturedPurchase.getStudent()).isEqualTo(student);

            verify(transactionGenerateService).generate(
                eq(TypeTransaction.COMPRA),
                eq((double) unlimitedBenefit.getCost()),
                eq("Compra de beneficio"),
                eq(TransactionActor.ESTUDIANTE),
                eq(TransactionActor.SISTEMA),
                eq(student.getWallet()),
                eq(null),
                eq(null),
                eq(unlimitedBenefit),
                eq(null),
                eq(null),
                eq(null)
            );

            assertThat(response.getId()).isEqualTo(savedPurchase.getId());
            // Verifica que no se decrementa purchasesLeft si es ilimitado
        }

        @Test
        @DisplayName("Given student has purchased but not used benefit When purchasing again Then throws ConflictException and avoids persistence")
        void whenStudentHasPurchasedButNotUsed_throwsConflict() {
            // Given
            BenefitPurchaseRequestDto request = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);
            User user = BenefitTestMother.studentUser(BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacherAndStudent(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.DEFAULT_COURSE_ID,
                BenefitTestMother.DEFAULT_TEACHER_EMAIL,
                BenefitTestMother.DEFAULT_STUDENT_ID,
                BenefitTestMother.DEFAULT_STUDENT_EMAIL
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            when(studentGetByEmailService.getByEmail(BenefitTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID)).thenReturn(benefit);
            when(benefitGetPurchasesPerStudentService.getPurchasesLeftByStudent(benefit, student)).thenReturn(1);
            doThrow(new ConflictException("El estudiante debe usar este beneficio antes de poder volver a comprarlo"))
                .when(benefitValidateIfPurchasedByStudentService).validateIfPurchasedByStudent(benefit, student);

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseService.cu75PurchaseBenefit(request, user))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El estudiante debe usar este beneficio antes de poder volver a comprarlo");

            verify(benefitValidateIfPurchasedByStudentService).validateIfPurchasedByStudent(benefit, student);
            verifyNoInteractions(transactionGenerateService, benefitPurchaseRepository);
        }
    }
}

