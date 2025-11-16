package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;

@ExtendWith(MockitoExtension.class)
class BenefitGetPurchasesLeftByStudentServiceTest {

    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitGetPurchasesLeftByStudentService benefitGetPurchasesLeftByStudentService;

    @BeforeEach
    void setUp() {
        benefitGetPurchasesLeftByStudentService = new BenefitGetPurchasesLeftByStudentService(benefitPurchaseRepository);
    }

    @Nested
    @DisplayName("getPurchasesLeftByStudent")
    class GetPurchasesLeftByStudent {

        @Test
        @DisplayName("Given benefit with purchase limit per student and no purchases When getting purchases left Then returns limit")
        void whenNoPurchases_returnsLimit() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            when(benefitPurchaseRepository.findByBenefitAndStudent(benefit, student))
                .thenReturn(new ArrayList<>());

            // When
            Integer result = benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit, student);

            // Then
            assertThat(result).isEqualTo(benefit.getPurchaseLimitPerStudent());
            verify(benefitPurchaseRepository).findByBenefitAndStudent(benefit, student);
        }

        @Test
        @DisplayName("Given benefit with purchase limit per student and some purchases When getting purchases left Then returns remaining")
        void whenSomePurchases_returnsRemaining() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            BenefitPurchase purchase = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitPurchaseRepository.findByBenefitAndStudent(benefit, student))
                .thenReturn(List.of(purchase));

            // When
            Integer result = benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit, student);

            // Then
            assertThat(result).isEqualTo(0); // 1 - 1 = 0
            verify(benefitPurchaseRepository).findByBenefitAndStudent(benefit, student);
        }

        @Test
        @DisplayName("Given benefit with null purchase limit per student When getting purchases left Then returns null")
        void whenNullPurchaseLimit_returnsNull() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit unlimitedBenefit = BenefitTestMother.unlimitedBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            // When
            Integer result = benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(unlimitedBenefit, student);

            // Then
            assertThat(result).isNull();
            verify(benefitPurchaseRepository, org.mockito.Mockito.never()).findByBenefitAndStudent(unlimitedBenefit, student);
        }

        @Test
        @DisplayName("Given benefit with zero purchase limit per student When getting purchases left Then returns null")
        void whenZeroPurchaseLimit_returnsNull() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefitBuilder(BenefitTestMother.DEFAULT_BENEFIT_ID, subject)
                .purchaseLimitPerStudent(0)
                .build();
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            // When
            Integer result = benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit, student);

            // Then
            assertThat(result).isNull();
            verify(benefitPurchaseRepository, org.mockito.Mockito.never()).findByBenefitAndStudent(benefit, student);
        }
    }
}

