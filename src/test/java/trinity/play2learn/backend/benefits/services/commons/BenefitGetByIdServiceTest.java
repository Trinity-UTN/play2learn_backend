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

import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class BenefitGetByIdServiceTest {

    @Mock
    private IBenefitRepository benefitRepository;

    private BenefitGetByIdService benefitGetByIdService;

    @BeforeEach
    void setUp() {
        benefitGetByIdService = new BenefitGetByIdService(benefitRepository);
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Given existing benefit ID When getting by ID Then returns benefit")
        void whenBenefitExists_returnsBenefit() {
            // Given
            Benefit expectedBenefit = BenefitTestMother.defaultBenefit();

            when(benefitRepository.findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .thenReturn(Optional.of(expectedBenefit));

            // When
            Benefit result = benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(BenefitTestMother.DEFAULT_BENEFIT_ID);
            assertThat(result.getName()).isEqualTo(expectedBenefit.getName());
            verify(benefitRepository).findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_BENEFIT_ID);
        }

        @Test
        @DisplayName("Given non-existing benefit ID When getting by ID Then throws NotFoundException")
        void whenBenefitNotExists_throwsNotFoundException() {
            // Given
            when(benefitRepository.findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No existe un beneficio con el id proporcionado");

            verify(benefitRepository).findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_BENEFIT_ID);
        }

        @Test
        @DisplayName("Given deleted benefit ID When getting by ID Then throws NotFoundException")
        void whenBenefitDeleted_throwsNotFoundException() {
            // Given
            when(benefitRepository.findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> benefitGetByIdService.getById(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(benefitRepository).findByIdAndDeletedAtIsNull(BenefitTestMother.DEFAULT_BENEFIT_ID);
        }
    }
}

