package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
class BenefitGetLastPurchaseServiceTest {

    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitGetLastPurchaseService benefitGetLastPurchaseService;

    @BeforeEach
    void setUp() {
        benefitGetLastPurchaseService = new BenefitGetLastPurchaseService(benefitPurchaseRepository);
    }

    @Nested
    @DisplayName("getLastPurchase")
    class GetLastPurchase {

        @Test
        @DisplayName("Given benefit and student with purchases When getting last purchase Then returns last purchase")
        void whenPurchasesExist_returnsLastPurchase() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            BenefitPurchase expectedPurchase = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitPurchaseRepository.findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student))
                .thenReturn(Optional.of(expectedPurchase));

            // When
            Optional<BenefitPurchase> result = benefitGetLastPurchaseService.getLastPurchase(benefit, student);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(expectedPurchase.getId());
            assertThat(result.get().getBenefit()).isEqualTo(benefit);
            assertThat(result.get().getStudent()).isEqualTo(student);
            verify(benefitPurchaseRepository).findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with no purchases When getting last purchase Then returns empty optional")
        void whenNoPurchasesExist_returnsEmptyOptional() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);

            when(benefitPurchaseRepository.findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student))
                .thenReturn(Optional.empty());

            // When
            Optional<BenefitPurchase> result = benefitGetLastPurchaseService.getLastPurchase(benefit, student);

            // Then
            assertThat(result).isEmpty();
            verify(benefitPurchaseRepository).findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student);
        }
    }
}

