package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsPurchasedService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsUseRequestedService;

@ExtendWith(MockitoExtension.class)
class BenefitGetStudentStateServiceTest {

    @Mock
    private IBenefitIsPurchasedService benefitIsPurchasedService;
    @Mock
    private IBenefitIsUseRequestedService benefitIsUseRequestedService;

    private BenefitGetStudentStateService benefitGetStudentStateService;

    @BeforeEach
    void setUp() {
        benefitGetStudentStateService = new BenefitGetStudentStateService(
            benefitIsPurchasedService,
            benefitIsUseRequestedService
        );
    }

    @Nested
    @DisplayName("getStudentState")
    class GetStudentState {

        @Test
        @DisplayName("Given expired benefit When getting state Then returns EXPIRED")
        void whenBenefitExpired_returnsExpired() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit expiredBenefit = BenefitTestMother.expiredBenefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            // When
            BenefitStudentState result = benefitGetStudentStateService.getStudentState(expiredBenefit, student);

            // Then
            assertThat(result).isEqualTo(BenefitStudentState.EXPIRED);
            // No need to verify service calls since expired check is done first in the implementation
        }

        @Test
        @DisplayName("Given benefit with use requested When getting state Then returns USE_REQUESTED")
        void whenUseRequested_returnsUseRequested() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            when(benefitIsPurchasedService.isPurchased(student, benefit)).thenReturn(true);
            when(benefitIsUseRequestedService.isUseRequested(benefit, student)).thenReturn(true);

            // When
            BenefitStudentState result = benefitGetStudentStateService.getStudentState(benefit, student);

            // Then
            assertThat(result).isEqualTo(BenefitStudentState.USE_REQUESTED);
            verify(benefitIsPurchasedService).isPurchased(student, benefit);
            verify(benefitIsUseRequestedService).isUseRequested(benefit, student);
        }

        @Test
        @DisplayName("Given purchased benefit When getting state Then returns PURCHASED")
        void whenPurchased_returnsPurchased() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            when(benefitIsPurchasedService.isPurchased(student, benefit)).thenReturn(true);
            when(benefitIsUseRequestedService.isUseRequested(benefit, student)).thenReturn(false);

            // When
            BenefitStudentState result = benefitGetStudentStateService.getStudentState(benefit, student);

            // Then
            assertThat(result).isEqualTo(BenefitStudentState.PURCHASED);
            verify(benefitIsPurchasedService).isPurchased(student, benefit);
            verify(benefitIsUseRequestedService).isUseRequested(benefit, student);
        }

        @Test
        @DisplayName("Given available benefit When getting state Then returns AVAILABLE")
        void whenAvailable_returnsAvailable() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            when(benefitIsPurchasedService.isPurchased(student, benefit)).thenReturn(false);
            when(benefitIsUseRequestedService.isUseRequested(benefit, student)).thenReturn(false);

            // When
            BenefitStudentState result = benefitGetStudentStateService.getStudentState(benefit, student);

            // Then
            assertThat(result).isEqualTo(BenefitStudentState.AVAILABLE);
            verify(benefitIsPurchasedService).isPurchased(student, benefit);
            verify(benefitIsUseRequestedService).isUseRequested(benefit, student);
        }
    }
}

