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
class FixedTermDepositFindAllByStateAndWalletServiceTest {

    @Mock
    private IFixedTermDepositRepository fixedTermDepositRepository;

    private FixedTermDepositFindAllByStateAndWalletService fixedTermDepositFindAllByStateAndWalletService;

    @BeforeEach
    void setUp() {
        fixedTermDepositFindAllByStateAndWalletService = new FixedTermDepositFindAllByStateAndWalletService(
            fixedTermDepositRepository
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet and fixed term state When finding all by state and wallet Then returns list of deposits")
        void whenWalletAndState_returnsListOfDeposits() {
            // Given - Wallet y estado
            Wallet wallet = InvestmentTestMother.defaultWallet();
            FixedTermDeposit deposit1 = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    InvestmentTestMother.AMOUNT_MEDIUM + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            FixedTermDeposit deposit2 = InvestmentTestMother.fixedTermDeposit(802L, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.AMOUNT_LARGE + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            List<FixedTermDeposit> expectedDeposits = Arrays.asList(deposit1, deposit2);

            when(fixedTermDepositRepository.findByWalletAndFixedTermState(wallet, FixedTermState.IN_PROGRESS))
                    .thenReturn(expectedDeposits);

            // When - Buscar todos por estado y wallet
            List<FixedTermDeposit> result = fixedTermDepositFindAllByStateAndWalletService
                    .execute(FixedTermState.IN_PROGRESS, wallet);

            // Then - Debe retornar lista de plazos fijos
            // Motivo: Verificar que el servicio filtra correctamente por wallet y estado
            verify(fixedTermDepositRepository).findByWalletAndFixedTermState(wallet, FixedTermState.IN_PROGRESS);
            assertThat(result)
                .hasSize(2)
                .containsExactlyElementsOf(expectedDeposits);
            assertThat(result)
                .extracting(FixedTermDeposit::getWallet)
                .containsOnly(wallet);
            assertThat(result)
                .extracting(FixedTermDeposit::getFixedTermState)
                .containsOnly(FixedTermState.IN_PROGRESS);
        }

        @Test
        @DisplayName("Given wallet with no deposits in state When finding all by state and wallet Then returns empty list")
        void whenNoDepositsInState_returnsEmptyList() {
            // Given - Wallet sin plazos fijos en ese estado
            Wallet wallet = InvestmentTestMother.defaultWallet();
            List<FixedTermDeposit> expectedDeposits = Collections.emptyList();

            when(fixedTermDepositRepository.findByWalletAndFixedTermState(wallet, FixedTermState.FINISHED))
                    .thenReturn(expectedDeposits);

            // When - Buscar todos por estado y wallet
            List<FixedTermDeposit> result = fixedTermDepositFindAllByStateAndWalletService
                    .execute(FixedTermState.FINISHED, wallet);

            // Then - Debe retornar lista vacía
            verify(fixedTermDepositRepository).findByWalletAndFixedTermState(wallet, FixedTermState.FINISHED);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Given different wallets When finding all by state and wallet Then returns only deposits for specified wallet")
        void whenDifferentWallets_returnsOnlyDepositsForSpecifiedWallet() {
            // Given - Diferentes wallets
            Wallet wallet1 = InvestmentTestMother.defaultWallet();
            Wallet wallet2 = InvestmentTestMother.wallet(999L, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.AMOUNT_MEDIUM);
            FixedTermDeposit deposit1 = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    InvestmentTestMother.AMOUNT_MEDIUM + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet1);
            List<FixedTermDeposit> wallet1Deposits = Arrays.asList(deposit1);

            when(fixedTermDepositRepository.findByWalletAndFixedTermState(wallet1, FixedTermState.IN_PROGRESS))
                    .thenReturn(wallet1Deposits);
            when(fixedTermDepositRepository.findByWalletAndFixedTermState(wallet2, FixedTermState.IN_PROGRESS))
                    .thenReturn(Collections.emptyList());

            // When - Buscar por diferentes wallets
            List<FixedTermDeposit> wallet1Result = fixedTermDepositFindAllByStateAndWalletService
                    .execute(FixedTermState.IN_PROGRESS, wallet1);
            List<FixedTermDeposit> wallet2Result = fixedTermDepositFindAllByStateAndWalletService
                    .execute(FixedTermState.IN_PROGRESS, wallet2);

            // Then - Debe retornar solo plazos fijos del wallet especificado
            assertThat(wallet1Result).hasSize(1);
            assertThat(wallet1Result).extracting(FixedTermDeposit::getWallet).containsOnly(wallet1);
            assertThat(wallet2Result).isEmpty();
        }
    }
}

