package trinity.play2learn.backend.investment.savingAccount.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;

@ExtendWith(MockitoExtension.class)
class SavingAccountUpdateServiceTest {

    @Mock
    private ISavingAccountRepository savingAccountRepository;

    @Mock
    private IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    private SavingAccountUpdateService savingAccountUpdateService;

    @BeforeEach
    void setUp() {
        savingAccountUpdateService = new SavingAccountUpdateService(
            savingAccountRepository,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu107updateSavingAccounts")
    class Cu107updateSavingAccounts {

        @Test
        @DisplayName("Given saving accounts with outdated lastUpdate When updating Then calculates and applies daily interest")
        void whenOutdatedAccounts_calculatesAndAppliesDailyInterest() {
            // Given - Cajas de ahorro desactualizadas
            Wallet wallet = InvestmentTestMother.defaultWallet();
            SavingAccount account1 = InvestmentTestMother.defaultSavingAccount();
            account1.setWallet(wallet);
            account1.setLastUpdate(LocalDate.now().minusDays(1));
            account1.setAccumulatedInterest(0.0);
            Double initialAmount1 = account1.getCurrentAmount();
            Double expectedInterest1 = initialAmount1 * 0.001;
            Double expectedNewAmount1 = initialAmount1 + expectedInterest1;

            SavingAccount account2 = InvestmentTestMother.savingAccount(902L, "Otra Caja",
                    InvestmentTestMother.AMOUNT_LARGE, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.INTEREST_ACCUMULATED, wallet);
            account2.setLastUpdate(LocalDate.now().minusDays(2));
            Double initialAmount2 = account2.getCurrentAmount();
            Double expectedInterest2 = initialAmount2 * 0.001;
            Double expectedNewAmount2 = initialAmount2 + expectedInterest2;

            List<SavingAccount> accounts = Arrays.asList(account1, account2);

            when(savingAccountRepository.findAllByDeletedAtIsNull()).thenReturn(accounts);
            when(savingAccountRepository.save(any(SavingAccount.class))).thenAnswer(invocation -> {
                SavingAccount saved = invocation.getArgument(0);
                return saved;
            });

            // When - Ejecutar actualización
            savingAccountUpdateService.cu107updateSavingAccounts();

            // Then - Debe calcular y aplicar interés diario (0.1%)
            ArgumentCaptor<SavingAccount> accountCaptor = ArgumentCaptor.forClass(SavingAccount.class);
            verify(savingAccountRepository, org.mockito.Mockito.times(2)).save(accountCaptor.capture());

            List<SavingAccount> capturedAccounts = accountCaptor.getAllValues();
            org.assertj.core.api.Assertions.assertThat(capturedAccounts).hasSize(2);

            SavingAccount captured1 = capturedAccounts.stream()
                    .filter(a -> a.getId().equals(account1.getId())).findFirst().orElse(null);
            org.assertj.core.api.Assertions.assertThat(captured1).isNotNull();
            org.assertj.core.api.Assertions.assertThat(captured1.getCurrentAmount()).isEqualTo(expectedNewAmount1);
            org.assertj.core.api.Assertions.assertThat(captured1.getAccumulatedInterest())
                    .isEqualTo(expectedInterest1);
            org.assertj.core.api.Assertions.assertThat(captured1.getLastUpdate()).isEqualTo(LocalDate.now());

            SavingAccount captured2 = capturedAccounts.stream()
                    .filter(a -> a.getId().equals(account2.getId())).findFirst().orElse(null);
            org.assertj.core.api.Assertions.assertThat(captured2).isNotNull();
            org.assertj.core.api.Assertions.assertThat(captured2.getCurrentAmount()).isEqualTo(expectedNewAmount2);
            org.assertj.core.api.Assertions.assertThat(captured2.getAccumulatedInterest())
                    .isEqualTo(InvestmentTestMother.INTEREST_ACCUMULATED + expectedInterest2);
            org.assertj.core.api.Assertions.assertThat(captured2.getLastUpdate()).isEqualTo(LocalDate.now());

            verify(walletUpdateInvestedBalanceService, org.mockito.Mockito.times(2)).execute(wallet);
        }

        @Test
        @DisplayName("Given saving accounts with current lastUpdate When updating Then skips them")
        void whenCurrentAccounts_skipsThem() {
            // Given - Cajas de ahorro actualizadas
            SavingAccount account1 = InvestmentTestMother.defaultSavingAccount();
            account1.setLastUpdate(LocalDate.now());
            SavingAccount account2 = InvestmentTestMother.savingAccount(902L, "Otra Caja",
                    InvestmentTestMother.AMOUNT_LARGE, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.INTEREST_ACCUMULATED, InvestmentTestMother.defaultWallet());
            account2.setLastUpdate(LocalDate.now());

            List<SavingAccount> accounts = Arrays.asList(account1, account2);

            when(savingAccountRepository.findAllByDeletedAtIsNull()).thenReturn(accounts);

            // When - Ejecutar actualización
            savingAccountUpdateService.cu107updateSavingAccounts();

            // Then - No debe actualizar ni generar transacciones
            verify(savingAccountRepository, never()).save(any(SavingAccount.class));
            verify(walletUpdateInvestedBalanceService, never()).execute(any(Wallet.class));
        }

        @Test
        @DisplayName("Given empty list of saving accounts When updating Then does nothing")
        void whenEmptyList_doesNothing() {
            // Given - Lista vacía
            when(savingAccountRepository.findAllByDeletedAtIsNull()).thenReturn(List.of());

            // When - Ejecutar actualización
            savingAccountUpdateService.cu107updateSavingAccounts();

            // Then - No debe hacer nada
            verify(savingAccountRepository, never()).save(any(SavingAccount.class));
            verify(walletUpdateInvestedBalanceService, never()).execute(any(Wallet.class));
        }
    }
}

