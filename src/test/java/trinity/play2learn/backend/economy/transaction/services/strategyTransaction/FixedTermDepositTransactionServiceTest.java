package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;

@ExtendWith(MockitoExtension.class)
class FixedTermDepositTransactionServiceTest {

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private IWalletRemoveAmountService removeAmountWalletService;

    @Mock
    private IReserveModifyService modifyReserveService;

    @Mock
    private IReserveFindLastService findLastReserveService;

    @Mock
    private IWalletAddAmountService addAmountWalletService;

    private FixedTermDepositTransactionService fixedTermDepositTransactionService;

    @BeforeEach
    void setUp() {
        fixedTermDepositTransactionService = new FixedTermDepositTransactionService(
            transaccionRepository,
            removeAmountWalletService,
            modifyReserveService,
            findLastReserveService,
            addAmountWalletService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given fixed term deposit with IN_PROGRESS state and wallet with sufficient balance When executing transaction Then creates transaction, removes from wallet and moves to reserve")
        void whenFixedTermDepositInProgressWithSufficientBalance_createsTransactionRemovesFromWalletAndMovesToReserve() {
            // Given - Plazo fijo en estado IN_PROGRESS con wallet con balance suficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 1000.0);
            FixedTermDeposit fixedTermDeposit = EconomyTestMother.fixedTermDepositInProgress();
            Double amount = 100.0;
            String description = "Inversion en plazo fijo.";
            TransactionActor origin = TransactionActor.ESTUDIANTE;
            TransactionActor destination = TransactionActor.SISTEMA;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
            when(removeAmountWalletService.execute(wallet, amount)).thenReturn(wallet);
            when(modifyReserveService.moveToReserve(amount, reserve)).thenReturn(reserve);

            // When - Ejecutar transacción de plazo fijo
            Transaction result = fixedTermDepositTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null,
                fixedTermDeposit, null
            );

            // Then - Debe crear transacción, remover del wallet y mover a reserva
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(removeAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToReserve(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given fixed term deposit with FINISHED state When executing transaction Then creates transaction, adds to wallet and moves to circulation")
        void whenFixedTermDepositFinished_createsTransactionAddsToWalletAndMovesToCirculation() {
            // Given - Plazo fijo en estado FINISHED
            Wallet wallet = EconomyTestMother.defaultWallet();
            FixedTermDeposit fixedTermDeposit = EconomyTestMother.fixedTermDepositFinished();
            Double amount = 100.0;
            String description = "Vencimiento de plazo fijo";
            TransactionActor origin = TransactionActor.SISTEMA;
            TransactionActor destination = TransactionActor.ESTUDIANTE;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de plazo fijo finalizado
            Transaction result = fixedTermDepositTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null,
                fixedTermDeposit, null
            );

            // Then - Debe crear transacción, agregar al wallet y mover a circulación
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(addAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToCirculation(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given fixed term deposit with IN_PROGRESS state and wallet with insufficient balance When executing transaction Then throws BadRequestException")
        void whenFixedTermDepositInProgressWithInsufficientBalance_throwsBadRequestException() {
            // Given - Plazo fijo en estado IN_PROGRESS con wallet con balance insuficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 50.0);
            FixedTermDeposit fixedTermDeposit = EconomyTestMother.fixedTermDepositInProgress();
            Double amount = 100.0;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> fixedTermDepositTransactionService.execute(
                amount,
                "Inversion en plazo fijo.",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null, null,
                fixedTermDeposit, null
            ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El wallet no cuenta con el balance suficiente para realizar el plazo fijo");
        }
    }
}

