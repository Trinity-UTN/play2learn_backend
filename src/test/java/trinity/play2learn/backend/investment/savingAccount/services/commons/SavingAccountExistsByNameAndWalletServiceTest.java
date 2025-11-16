package trinity.play2learn.backend.investment.savingAccount.services.commons;

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
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;

@ExtendWith(MockitoExtension.class)
class SavingAccountExistsByNameAndWalletServiceTest {

    @Mock
    private ISavingAccountRepository savingAccountRepository;

    private SavingAccountExistsByNameAndWalletService savingAccountExistsByNameAndWalletService;

    @BeforeEach
    void setUp() {
        savingAccountExistsByNameAndWalletService = new SavingAccountExistsByNameAndWalletService(
            savingAccountRepository
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given existing saving account name and wallet When checking existence Then returns true")
        void whenExistingNameAndWallet_returnsTrue() {
            // Given - Nombre y wallet existentes
            String name = InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME;
            Wallet wallet = InvestmentTestMother.defaultWallet();

            when(savingAccountRepository.existsByNameAndWalletAndDeletedAtIsNull(name, wallet)).thenReturn(true);

            // When - Verificar existencia
            boolean result = savingAccountExistsByNameAndWalletService.execute(name, wallet);

            // Then - Debe retornar true
            verify(savingAccountRepository).existsByNameAndWalletAndDeletedAtIsNull(name, wallet);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Given non-existing saving account name and wallet When checking existence Then returns false")
        void whenNonExistingNameAndWallet_returnsFalse() {
            // Given - Nombre y wallet no existentes
            String name = "Cuenta Inexistente";
            Wallet wallet = InvestmentTestMother.defaultWallet();

            when(savingAccountRepository.existsByNameAndWalletAndDeletedAtIsNull(name, wallet)).thenReturn(false);

            // When - Verificar existencia
            boolean result = savingAccountExistsByNameAndWalletService.execute(name, wallet);

            // Then - Debe retornar false
            verify(savingAccountRepository).existsByNameAndWalletAndDeletedAtIsNull(name, wallet);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Given same name but different wallet When checking existence Then returns false")
        void whenSameNameDifferentWallet_returnsFalse() {
            // Given - Mismo nombre pero wallet diferente
            String name = InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME;
            Wallet wallet1 = InvestmentTestMother.defaultWallet();
            Wallet wallet2 = InvestmentTestMother.wallet(999L, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.AMOUNT_MEDIUM);

            when(savingAccountRepository.existsByNameAndWalletAndDeletedAtIsNull(name, wallet1)).thenReturn(true);
            when(savingAccountRepository.existsByNameAndWalletAndDeletedAtIsNull(name, wallet2)).thenReturn(false);

            // When - Verificar existencia con diferentes wallets
            boolean result1 = savingAccountExistsByNameAndWalletService.execute(name, wallet1);
            boolean result2 = savingAccountExistsByNameAndWalletService.execute(name, wallet2);

            // Then - Debe retornar resultados diferentes según el wallet
            assertThat(result1).isTrue();
            assertThat(result2).isFalse();
        }
    }
}

