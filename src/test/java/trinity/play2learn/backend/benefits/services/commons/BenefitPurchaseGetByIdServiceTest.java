package trinity.play2learn.backend.benefits.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class BenefitPurchaseGetByIdServiceTest {

    @Mock
    private IBenefitPurchaseRepository benefitPurchaseRepository;

    private BenefitPurchaseGetByIdService benefitPurchaseGetByIdService;

    @BeforeEach
    void setUp() {
        benefitPurchaseGetByIdService = new BenefitPurchaseGetByIdService(benefitPurchaseRepository);
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Given existing purchase ID When getting by ID Then returns purchase")
        void whenPurchaseExists_returnsPurchase() {
            // Given
            Subject subject = BenefitTestMother.subjectWithTeacher(
                BenefitTestMother.DEFAULT_SUBJECT_ID,
                BenefitTestMother.course(BenefitTestMother.DEFAULT_COURSE_ID),
                BenefitTestMother.teacher(BenefitTestMother.DEFAULT_TEACHER_ID, BenefitTestMother.DEFAULT_TEACHER_EMAIL)
            );
            Benefit benefit = BenefitTestMother.benefit(BenefitTestMother.DEFAULT_BENEFIT_ID, subject);
            Student student = BenefitTestMother.student(BenefitTestMother.DEFAULT_STUDENT_ID, BenefitTestMother.DEFAULT_STUDENT_EMAIL);
            BenefitPurchase expectedPurchase = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitPurchaseRepository.findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .thenReturn(Optional.of(expectedPurchase));

            // When
            BenefitPurchase result = benefitPurchaseGetByIdService.getById(BenefitTestMother.DEFAULT_PURCHASE_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(BenefitTestMother.DEFAULT_PURCHASE_ID);
            assertThat(result.getBenefit()).isEqualTo(benefit);
            assertThat(result.getStudent()).isEqualTo(student);
            verify(benefitPurchaseRepository).findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_PURCHASE_ID);
        }

        @Test
        @DisplayName("Given non-existing purchase ID When getting by ID Then throws NotFoundException")
        void whenPurchaseNotExists_throwsNotFoundException() {
            // Given
            when(benefitPurchaseRepository.findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseGetByIdService.getById(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No existe una compra de beneficio con el id proporcionado");

            verify(benefitPurchaseRepository).findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_PURCHASE_ID);
        }

        @Test
        @DisplayName("Given deleted purchase ID When getting by ID Then throws NotFoundException")
        void whenPurchaseDeleted_throwsNotFoundException() {
            // Given
            when(benefitPurchaseRepository.findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> benefitPurchaseGetByIdService.getById(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .isInstanceOf(NotFoundException.class);

            verify(benefitPurchaseRepository).findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_PURCHASE_ID);
        }
    }
}

