package trinity.play2learn.backend.investment.fixedTermDeposit.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositFindAllByStateService;

@ExtendWith(MockitoExtension.class)
class FixedTermDepositAutomaticEndsServiceTest {

    @Mock
    private IFixedTermDepositFindAllByStateService fixedTermDepositFindAllByStateService;

    @Mock
    private IFixedTermDepositRepository fixedTermDepositRepository;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    private FixedTermDepositAutomaticEndsService fixedTermDepositAutomaticEndsService;

    @BeforeEach
    void setUp() {
        fixedTermDepositAutomaticEndsService = new FixedTermDepositAutomaticEndsService(
            fixedTermDepositFindAllByStateService,
            fixedTermDepositRepository,
            transactionGenerateService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu95fixedTermDepositAutomaticEnds")
    class Cu95fixedTermDepositAutomaticEnds {

        @Test
        @DisplayName("Given expired fixed term deposits When executing automatic ends Then finishes them and generates transactions")
        void whenExpiredDeposits_finishesThemAndGeneratesTransactions() {
            // Given - Plazos fijos vencidos
            Wallet wallet = InvestmentTestMother.defaultWallet();
            FixedTermDeposit expiredDeposit1 = InvestmentTestMother.fixedTermDepositInProgress(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    LocalDate.now().minusDays(1));
            FixedTermDeposit expiredDeposit2 = InvestmentTestMother.fixedTermDepositInProgress(802L,
                    InvestmentTestMother.AMOUNT_LARGE, LocalDate.now());
            List<FixedTermDeposit> expiredDeposits = Arrays.asList(expiredDeposit1, expiredDeposit2);

            when(fixedTermDepositFindAllByStateService.execute(FixedTermState.IN_PROGRESS))
                    .thenReturn(expiredDeposits);
            when(fixedTermDepositRepository.save(any(FixedTermDeposit.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When - Ejecutar finalización automática
            fixedTermDepositAutomaticEndsService.cu95fixedTermDepositAutomaticEnds();

            // Then - Debe finalizar ambos plazos fijos y generar transacciones
            ArgumentCaptor<FixedTermDeposit> depositCaptor = ArgumentCaptor.forClass(FixedTermDeposit.class);
            verify(fixedTermDepositRepository, org.mockito.Mockito.times(2)).save(depositCaptor.capture());

            List<FixedTermDeposit> capturedDeposits = depositCaptor.getAllValues();
            org.assertj.core.api.Assertions.assertThat(capturedDeposits).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(capturedDeposits.get(0).getFixedTermState())
                    .isEqualTo(FixedTermState.FINISHED);
            org.assertj.core.api.Assertions.assertThat(capturedDeposits.get(1).getFixedTermState())
                    .isEqualTo(FixedTermState.FINISHED);

            verify(transactionGenerateService, org.mockito.Mockito.times(2)).generate(
                    eq(TypeTransaction.PLAZO_FIJO), any(Double.class), eq("Inversion en plazo fijo, retorno."),
                    eq(TransactionActor.SISTEMA), eq(TransactionActor.ESTUDIANTE), eq(wallet), eq(null), eq(null),
                    eq(null), eq(null), any(FixedTermDeposit.class), eq(null));
            verify(walletUpdateInvestedBalanceService, org.mockito.Mockito.times(2)).execute(wallet);
        }

        @Test
        @DisplayName("Given fixed term deposits not yet expired When executing automatic ends Then skips them")
        void whenNotExpiredDeposits_skipsThem() {
            // Given - Plazos fijos aún no vencidos
            FixedTermDeposit activeDeposit1 = InvestmentTestMother.fixedTermDepositInProgress(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    LocalDate.now().plusDays(1));
            FixedTermDeposit activeDeposit2 = InvestmentTestMother.fixedTermDepositInProgress(802L,
                    InvestmentTestMother.AMOUNT_LARGE, LocalDate.now().plusDays(5));
            List<FixedTermDeposit> activeDeposits = Arrays.asList(activeDeposit1, activeDeposit2);

            when(fixedTermDepositFindAllByStateService.execute(FixedTermState.IN_PROGRESS))
                    .thenReturn(activeDeposits);

            // When - Ejecutar finalización automática
            fixedTermDepositAutomaticEndsService.cu95fixedTermDepositAutomaticEnds();

            // Then - No debe finalizar ni generar transacciones
            verify(fixedTermDepositRepository, never()).save(any(FixedTermDeposit.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any());
            verify(walletUpdateInvestedBalanceService, never()).execute(any(Wallet.class));
        }

        @Test
        @DisplayName("Given mixed expired and active deposits When executing automatic ends Then only finishes expired ones")
        void whenMixedDeposits_finishesOnlyExpiredOnes() {
            // Given - Mezcla de plazos fijos vencidos y activos
            Wallet wallet = InvestmentTestMother.defaultWallet();
            FixedTermDeposit expiredDeposit = InvestmentTestMother.fixedTermDepositInProgress(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, InvestmentTestMother.AMOUNT_MEDIUM,
                    LocalDate.now().minusDays(1));
            FixedTermDeposit activeDeposit = InvestmentTestMother.fixedTermDepositInProgress(802L,
                    InvestmentTestMother.AMOUNT_LARGE, LocalDate.now().plusDays(1));
            List<FixedTermDeposit> mixedDeposits = Arrays.asList(expiredDeposit, activeDeposit);

            when(fixedTermDepositFindAllByStateService.execute(FixedTermState.IN_PROGRESS))
                    .thenReturn(mixedDeposits);
            when(fixedTermDepositRepository.save(any(FixedTermDeposit.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When - Ejecutar finalización automática
            fixedTermDepositAutomaticEndsService.cu95fixedTermDepositAutomaticEnds();

            // Then - Debe finalizar solo el vencido
            ArgumentCaptor<FixedTermDeposit> depositCaptor = ArgumentCaptor.forClass(FixedTermDeposit.class);
            verify(fixedTermDepositRepository).save(depositCaptor.capture());

            FixedTermDeposit capturedDeposit = depositCaptor.getValue();
            org.assertj.core.api.Assertions.assertThat(capturedDeposit.getFixedTermState())
                    .isEqualTo(FixedTermState.FINISHED);
            org.assertj.core.api.Assertions.assertThat(capturedDeposit.getId()).isEqualTo(expiredDeposit.getId());

            verify(transactionGenerateService).generate(eq(TypeTransaction.PLAZO_FIJO), any(Double.class),
                    eq("Inversion en plazo fijo, retorno."), eq(TransactionActor.SISTEMA),
                    eq(TransactionActor.ESTUDIANTE), eq(wallet), eq(null), eq(null), eq(null), eq(null),
                    any(FixedTermDeposit.class), eq(null));
            verify(walletUpdateInvestedBalanceService).execute(wallet);
        }

        @Test
        @DisplayName("Given empty list of fixed term deposits When executing automatic ends Then does nothing")
        void whenEmptyList_doesNothing() {
            // Given - Lista vacía
            when(fixedTermDepositFindAllByStateService.execute(FixedTermState.IN_PROGRESS))
                    .thenReturn(List.of());

            // When - Ejecutar finalización automática
            fixedTermDepositAutomaticEndsService.cu95fixedTermDepositAutomaticEnds();

            // Then - No debe hacer nada
            verify(fixedTermDepositRepository, never()).save(any(FixedTermDeposit.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any());
            verify(walletUpdateInvestedBalanceService, never()).execute(any(Wallet.class));
        }
    }
}

