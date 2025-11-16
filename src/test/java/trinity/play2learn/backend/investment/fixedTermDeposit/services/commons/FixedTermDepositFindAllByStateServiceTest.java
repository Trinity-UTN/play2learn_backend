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
class FixedTermDepositFindAllByStateServiceTest {

    @Mock
    private IFixedTermDepositRepository fixedTermDepositRepository;

    private FixedTermDepositFindAllByStateService fixedTermDepositFindAllByStateService;

    @BeforeEach
    void setUp() {
        fixedTermDepositFindAllByStateService = new FixedTermDepositFindAllByStateService(
            fixedTermDepositRepository
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given fixed term state When finding all by state Then returns list of deposits with that state")
        void whenFixedTermState_returnsListOfDepositsWithState() {
            // Given - Estado de plazo fijo
            Wallet wallet = InvestmentTestMother.defaultWallet();
            FixedTermDeposit deposit1 = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    InvestmentTestMother.AMOUNT_MEDIUM + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            FixedTermDeposit deposit2 = InvestmentTestMother.fixedTermDeposit(802L, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.AMOUNT_LARGE + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            List<FixedTermDeposit> expectedDeposits = Arrays.asList(deposit1, deposit2);

            when(fixedTermDepositRepository.findByFixedTermState(FixedTermState.IN_PROGRESS))
                    .thenReturn(expectedDeposits);

            // When - Buscar todos por estado
            List<FixedTermDeposit> result = fixedTermDepositFindAllByStateService.execute(FixedTermState.IN_PROGRESS);

            // Then - Debe retornar lista de plazos fijos con ese estado
            // Motivo: Verificar que el servicio filtra correctamente por estado
            verify(fixedTermDepositRepository).findByFixedTermState(FixedTermState.IN_PROGRESS);
            assertThat(result)
                .hasSize(2)
                .containsExactlyElementsOf(expectedDeposits)
                .extracting(FixedTermDeposit::getFixedTermState)
                .containsOnly(FixedTermState.IN_PROGRESS);
        }

        @Test
        @DisplayName("Given fixed term state with no deposits When finding all by state Then returns empty list")
        void whenNoDepositsWithState_returnsEmptyList() {
            // Given - Estado sin plazos fijos
            List<FixedTermDeposit> expectedDeposits = Collections.emptyList();

            when(fixedTermDepositRepository.findByFixedTermState(FixedTermState.FINISHED))
                    .thenReturn(expectedDeposits);

            // When - Buscar todos por estado
            List<FixedTermDeposit> result = fixedTermDepositFindAllByStateService.execute(FixedTermState.FINISHED);

            // Then - Debe retornar lista vacía
            verify(fixedTermDepositRepository).findByFixedTermState(FixedTermState.FINISHED);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Given different fixed term states When finding all by state Then returns only deposits with specified state")
        void whenDifferentStates_returnsOnlyDepositsWithSpecifiedState() {
            // Given - Diferentes estados
            Wallet wallet = InvestmentTestMother.defaultWallet();
            FixedTermDeposit inProgressDeposit = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    InvestmentTestMother.AMOUNT_MEDIUM + InvestmentTestMother.INTEREST_MEDIUM, FixedTermDays.MENSUAL,
                    FixedTermState.IN_PROGRESS, wallet);
            List<FixedTermDeposit> inProgressDeposits = Arrays.asList(inProgressDeposit);

            when(fixedTermDepositRepository.findByFixedTermState(FixedTermState.IN_PROGRESS))
                    .thenReturn(inProgressDeposits);
            when(fixedTermDepositRepository.findByFixedTermState(FixedTermState.FINISHED))
                    .thenReturn(Collections.emptyList());

            // When - Buscar por diferentes estados
            List<FixedTermDeposit> inProgressResult = fixedTermDepositFindAllByStateService
                    .execute(FixedTermState.IN_PROGRESS);
            List<FixedTermDeposit> finishedResult = fixedTermDepositFindAllByStateService.execute(FixedTermState.FINISHED);

            // Then - Debe retornar solo plazos fijos con el estado especificado
            assertThat(inProgressResult).hasSize(1);
            assertThat(inProgressResult).extracting(FixedTermDeposit::getFixedTermState)
                    .containsOnly(FixedTermState.IN_PROGRESS);
            assertThat(finishedResult).isEmpty();
        }
    }
}

