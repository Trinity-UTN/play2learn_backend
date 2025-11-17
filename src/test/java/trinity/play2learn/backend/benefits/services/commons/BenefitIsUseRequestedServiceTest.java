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
class BenefitIsUseRequestedServiceTest {

    @Mock
    private IBenefitGetLastPurchaseService benefitGetLastPurchaseService;

    private BenefitIsUseRequestedService benefitIsUseRequestedService;

    @BeforeEach
    void setUp() {
        benefitIsUseRequestedService = new BenefitIsUseRequestedService(benefitGetLastPurchaseService);
    }

    @Nested
    @DisplayName("isUseRequested")
    class IsUseRequested {

        @Test
        @DisplayName("Given benefit and student with use requested purchase When checking if use requested Then returns true")
        void whenUseRequested_returnsTrue() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();
            BenefitPurchase useRequestedPurchase = BenefitTestMother.useRequestedBenefitPurchase(benefit, student);

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.of(useRequestedPurchase));

            // When
            Boolean result = benefitIsUseRequestedService.isUseRequested(benefit, student);

            // Then
            assertThat(result).isTrue();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with no purchase When checking if use requested Then returns false")
        void whenNoPurchase_returnsFalse() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.empty());

            // When
            Boolean result = benefitIsUseRequestedService.isUseRequested(benefit, student);

            // Then
            assertThat(result).isFalse();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with purchased (not use requested) purchase When checking if use requested Then returns false")
        void whenPurchasedButNotUseRequested_returnsFalse() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();
            BenefitPurchase purchasedBenefit = BenefitTestMother.purchasedBenefitPurchase(benefit, student);

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.of(purchasedBenefit));

            // When
            Boolean result = benefitIsUseRequestedService.isUseRequested(benefit, student);

            // Then
            assertThat(result).isFalse();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }

        @Test
        @DisplayName("Given benefit and student with used purchase When checking if use requested Then returns false")
        void whenPurchaseUsed_returnsFalse() {
            // Given
            Benefit benefit = BenefitTestMother.defaultBenefit();
            Student student = BenefitTestMother.defaultStudent();
            BenefitPurchase usedPurchase = BenefitTestMother.usedBenefitPurchase(benefit, student);

            when(benefitGetLastPurchaseService.getLastPurchase(benefit, student))
                .thenReturn(Optional.of(usedPurchase));

            // When
            Boolean result = benefitIsUseRequestedService.isUseRequested(benefit, student);

            // Then
            assertThat(result).isFalse();
            verify(benefitGetLastPurchaseService).getLastPurchase(benefit, student);
        }
    }
}

