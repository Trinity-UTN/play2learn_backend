package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import java.util.Optional;

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
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitGetByIdService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetLastPurchaseService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitRequestUseServiceTest {

    private static final Long BENEFIT_ID = 1001L;
    private static final String STUDENT_EMAIL = "student@example.com";

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private BenefitGetByIdService benefitGetByIdService;
    @Mock
    private ISubjectHasStudentService subjectHasStudentService;
    @Mock
    private IBenefitGetLastPurchaseService benefitGetLastPurchaseService;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitRequestUseService benefitRequestUseService;

    @BeforeEach
    void setUp() {
        benefitRequestUseService = new BenefitRequestUseService(
            studentGetByEmailService,
            benefitGetByIdService,
            subjectHasStudentService,
            benefitGetLastPurchaseService,
            benefitPurchaseRepository
        );
    }

    @Nested
    @DisplayName("cu81RequestBenefitUse")
    class RequestBenefitUse {

        @Test
        @DisplayName("Given valid benefit purchase When requesting use Then updates state to USE_REQUESTED and persists")
        void whenValidPurchase_updatesStateToUseRequested() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            subject.setStudents(List.of(student));
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);
            BenefitPurchase purchase = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student)).thenReturn(Optional.of(purchase));
            when(benefitPurchaseRepository.save(purchase)).thenReturn(purchase);

            // When
            BenefitPurchaseSimpleResponseDto response = benefitRequestUseService.cu81RequestBenefitUse(user, BENEFIT_ID);

            // Then
            ArgumentCaptor<BenefitPurchase> purchaseCaptor = ArgumentCaptor.forClass(BenefitPurchase.class);
            verify(benefitPurchaseRepository).save(purchaseCaptor.capture());

            BenefitPurchase savedPurchase = purchaseCaptor.getValue();
            assertThat(savedPurchase.getState()).isEqualTo(BenefitPurchaseState.USE_REQUESTED);
            assertThat(response.getId()).isEqualTo(purchase.getId());
        }

        @Test
        @DisplayName("Given benefit not found When requesting use Then propagates NotFoundException")
        void whenBenefitMissing_propagatesNotFound() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BENEFIT_ID))
                .thenThrow(new NotFoundException("Beneficio no encontrado"));

            // When & Then
            assertThatThrownBy(() -> benefitRequestUseService.cu81RequestBenefitUse(user, BENEFIT_ID))
                .isInstanceOf(NotFoundException.class);

            verifyNoInteractions(benefitGetLastPurchaseService, benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given expired benefit When requesting use Then throws ConflictException")
        void whenBenefitExpired_throwsConflict() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            Benefit expiredBenefit = BenefitTestMother.expiredBenefit(BENEFIT_ID, subject);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(expiredBenefit);

            // When & Then
            assertThatThrownBy(() -> benefitRequestUseService.cu81RequestBenefitUse(user, BENEFIT_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El beneficio ha expirado");

            verifyNoInteractions(benefitGetLastPurchaseService, benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given student not enrolled in subject When requesting use Then throws ConflictException")
        void whenStudentNotEnrolled_throwsConflict() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            doThrow(new ConflictException("El estudiante no está asignado"))
                .when(subjectHasStudentService).subjectHasStudent(subject, student);

            // When & Then
            assertThatThrownBy(() -> benefitRequestUseService.cu81RequestBenefitUse(user, BENEFIT_ID))
                .isInstanceOf(ConflictException.class);

            verifyNoInteractions(benefitGetLastPurchaseService, benefitPurchaseRepository);
        }

        @Test
        @DisplayName("Given student has not purchased benefit When requesting use Then throws ConflictException")
        void whenStudentHasNotPurchased_throwsConflict() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            subject.setStudents(List.of(student));
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> benefitRequestUseService.cu81RequestBenefitUse(user, BENEFIT_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El estudiante debe comprar el beneficio para solicitar su uso");

            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
            verify(benefitPurchaseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given purchase is not in PURCHASED state When requesting use Then throws ConflictException")
        void whenPurchaseNotInPurchasedState_throwsConflict() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            subject.setStudents(List.of(student));
            Benefit benefit = BenefitTestMother.benefit(BENEFIT_ID, subject);
            BenefitPurchase useRequestedPurchase = BenefitTestMother.useRequestedBenefitPurchase(benefit, student);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByIdService.getById(BENEFIT_ID)).thenReturn(benefit);
            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student)).thenReturn(Optional.of(useRequestedPurchase));

            // When & Then
            assertThatThrownBy(() -> benefitRequestUseService.cu81RequestBenefitUse(user, BENEFIT_ID))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El estudiante debe comprar el beneficio para solicitar su uso");

            verify(benefitPurchaseRepository, never()).save(any());
        }
    }
}

