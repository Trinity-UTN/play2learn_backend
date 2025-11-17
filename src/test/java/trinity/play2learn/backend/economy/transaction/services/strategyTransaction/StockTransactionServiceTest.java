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
import trinity.play2learn.backend.investment.stock.models.Order;

@ExtendWith(MockitoExtension.class)
class StockTransactionServiceTest {

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

    private StockTransactionService stockTransactionService;

    @BeforeEach
    void setUp() {
        stockTransactionService = new StockTransactionService(
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
        @DisplayName("Given order with COMPRA type and wallet with sufficient balance When executing stock transaction Then creates transaction, removes from wallet and moves to reserve")
        void whenOrderCompraTypeWithSufficientBalance_createsTransactionRemovesFromWalletAndMovesToReserve() {
            // Given - Orden de tipo COMPRA con wallet con balance suficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 1000.0);
            Order order = EconomyTestMother.orderCompra();
            Double amount = 100.0;
            String description = "Compra de acciones";
            TransactionActor origin = TransactionActor.ESTUDIANTE;
            TransactionActor destination = TransactionActor.SISTEMA;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
            when(removeAmountWalletService.execute(wallet, amount)).thenReturn(wallet);
            when(modifyReserveService.moveToReserve(amount, reserve)).thenReturn(reserve);

            // When - Ejecutar transacción de compra de acciones
            Transaction result = stockTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null,
                order, null, null
            );

            // Then - Debe crear transacción, remover del wallet y mover a reserva
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(removeAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToReserve(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given order with VENTA type When executing stock transaction Then creates transaction, adds to wallet and moves to circulation")
        void whenOrderVentaType_createsTransactionAddsToWalletAndMovesToCirculation() {
            // Given - Orden de tipo VENTA
            Wallet wallet = EconomyTestMother.defaultWallet();
            Order order = EconomyTestMother.orderVenta();
            Double amount = 100.0;
            String description = "Venta de acciones";
            TransactionActor origin = TransactionActor.SISTEMA;
            TransactionActor destination = TransactionActor.ESTUDIANTE;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de venta de acciones
            Transaction result = stockTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null,
                order, null, null
            );

            // Then - Debe crear transacción, agregar al wallet y mover a circulación
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(addAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToCirculation(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given order with COMPRA type and wallet with insufficient balance When executing stock transaction Then throws BadRequestException")
        void whenOrderCompraTypeWithInsufficientBalance_throwsBadRequestException() {
            // Given - Orden de tipo COMPRA con wallet con balance insuficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 50.0);
            Order order = EconomyTestMother.orderCompra();
            Double amount = 100.0;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> stockTransactionService.execute(
                amount,
                "Compra de acciones",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null,
                order, null, null
            ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El wallet no cuenta con el balance suficiente para realizar la compra de acciones");
        }
    }
}

