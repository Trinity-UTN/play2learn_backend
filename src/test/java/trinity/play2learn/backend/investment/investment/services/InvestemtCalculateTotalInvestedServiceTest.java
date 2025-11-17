package trinity.play2learn.backend.investment.investment.services;

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

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositCalculateAmountInvestedService;
import trinity.play2learn.backend.investment.savingAccount.services.commons.SavingAccountCalculateAmountInvestedService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateAmountInvestedService;

@ExtendWith(MockitoExtension.class)
class InvestemtCalculateTotalInvestedServiceTest {

    @Mock
    private IStockCalculateAmountInvestedService stockCalculateAmountInvestedService;

    @Mock
    private IFixedTermDepositCalculateAmountInvestedService fixedTermDepositCalculateAmountInvestedService;

    @Mock
    private SavingAccountCalculateAmountInvestedService savingAccountCalculateAmountInvestedService;

    private InvestemtCalculateTotalInvestedService investemtCalculateTotalInvestedService;

    @BeforeEach
    void setUp() {
        investemtCalculateTotalInvestedService = new InvestemtCalculateTotalInvestedService(
            stockCalculateAmountInvestedService,
            fixedTermDepositCalculateAmountInvestedService,
            savingAccountCalculateAmountInvestedService
        );
    }

    @Nested
    @DisplayName("cu110calculateTotalInvested")
    class Cu110calculateTotalInvested {

        @Test
        @DisplayName("Given wallet with investments When calculating total Then returns sum of all investments")
        void whenWalletWithInvestments_returnsSumOfAllInvestments() {
            // Given - Wallet con inversiones
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Double stockAmount = InvestmentTestMother.DEFAULT_INITIAL_AMOUNT;
            Double fixedTermDepositAmount = InvestmentTestMother.AMOUNT_MEDIUM * 3;
            Double savingAccountAmount = InvestmentTestMother.AMOUNT_MEDIUM * 2;
            Double expectedTotal = stockAmount + fixedTermDepositAmount + savingAccountAmount;

            when(stockCalculateAmountInvestedService.execute(wallet)).thenReturn(stockAmount);
            when(fixedTermDepositCalculateAmountInvestedService.execute(wallet)).thenReturn(fixedTermDepositAmount);
            when(savingAccountCalculateAmountInvestedService.execute(wallet)).thenReturn(savingAccountAmount);

            // When - Calcular total invertido
            Double result = investemtCalculateTotalInvestedService.cu110calculateTotalInvested(wallet);

            // Then - Debe retornar suma de todas las inversiones
            verify(stockCalculateAmountInvestedService).execute(wallet);
            verify(fixedTermDepositCalculateAmountInvestedService).execute(wallet);
            verify(savingAccountCalculateAmountInvestedService).execute(wallet);
            assertThat(result).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Given wallet with zero investments When calculating total Then returns zero")
        void whenZeroInvestments_returnsZero() {
            // Given - Wallet sin inversiones
            Wallet wallet = InvestmentTestMother.defaultWallet();

            when(stockCalculateAmountInvestedService.execute(wallet)).thenReturn(0.0);
            when(fixedTermDepositCalculateAmountInvestedService.execute(wallet)).thenReturn(0.0);
            when(savingAccountCalculateAmountInvestedService.execute(wallet)).thenReturn(0.0);

            // When - Calcular total invertido
            Double result = investemtCalculateTotalInvestedService.cu110calculateTotalInvested(wallet);

            // Then - Debe retornar cero
            assertThat(result).isZero();
        }
    }
}

