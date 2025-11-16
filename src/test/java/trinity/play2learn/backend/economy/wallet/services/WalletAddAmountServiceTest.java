package trinity.play2learn.backend.economy.wallet.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import trinity.play2learn.backend.configs.messages.EconomyMessages;

@ExtendWith(MockitoExtension.class)
class WalletAddAmountServiceTest {

    @Mock
    private IWalletRepository walletRepository;

    private WalletAddAmountService walletAddAmountService;

    @BeforeEach
    void setUp() {
        walletAddAmountService = new WalletAddAmountService(walletRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet and positive amount When adding amount Then increments balance and persists wallet")
        void whenWalletAndPositiveAmount_incrementsBalanceAndPersists() {
            // Given - Wallet con balance inicial y monto positivo
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, EconomyTestMother.AMOUNT_LARGE);
            Double amount = EconomyTestMother.AMOUNT_MEDIUM;
            Double expectedBalance = wallet.getBalance() + amount;

            Wallet savedWallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, expectedBalance);
            when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

            // When - Agregar monto al wallet
            Wallet result = walletAddAmountService.execute(wallet, amount);

            // Then - Debe incrementar balance y persistir wallet
            ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
            verify(walletRepository).save(walletCaptor.capture());

            Wallet capturedWallet = walletCaptor.getValue();
            assertThat(capturedWallet.getBalance()).isEqualTo(expectedBalance);
            assertThat(result.getBalance()).isEqualTo(expectedBalance);
        }

        @Test
        @DisplayName("Given wallet and zero amount When adding amount Then throws IllegalArgumentException")
        void whenAmountIsZero_throwsIllegalArgumentException() {
            // Given - Wallet con balance inicial y monto cero
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double amount = EconomyTestMother.AMOUNT_ZERO;

            // When & Then - Debe lanzar IllegalArgumentException con mensaje AMOUNT_MAJOR_TO_0
            assertThatThrownBy(() -> walletAddAmountService.execute(wallet, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EconomyMessages.AMOUNT_MAJOR_TO_0);

            // Verify - No debe llamar al repositorio cuando hay excepción
            verifyNoInteractions(walletRepository);
        }

        @Test
        @DisplayName("Given wallet and negative amount When adding amount Then throws IllegalArgumentException")
        void whenAmountIsNegative_throwsIllegalArgumentException() {
            // Given - Wallet con balance inicial y monto negativo
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double amount = EconomyTestMother.AMOUNT_NEGATIVE;

            // When & Then - Debe lanzar IllegalArgumentException con mensaje AMOUNT_MAJOR_TO_0
            assertThatThrownBy(() -> walletAddAmountService.execute(wallet, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EconomyMessages.AMOUNT_MAJOR_TO_0);

            // Verify - No debe llamar al repositorio cuando hay excepción
            verifyNoInteractions(walletRepository);
        }

        @Test
        @DisplayName("Given wallet and minimum amount (0.01) When adding amount Then increments balance successfully")
        void whenAmountIsMinimum_incrementsBalanceSuccessfully() {
            // Given - Wallet con balance inicial y monto mínimo permitido
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, EconomyTestMother.AMOUNT_LARGE);
            Double amount = EconomyTestMother.AMOUNT_MINIMUM;
            Double expectedBalance = wallet.getBalance() + amount;

            Wallet savedWallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, expectedBalance);
            when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

            // When - Agregar monto mínimo al wallet
            Wallet result = walletAddAmountService.execute(wallet, amount);

            // Then - Debe incrementar balance y persistir wallet
            assertThat(result.getBalance()).isEqualTo(expectedBalance);
        }
    }
}

