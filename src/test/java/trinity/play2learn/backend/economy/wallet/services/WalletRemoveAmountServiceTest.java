package trinity.play2learn.backend.economy.wallet.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import trinity.play2learn.backend.configs.messages.EconomyMessages;

@ExtendWith(MockitoExtension.class)
class WalletRemoveAmountServiceTest {

    @Mock
    private IWalletRepository walletRepository;

    private WalletRemoveAmountService walletRemoveAmountService;

    @BeforeEach
    void setUp() {
        walletRemoveAmountService = new WalletRemoveAmountService(walletRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with sufficient balance and amount When removing amount Then decrements balance and persists wallet")
        void whenWalletHasSufficientBalance_decrementsBalanceAndPersists() {
            // Given - Wallet con balance suficiente y monto a remover
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, EconomyTestMother.AMOUNT_LARGE);
            Double amount = EconomyTestMother.AMOUNT_MEDIUM;
            Double expectedBalance = wallet.getBalance() - amount;

            Wallet savedWallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, expectedBalance);
            when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

            // When - Remover monto del wallet
            Wallet result = walletRemoveAmountService.execute(wallet, amount);

            // Then - Debe decrementar balance y persistir wallet
            ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
            verify(walletRepository).save(walletCaptor.capture());

            Wallet capturedWallet = walletCaptor.getValue();
            assertThat(capturedWallet.getBalance()).isEqualTo(expectedBalance);
            assertThat(result.getBalance()).isEqualTo(expectedBalance);
        }

        @Test
        @DisplayName("Given wallet with balance equal to amount When removing amount Then decrements balance to zero and persists wallet")
        void whenWalletBalanceEqualsAmount_decrementsToZero() {
            // Given - Wallet con balance igual al monto a remover
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, EconomyTestMother.AMOUNT_MEDIUM);
            Double amount = EconomyTestMother.AMOUNT_MEDIUM;
            Double expectedBalance = EconomyTestMother.AMOUNT_ZERO;

            Wallet savedWallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, expectedBalance);
            when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

            // When - Remover monto igual al balance
            Wallet result = walletRemoveAmountService.execute(wallet, amount);

            // Then - Debe decrementar balance a cero y persistir wallet
            assertThat(result.getBalance()).isEqualTo(expectedBalance);
        }

        @Test
        @DisplayName("Given wallet with insufficient balance When removing amount Then throws IllegalArgumentException and does not persist")
        void whenWalletHasInsufficientBalance_throwsIllegalArgumentException() {
            // Given - Wallet con balance insuficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, EconomyTestMother.AMOUNT_MEDIUM);
            Double amount = EconomyTestMother.AMOUNT_LARGE * 2;

            // When & Then - Debe lanzar IllegalArgumentException con mensaje NOT_ENOUGH_WALLET_MONEY_STUDENT
            assertThatThrownBy(() -> walletRemoveAmountService.execute(wallet, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_STUDENT);

            verify(walletRepository, never()).save(any(Wallet.class));
        }

        @Test
        @DisplayName("Given wallet with balance less than amount When removing amount Then throws IllegalArgumentException")
        void whenWalletBalanceLessThanAmount_throwsIllegalArgumentException() {
            // Given - Wallet con balance menor al monto a remover
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, EconomyTestMother.AMOUNT_SMALL);
            Double amount = EconomyTestMother.AMOUNT_MEDIUM;

            // When & Then - Debe lanzar IllegalArgumentException con mensaje NOT_ENOUGH_WALLET_MONEY_STUDENT
            assertThatThrownBy(() -> walletRemoveAmountService.execute(wallet, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_STUDENT);

            verify(walletRepository, never()).save(any(Wallet.class));
        }
    }
}

