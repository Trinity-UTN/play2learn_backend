package trinity.play2learn.backend.economy.wallet.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.investment.investment.services.interfaces.IInvestmentCalculateTotalInvestedService;

@ExtendWith(MockitoExtension.class)
class WalletUpdateInvestedBalanceServiceTest {

    @Mock
    private IWalletRepository walletRepository;

    @Mock
    private IInvestmentCalculateTotalInvestedService investmentCalculateTotalInvestedService;

    private WalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    @BeforeEach
    void setUp() {
        walletUpdateInvestedBalanceService = new WalletUpdateInvestedBalanceService(
            walletRepository,
            investmentCalculateTotalInvestedService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet When updating invested balance Then calculates total invested and updates wallet")
        void whenWallet_updatesInvestedBalance() {
            // Given - Wallet con balance inicial y servicio de inversión mockeado
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double totalInvested = 750.0;

            when(investmentCalculateTotalInvestedService.cu110calculateTotalInvested(wallet)).thenReturn(totalInvested);
            when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

            // When - Actualizar balance invertido
            walletUpdateInvestedBalanceService.execute(wallet);

            // Then - Debe calcular total invertido y actualizar wallet
            ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
            verify(investmentCalculateTotalInvestedService).cu110calculateTotalInvested(wallet);
            verify(walletRepository).save(walletCaptor.capture());

            Wallet capturedWallet = walletCaptor.getValue();
            assertThat(capturedWallet.getInvertedBalance()).isEqualTo(totalInvested);
        }

        @Test
        @DisplayName("Given wallet with zero invested When updating invested balance Then sets inverted balance to zero")
        void whenWalletWithZeroInvested_setsInvertedBalanceToZero() {
            // Given - Wallet sin inversiones
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double totalInvested = 0.0;

            when(investmentCalculateTotalInvestedService.cu110calculateTotalInvested(wallet)).thenReturn(totalInvested);
            when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

            // When - Actualizar balance invertido
            walletUpdateInvestedBalanceService.execute(wallet);

            // Then - Debe establecer balance invertido en cero
            ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
            verify(walletRepository).save(walletCaptor.capture());

            Wallet capturedWallet = walletCaptor.getValue();
            assertThat(capturedWallet.getInvertedBalance()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Given wallet with investments When updating invested balance Then updates inverted balance correctly")
        void whenWalletWithInvestments_updatesInvertedBalance() {
            // Given - Wallet con inversiones
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double totalInvested = 1250.0;

            when(investmentCalculateTotalInvestedService.cu110calculateTotalInvested(wallet)).thenReturn(totalInvested);
            when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

            // When - Actualizar balance invertido
            walletUpdateInvestedBalanceService.execute(wallet);

            // Then - Debe actualizar balance invertido correctamente
            ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
            verify(walletRepository).save(walletCaptor.capture());

            Wallet capturedWallet = walletCaptor.getValue();
            assertThat(capturedWallet.getInvertedBalance()).isEqualTo(totalInvested);
        }
    }
}

