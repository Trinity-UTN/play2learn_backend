package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@ExtendWith(MockitoExtension.class)
class BenefitValidatePurchaseLimitServiceTest {

    private BenefitValidatePurchaseLimitService benefitValidatePurchaseLimitService;

    @BeforeEach
    void setUp() {
        benefitValidatePurchaseLimitService = new BenefitValidatePurchaseLimitService();
    }

    @Nested
    @DisplayName("validatePurchaseLimit")
    class ValidatePurchaseLimit {

        @Test
        @DisplayName("Given benefit with purchases left When validating Then does not throw exception")
        void whenBenefitHasPurchasesLeft_doesNotThrowException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            // purchasesLeft = 50 by default

            // When & Then
            assertThatCode(() -> benefitValidatePurchaseLimitService.validatePurchaseLimit(benefit))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Given benefit with null purchase limit When validating Then does not throw exception")
        void whenBenefitHasNullPurchaseLimit_doesNotThrowException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit unlimitedBenefit = BenefitTestMother.unlimitedBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);

            // When & Then
            assertThatCode(() -> benefitValidatePurchaseLimitService.validatePurchaseLimit(unlimitedBenefit))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Given benefit with zero purchase limit When validating Then does not throw exception")
        void whenBenefitHasZeroPurchaseLimit_doesNotThrowException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefitBuilder(BenefitTestMother.DEFAULT_BENEFIT_ID, subject)
                .purchaseLimit(0)
                .purchasesLeft(0)
                .build();

            // When & Then
            assertThatCode(() -> benefitValidatePurchaseLimitService.validatePurchaseLimit(benefit))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Given benefit with no purchases left When validating Then throws ConflictException")
        void whenBenefitHasNoPurchasesLeft_throwsConflictException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefitWithPurchasesLeft(
                BenefitTestMother.DEFAULT_BENEFIT_ID,
                subject,
                0
            );

            // When & Then
            assertThatThrownBy(() -> benefitValidatePurchaseLimitService.validatePurchaseLimit(benefit))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El beneficio ya ha alcanzado");
        }

        @Test
        @DisplayName("Given benefit with negative purchases left When validating Then throws ConflictException")
        void whenBenefitHasNegativePurchasesLeft_throwsConflictException() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefitWithPurchasesLeft(
                BenefitTestMother.DEFAULT_BENEFIT_ID,
                subject,
                -1
            );

            // When & Then
            assertThatThrownBy(() -> benefitValidatePurchaseLimitService.validatePurchaseLimit(benefit))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El beneficio ya ha alcanzado");
        }
    }
}

