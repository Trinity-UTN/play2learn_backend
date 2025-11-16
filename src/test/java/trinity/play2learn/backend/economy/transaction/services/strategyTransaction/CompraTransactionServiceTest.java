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

import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;

@ExtendWith(MockitoExtension.class)
class CompraTransactionServiceTest {

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private IWalletRemoveAmountService removeAmountWalletService;

    @Mock
    private IReserveModifyService modifyReserveService;

    @Mock
    private IReserveFindLastService findLastReserveService;

    private CompraTransactionService compraTransactionService;

    @BeforeEach
    void setUp() {
        compraTransactionService = new CompraTransactionService(
            transaccionRepository,
            removeAmountWalletService,
            modifyReserveService,
            findLastReserveService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with sufficient balance When executing compra transaction Then creates transaction, removes amount from wallet and moves to reserve")
        void whenWalletHasSufficientBalance_createsTransactionRemovesAmountAndMovesToReserve() {
            // Given - Wallet con balance suficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 1000.0);
            Double amount = 100.0;
            String description = "Compra de beneficio";
            TransactionActor origin = TransactionActor.ESTUDIANTE;
            TransactionActor destination = TransactionActor.SISTEMA;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
            when(removeAmountWalletService.execute(wallet, amount)).thenReturn(wallet);
            when(modifyReserveService.moveToReserve(amount, reserve)).thenReturn(reserve);

            // When - Ejecutar transacción de compra
            Transaction result = compraTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null, null
            );

            // Then - Debe crear transacción, remover monto del wallet y mover a reserva
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(removeAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToReserve(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given wallet with insufficient balance When executing compra transaction Then throws IllegalArgumentException")
        void whenWalletHasInsufficientBalance_throwsIllegalArgumentException() {
            // Given - Wallet con balance insuficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 50.0);
            Double amount = 100.0;

            // When & Then - Debe lanzar IllegalArgumentException
            assertThatThrownBy(() -> compraTransactionService.execute(
                amount,
                "Compra de beneficio",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null, null, null, null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_STUDENT);
        }

        @Test
        @DisplayName("Given wallet with balance equal to amount When executing compra transaction Then creates transaction successfully")
        void whenWalletBalanceEqualsAmount_createsTransactionSuccessfully() {
            // Given - Wallet con balance igual al monto
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 100.0);
            Double amount = 100.0;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
            when(removeAmountWalletService.execute(wallet, amount)).thenReturn(wallet);
            when(modifyReserveService.moveToReserve(amount, reserve)).thenReturn(reserve);

            // When - Ejecutar transacción de compra
            Transaction result = compraTransactionService.execute(
                amount,
                "Compra de beneficio",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null, null, null, null
            );

            // Then - Debe crear transacción exitosamente
            assertThat(result).isEqualTo(savedTransaction);
        }
    }
}

