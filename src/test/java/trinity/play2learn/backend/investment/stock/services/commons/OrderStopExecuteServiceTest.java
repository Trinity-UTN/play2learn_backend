package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
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
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderStop;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindLastService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;

@ExtendWith(MockitoExtension.class)
class OrderStopExecuteServiceTest {

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private IStockCalculateByWalletService stockCalculateByWalletService;

    @Mock
    private IStockHistoryFindLastService stockHistoryFindLastService;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private IStockMoveService stockMoveService;

    @Mock
    private IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    private OrderStopExecuteService orderStopExecuteService;

    @BeforeEach
    void setUp() {
        orderStopExecuteService = new OrderStopExecuteService(
            orderRepository,
            stockCalculateByWalletService,
            stockHistoryFindLastService,
            transactionGenerateService,
            stockMoveService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stock with executable loss stop order When executing stop orders Then executes order and generates transaction")
        void whenExecutableLossStopOrder_executesOrderAndGeneratesTransaction() {
            // Given - Acción con orden stop de pérdida ejecutable
            Stock stock = InvestmentTestMother.defaultStock();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();
            lastHistory.setPrice(InvestmentTestMother.DEFAULT_CURRENT_PRICE - 10.0); // Precio menor al stop
            Order stopOrder = InvestmentTestMother.orderPending(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderStop.LOSS, InvestmentTestMother.DEFAULT_CURRENT_PRICE);
            stopOrder.setWallet(wallet);
            List<Order> orders = Arrays.asList(stopOrder);
            Double totalPrice = stock.getCurrentPrice() * stopOrder.getQuantity().doubleValue();

            when(orderRepository.findByStockAndOrderStateOrderByCreatedAtAsc(stock, OrderState.PENDIENTE))
                    .thenReturn(orders);
            when(stockHistoryFindLastService.execute(stock)).thenReturn(lastHistory);
            when(stockCalculateByWalletService.execute(stock, wallet))
                    .thenReturn(InvestmentTestMother.DEFAULT_QUANTITY);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When - Ejecutar órdenes stop
            orderStopExecuteService.execute(stock);

            // Then - Debe ejecutar orden y generar transacción
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());

            Order capturedOrder = orderCaptor.getValue();
            assertThat(capturedOrder.getOrderState()).isEqualTo(OrderState.EJECUTADA);
            assertThat(capturedOrder.getPricePerUnit()).isEqualTo(lastHistory.getPrice());

            verify(transactionGenerateService).generate(eq(TypeTransaction.STOCK), eq(totalPrice),
                    eq("Venta de acciones"), eq(TransactionActor.SISTEMA), eq(TransactionActor.ESTUDIANTE),
                    eq(wallet), isNull(), isNull(), isNull(), eq(capturedOrder), isNull(), isNull());
            verify(stockMoveService).toAvailable(stock, stopOrder.getQuantity());
            verify(walletUpdateInvestedBalanceService).execute(wallet);
        }

        @Test
        @DisplayName("Given stock with non-executable loss stop order When executing stop orders Then skips order")
        void whenNonExecutableLossStopOrder_skipsOrder() {
            // Given - Acción con orden stop de pérdida no ejecutable
            Stock stock = InvestmentTestMother.defaultStock();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();
            lastHistory.setPrice(InvestmentTestMother.DEFAULT_CURRENT_PRICE + 10.0); // Precio mayor al stop
            Order stopOrder = InvestmentTestMother.orderPending(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderStop.LOSS, InvestmentTestMother.DEFAULT_CURRENT_PRICE);
            stopOrder.setWallet(wallet);
            List<Order> orders = Arrays.asList(stopOrder);

            when(orderRepository.findByStockAndOrderStateOrderByCreatedAtAsc(stock, OrderState.PENDIENTE))
                    .thenReturn(orders);
            when(stockHistoryFindLastService.execute(stock)).thenReturn(lastHistory);

            // When - Ejecutar órdenes stop
            orderStopExecuteService.execute(stock);

            // Then - No debe ejecutar orden ni generar transacción
            verify(orderRepository, never()).save(any(Order.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any());
            verify(stockMoveService, never()).toAvailable(any(Stock.class), any(BigInteger.class));
            verify(walletUpdateInvestedBalanceService, never()).execute(any(Wallet.class));
        }

        @Test
        @DisplayName("Given stock with no pending stop orders When executing stop orders Then does nothing")
        void whenNoPendingStopOrders_doesNothing() {
            // Given - Sin órdenes stop pendientes
            Stock stock = InvestmentTestMother.defaultStock();
            List<Order> orders = Collections.emptyList();

            when(orderRepository.findByStockAndOrderStateOrderByCreatedAtAsc(stock, OrderState.PENDIENTE))
                    .thenReturn(orders);

            // When - Ejecutar órdenes stop
            orderStopExecuteService.execute(stock);

            // Then - No debe hacer nada
            verify(stockHistoryFindLastService, never()).execute(any(Stock.class));
            verify(orderRepository, never()).save(any(Order.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Given stock with order having insufficient quantity When executing stop orders Then cancels order")
        void whenInsufficientQuantity_cancelsOrder() {
            // Given - Acción con orden con cantidad insuficiente
            Stock stock = InvestmentTestMother.defaultStock();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();
            lastHistory.setPrice(InvestmentTestMother.DEFAULT_CURRENT_PRICE - 10.0);
            Order stopOrder = InvestmentTestMother.orderPending(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderStop.LOSS, InvestmentTestMother.DEFAULT_CURRENT_PRICE);
            stopOrder.setWallet(wallet);
            List<Order> orders = Arrays.asList(stopOrder);

            when(orderRepository.findByStockAndOrderStateOrderByCreatedAtAsc(stock, OrderState.PENDIENTE))
                    .thenReturn(orders);
            when(stockHistoryFindLastService.execute(stock)).thenReturn(lastHistory);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(BigInteger.valueOf(5));

            // When - Ejecutar órdenes stop
            orderStopExecuteService.execute(stock);

            // Then - Debe cancelar orden
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());

            Order capturedOrder = orderCaptor.getValue();
            assertThat(capturedOrder.getOrderState()).isEqualTo(OrderState.CANCELADA);

            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any());
            verify(stockMoveService, never()).toAvailable(any(Stock.class), any(BigInteger.class));
        }
    }
}

