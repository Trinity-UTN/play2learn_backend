package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@ExtendWith(MockitoExtension.class)
class BenefitValidateIfPurchasedByStudentServiceTest {

    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitValidateIfPurchasedByStudentService benefitValidateIfPurchasedByStudentService;

    @BeforeEach
    void setUp() {
        benefitValidateIfPurchasedByStudentService = new BenefitValidateIfPurchasedByStudentService(benefitPurchaseRepository);
    }

    @Nested
    @DisplayName("validateIfPurchasedByStudent")
    class ValidateIfPurchasedByStudent {

        @Test
        @DisplayName("Given benefit and student with no purchases When validating Then does not throw exception")
        void whenNoPurchases_doesNotThrowException() {
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

            // When & Then
            assertThatCode(() -> benefitValidateIfPurchasedByStudentService.validateIfPurchasedByStudent(benefit, student))
                .doesNotThrowAnyException();

            verify(benefitPurchaseRepository).findByBenefitAndStudent(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with only used purchases When validating Then does not throw exception")
        void whenOnlyUsedPurchases_doesNotThrowException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            BenefitPurchase usedPurchase = BenefitTestMother.usedBenefitPurchase(benefit, student);

            when(benefitPurchaseRepository.findByBenefitAndStudent(benefit, student))
                .thenReturn(List.of(usedPurchase));

            // When & Then
            assertThatCode(() -> benefitValidateIfPurchasedByStudentService.validateIfPurchasedByStudent(benefit, student))
                .doesNotThrowAnyException();

            verify(benefitPurchaseRepository).findByBenefitAndStudent(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with purchased (not used) purchase When validating Then throws ConflictException")
        void whenPurchasedButNotUsed_throwsConflictException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            BenefitPurchase purchasedBenefit = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitPurchaseRepository.findByBenefitAndStudent(benefit, student))
                .thenReturn(List.of(purchasedBenefit));

            // When & Then
            assertThatThrownBy(() -> benefitValidateIfPurchasedByStudentService.validateIfPurchasedByStudent(benefit, student))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El estudiante debe usar este beneficio antes de poder volver a comprarlo");

            verify(benefitPurchaseRepository).findByBenefitAndStudent(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with use requested purchase When validating Then throws ConflictException")
        void whenUseRequested_throwsConflictException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            BenefitPurchase useRequestedPurchase = BenefitTestMother.useRequestedBenefitPurchase(benefit, student);

            when(benefitPurchaseRepository.findByBenefitAndStudent(benefit, student))
                .thenReturn(List.of(useRequestedPurchase));

            // When & Then
            assertThatThrownBy(() -> benefitValidateIfPurchasedByStudentService.validateIfPurchasedByStudent(benefit, student))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El estudiante debe usar este beneficio antes de poder volver a comprarlo");

            verify(benefitPurchaseRepository).findByBenefitAndStudent(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with multiple purchases where one is not used When validating Then throws ConflictException")
        void whenMultiplePurchasesWithOneNotUsed_throwsConflictException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            BenefitPurchase usedPurchase = BenefitTestMother.usedBenefitPurchase(benefit, student);
            BenefitPurchase purchasedBenefit = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitPurchaseRepository.findByBenefitAndStudent(benefit, student))
                .thenReturn(List.of(usedPurchase, purchasedBenefit));

            // When & Then
            assertThatThrownBy(() -> benefitValidateIfPurchasedByStudentService.validateIfPurchasedByStudent(benefit, student))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El estudiante debe usar este beneficio antes de poder volver a comprarlo");

            verify(benefitPurchaseRepository).findByBenefitAndStudent(benefit, student);
        }
    }
}

