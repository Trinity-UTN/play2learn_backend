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
import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetLastPurchaseService;

@ExtendWith(MockitoExtension.class)
class BenefitIsPurchasedServiceTest {

    @Mock
    private IBenefitGetLastPurchaseService benefitGetLastPurchaseService;

    private BenefitIsPurchasedService benefitIsPurchasedService;

    @BeforeEach
    void setUp() {
        benefitIsPurchasedService = new BenefitIsPurchasedService(benefitGetLastPurchaseService);
    }

    @Nested
    @DisplayName("isPurchased")
    class IsPurchased {

        @Test
        @DisplayName("Given benefit and student with purchased benefit When checking if purchased Then returns true")
        void whenBenefitPurchased_returnsTrue() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();
            BenefitPurchase purchase = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.of(purchase));

            // When
            Boolean result = benefitIsPurchasedService.isPurchased(student, benefit);

            // Then
            assertThat(result).isTrue();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with no purchase When checking if purchased Then returns false")
        void whenNoPurchase_returnsFalse() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.empty());

            // When
            Boolean result = benefitIsPurchasedService.isPurchased(student, benefit);

            // Then
            assertThat(result).isFalse();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with used purchase When checking if purchased Then returns false")
        void whenPurchaseUsed_returnsFalse() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();
            BenefitPurchase usedPurchase = BenefitTestMother.usedBenefitPurchase(benefit, student);

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.of(usedPurchase));

            // When
            Boolean result = benefitIsPurchasedService.isPurchased(student, benefit);

            // Then
            assertThat(result).isFalse();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with use requested purchase When checking if purchased Then returns false")
        void whenPurchaseUseRequested_returnsFalse() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();
            BenefitPurchase useRequestedPurchase = BenefitTestMother.useRequestedBenefitPurchase(benefit, student);

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.of(useRequestedPurchase));

            // When
            Boolean result = benefitIsPurchasedService.isPurchased(student, benefit);

            // Then
            assertThat(result).isFalse();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }
    }
}

