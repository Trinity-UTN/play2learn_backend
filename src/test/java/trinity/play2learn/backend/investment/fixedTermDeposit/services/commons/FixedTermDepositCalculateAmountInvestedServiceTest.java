package trinity.play2learn.backend.investment.fixedTermDeposit.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;

@ExtendWith(MockitoExtension.class)
class FixedTermDepositCalculateAmountInvestedServiceTest {

    @Mock
    private IFixedTermDepositRepository fixedTermDepositRepository;

    private FixedTermDepositCalculateAmountInvestedService fixedTermDepositCalculateAmountInvestedService;

    @BeforeEach
    void setUp() {
        fixedTermDepositCalculateAmountInvestedService = new FixedTermDepositCalculateAmountInvestedService(
            fixedTermDepositRepository
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with fixed term deposits in progress When calculating amount invested Then returns sum of reward amounts")
        void whenWalletWithDepositsInProgress_returnsSumOfRewardAmounts() {
            // Given - Wallet con plazos fijos en progreso
            Wallet wallet = InvestmentTestMother.defaultWallet();
            FixedTermDeposit deposit1 = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    InvestmentTestMother.AMOUNT_MEDIUM + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            FixedTermDeposit deposit2 = InvestmentTestMother.fixedTermDeposit(802L, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.AMOUNT_LARGE + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            List<FixedTermDeposit> deposits = Arrays.asList(deposit1, deposit2);
            Double expectedTotal = deposit1.getAmountReward() + deposit2.getAmountReward();

            when(fixedTermDepositRepository.findByFixedTermState(FixedTermState.IN_PROGRESS)).thenReturn(deposits);

            // When - Calcular monto invertido
            Double result = fixedTermDepositCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar suma de montos de recompensa
            verify(fixedTermDepositRepository).findByFixedTermState(FixedTermState.IN_PROGRESS);
            assertThat(result).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Given wallet with no fixed term deposits in progress When calculating amount invested Then returns zero")
        void whenNoDepositsInProgress_returnsZero() {
            // Given - Sin plazos fijos en progreso
            Wallet wallet = InvestmentTestMother.defaultWallet();
            List<FixedTermDeposit> deposits = Collections.emptyList();

            when(fixedTermDepositRepository.findByFixedTermState(FixedTermState.IN_PROGRESS)).thenReturn(deposits);

            // When - Calcular monto invertido
            Double result = fixedTermDepositCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar cero
            verify(fixedTermDepositRepository).findByFixedTermState(FixedTermState.IN_PROGRESS);
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("Given fixed term deposits with different reward amounts When calculating amount invested Then returns correct sum")
        void whenDepositsWithDifferentRewards_returnsCorrectSum() {
            // Given - Plazos fijos con diferentes montos de recompensa
            Wallet wallet = InvestmentTestMother.defaultWallet();
            FixedTermDeposit deposit1 = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_SMALL,
                    InvestmentTestMother.AMOUNT_SMALL + InvestmentTestMother.INTEREST_SMALL, FixedTermDays.SEMANAL,
                    FixedTermState.IN_PROGRESS, wallet);
            FixedTermDeposit deposit2 = InvestmentTestMother.fixedTermDeposit(802L, InvestmentTestMother.AMOUNT_MEDIUM,
                    InvestmentTestMother.AMOUNT_MEDIUM + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            List<FixedTermDeposit> deposits = Arrays.asList(deposit1, deposit2);
            Double expectedTotal = deposit1.getAmountReward() + deposit2.getAmountReward();

            when(fixedTermDepositRepository.findByFixedTermState(FixedTermState.IN_PROGRESS)).thenReturn(deposits);

            // When - Calcular monto invertido
            Double result = fixedTermDepositCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar suma correcta
            assertThat(result).isEqualTo(expectedTotal);
        }
    }
}

