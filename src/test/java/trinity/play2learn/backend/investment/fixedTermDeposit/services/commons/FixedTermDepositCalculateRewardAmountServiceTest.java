package trinity.play2learn.backend.investment.fixedTermDeposit.services.commons;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;

@ExtendWith(MockitoExtension.class)
class FixedTermDepositCalculateRewardAmountServiceTest {

    private FixedTermDepositCalculateRewardAmountService fixedTermDepositCalculateRewardAmountService;

    @BeforeEach
    void setUp() {
        fixedTermDepositCalculateRewardAmountService = new FixedTermDepositCalculateRewardAmountService();
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given amount invested and monthly fixed term days When calculating reward amount Then returns correct interest")
        void whenMonthlyFixedTermDays_returnsCorrectInterest() {
            // Given - Monto invertido y plazo mensual
            Double amountInvested = InvestmentTestMother.AMOUNT_MEDIUM;
            FixedTermDays fixedTermDays = FixedTermDays.MENSUAL;
            Double expectedInterest = amountInvested * 1.5 * (fixedTermDays.getValor() / 365.0);

            // When - Calcular monto de recompensa
            Double result = fixedTermDepositCalculateRewardAmountService.execute(amountInvested, fixedTermDays);

            // Then - Debe retornar interés calculado correctamente
            assertThat(result).isEqualTo(expectedInterest);
        }

        @Test
        @DisplayName("Given amount invested and weekly fixed term days When calculating reward amount Then returns correct interest")
        void whenWeeklyFixedTermDays_returnsCorrectInterest() {
            // Given - Monto invertido y plazo semanal
            Double amountInvested = InvestmentTestMother.AMOUNT_MEDIUM;
            FixedTermDays fixedTermDays = FixedTermDays.SEMANAL;
            Double expectedInterest = amountInvested * 1.5 * (fixedTermDays.getValor() / 365.0);

            // When - Calcular monto de recompensa
            Double result = fixedTermDepositCalculateRewardAmountService.execute(amountInvested, fixedTermDays);

            // Then - Debe retornar interés calculado correctamente
            assertThat(result).isEqualTo(expectedInterest);
        }

        @Test
        @DisplayName("Given different amounts invested When calculating reward amount Then returns proportional interest")
        void whenDifferentAmounts_returnsProportionalInterest() {
            // Given - Diferentes montos invertidos
            Double amountSmall = InvestmentTestMother.AMOUNT_SMALL;
            Double amountLarge = InvestmentTestMother.AMOUNT_LARGE;
            FixedTermDays fixedTermDays = FixedTermDays.MENSUAL;

            Double expectedInterestSmall = amountSmall * 1.5 * (fixedTermDays.getValor() / 365.0);
            Double expectedInterestLarge = amountLarge * 1.5 * (fixedTermDays.getValor() / 365.0);

            // When - Calcular monto de recompensa
            Double resultSmall = fixedTermDepositCalculateRewardAmountService.execute(amountSmall, fixedTermDays);
            Double resultLarge = fixedTermDepositCalculateRewardAmountService.execute(amountLarge, fixedTermDays);

            // Then - Debe retornar intereses proporcionales
            assertThat(resultSmall).isEqualTo(expectedInterestSmall);
            assertThat(resultLarge).isEqualTo(expectedInterestLarge);
            assertThat(resultLarge).isGreaterThan(resultSmall);
        }
    }
}

