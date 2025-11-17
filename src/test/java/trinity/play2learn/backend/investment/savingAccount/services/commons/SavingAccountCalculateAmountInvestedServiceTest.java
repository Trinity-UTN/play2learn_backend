package trinity.play2learn.backend.investment.savingAccount.services.commons;

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
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;

@ExtendWith(MockitoExtension.class)
class SavingAccountCalculateAmountInvestedServiceTest {

    @Mock
    private ISavingAccountRepository savingAccountRepository;

    private SavingAccountCalculateAmountInvestedService savingAccountCalculateAmountInvestedService;

    @BeforeEach
    void setUp() {
        savingAccountCalculateAmountInvestedService = new SavingAccountCalculateAmountInvestedService(
            savingAccountRepository
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with saving accounts When calculating amount invested Then returns sum of current amounts")
        void whenWalletWithSavingAccounts_returnsSumOfCurrentAmounts() {
            // Given - Wallet con cajas de ahorro
            Wallet wallet = InvestmentTestMother.defaultWallet();
            SavingAccount account1 = InvestmentTestMother.defaultSavingAccount();
            account1.setWallet(wallet);
            SavingAccount account2 = InvestmentTestMother.savingAccount(902L, "Otra Caja",
                    InvestmentTestMother.AMOUNT_MEDIUM, InvestmentTestMother.AMOUNT_MEDIUM + 100.0,
                    InvestmentTestMother.INTEREST_ACCUMULATED, wallet);
            List<SavingAccount> accounts = Arrays.asList(account1, account2);
            Double expectedTotal = account1.getCurrentAmount() + account2.getCurrentAmount();

            when(savingAccountRepository.findAllByWalletAndDeletedAtIsNull(wallet)).thenReturn(accounts);

            // When - Calcular monto invertido
            Double result = savingAccountCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar suma de montos actuales
            verify(savingAccountRepository).findAllByWalletAndDeletedAtIsNull(wallet);
            assertThat(result).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Given wallet with no saving accounts When calculating amount invested Then returns zero")
        void whenNoSavingAccounts_returnsZero() {
            // Given - Sin cajas de ahorro
            Wallet wallet = InvestmentTestMother.defaultWallet();
            List<SavingAccount> accounts = Collections.emptyList();

            when(savingAccountRepository.findAllByWalletAndDeletedAtIsNull(wallet)).thenReturn(accounts);

            // When - Calcular monto invertido
            Double result = savingAccountCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar cero
            verify(savingAccountRepository).findAllByWalletAndDeletedAtIsNull(wallet);
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("Given saving accounts with different current amounts When calculating amount invested Then returns correct sum")
        void whenDifferentCurrentAmounts_returnsCorrectSum() {
            // Given - Cajas de ahorro con diferentes montos actuales
            Wallet wallet = InvestmentTestMother.defaultWallet();
            SavingAccount account1 = InvestmentTestMother.savingAccountWithBalance(
                    InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID, InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                    InvestmentTestMother.AMOUNT_SMALL);
            account1.setWallet(wallet);
            SavingAccount account2 = InvestmentTestMother.savingAccount(902L, "Otra Caja",
                    InvestmentTestMother.AMOUNT_LARGE, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.INTEREST_ACCUMULATED, wallet);
            List<SavingAccount> accounts = Arrays.asList(account1, account2);
            Double expectedTotal = account1.getCurrentAmount() + account2.getCurrentAmount();

            when(savingAccountRepository.findAllByWalletAndDeletedAtIsNull(wallet)).thenReturn(accounts);

            // When - Calcular monto invertido
            Double result = savingAccountCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar suma correcta
            assertThat(result).isEqualTo(expectedTotal);
        }
    }
}

