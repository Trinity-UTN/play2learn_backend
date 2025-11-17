package trinity.play2learn.backend.benefits.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentCountResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.commons.BenefitGetByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetLastPurchaseService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetPurchasesLeftByStudentService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class BenefitStudentCountServiceTest {

    private static final String STUDENT_EMAIL = "student@example.com";

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;
    @Mock
    private BenefitGetByStudentService benefitGetByStudentService;
    @Mock
    private IBenefitGetLastPurchaseService benefitGetLastPurchaseService;
    @Mock
    private IBenefitGetPurchasesLeftByStudentService benefitGetPurchasesLeftByStudentService;
    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitStudentCountService benefitStudentCountService;

    @BeforeEach
    void setUp() {
        benefitStudentCountService = new BenefitStudentCountService(
            studentGetByEmailService,
            benefitGetByStudentService,
            benefitGetLastPurchaseService,
            benefitGetPurchasesLeftByStudentService,
            benefitPurchaseRepository
        );
    }

    @Nested
    @DisplayName("cu89CountByStudentState")
    class CountByStudentState {

        @Test
        @DisplayName("Given student with benefits in different states When counting Then returns correct counts")
        void whenStudentHasBenefitsInDifferentStates_returnsCorrectCounts() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);
            Subject subject = BenefitTestMother.subjectWithTeacher(201L, BenefitTestMother.course(101L), BenefitTestMother.teacher(301L, "teacher@example.com"));
            
            Benefit availableBenefit = BenefitTestMother.benefit(1001L, subject);
            Benefit purchasedBenefit = BenefitTestMother.benefit(1002L, subject);
            Benefit useRequestedBenefit = BenefitTestMother.benefit(1003L, subject);
            Benefit expiredBenefit = BenefitTestMother.expiredBenefit(1004L, subject);
            
            List<Benefit> allBenefits = List.of(availableBenefit, purchasedBenefit, useRequestedBenefit, expiredBenefit);
            
            BenefitPurchase purchasedPurchase = BenefitTestMother.purchasedBenefitPurchase(purchasedBenefit, student);
            BenefitPurchase useRequestedPurchase = BenefitTestMother.useRequestedBenefitPurchase(useRequestedBenefit, student);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByStudentService.getByStudent(student)).thenReturn(allBenefits);
            when(benefitPurchaseRepository.countByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED)).thenReturn(0);
            
            when(benefitGetLastPurchaseService.getLastPurchase(availableBenefit, student)).thenReturn(Optional.empty());
            when(benefitGetLastPurchaseService.getLastPurchase(purchasedBenefit, student)).thenReturn(Optional.of(purchasedPurchase));
            when(benefitGetLastPurchaseService.getLastPurchase(useRequestedBenefit, student)).thenReturn(Optional.of(useRequestedPurchase));
            when(benefitGetLastPurchaseService.getLastPurchase(expiredBenefit, student)).thenReturn(Optional.empty());
            
            when(benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(availableBenefit, student)).thenReturn(1);

            // When
            BenefitStudentCountResponseDto result = benefitStudentCountService.cu89CountByStudentState(user);

            // Then
            verify(studentGetByEmailService).getByEmail(STUDENT_EMAIL);
            verify(benefitGetByStudentService).getByStudent(student);
            verify(benefitPurchaseRepository).countByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED);
            
            assertThat(result.getAvailable()).isEqualTo(1);
            assertThat(result.getPurchased()).isEqualTo(1);
            assertThat(result.getUse_requested()).isEqualTo(1);
            assertThat(result.getExpired()).isEqualTo(1);
            assertThat(result.getUsed()).isEqualTo(0);
        }

        @Test
        @DisplayName("Given student with no benefits When counting Then returns zeros")
        void whenStudentHasNoBenefits_returnsZeros() {
            // Given
            User user = BenefitTestMother.studentUser(STUDENT_EMAIL);
            Student student = BenefitTestMother.student(401L, STUDENT_EMAIL);

            when(studentGetByEmailService.getByEmail(STUDENT_EMAIL)).thenReturn(student);
            when(benefitGetByStudentService.getByStudent(student)).thenReturn(List.of());
            when(benefitPurchaseRepository.countByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED)).thenReturn(0);

            // When
            BenefitStudentCountResponseDto result = benefitStudentCountService.cu89CountByStudentState(user);

            // Then
            assertThat(result.getAvailable()).isZero();
            assertThat(result.getPurchased()).isZero();
            assertThat(result.getUse_requested()).isZero();
            assertThat(result.getExpired()).isZero();
            assertThat(result.getUsed()).isZero();
        }
    }
}

